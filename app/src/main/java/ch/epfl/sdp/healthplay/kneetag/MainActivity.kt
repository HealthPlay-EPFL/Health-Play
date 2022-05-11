package ch.epfl.sdp.healthplay.kneetag

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import ch.epfl.sdp.healthplay.R
import ch.epfl.sdp.healthplay.database.Database
import ch.epfl.sdp.healthplay.kneetag.camera.CameraSource
import ch.epfl.sdp.healthplay.kneetag.data.Device
import ch.epfl.sdp.healthplay.kneetag.ml.PoseClassifier
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.ml.MoveNetMultiPose
import org.tensorflow.lite.examples.poseestimation.ml.Type


class MainActivity : AppCompatActivity(),
    CompoundButton.OnCheckedChangeListener {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"

    }


    private lateinit var spinner: Spinner
    private lateinit var spinnerCopy: Spinner
    val CAMERA_ORIENTATION = "ch.epfl.sdp.healthplay.kneetag.MainActivity.CAMERA_ORIENTATION"

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView
    private var leftPersonId: String? = null
    private var rightPersonId: String? = null

    /** Default device is CPU */
    private var device = Device.CPU
    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var database: Database

    private var message: Boolean = false
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var poseDetector: MoveNetMultiPose
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false
    private var name: String = ""
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }

    //When the switch button is pressed change the camera between BACK/FRONT
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(CAMERA_ORIENTATION, !message)
        startActivity(intent)
        finish()

    }

    //Initialize the friendList + Create the layout
    override fun onCreate(savedInstanceState: Bundle?) {


        database = Database()
        val user = mAuth.currentUser
        if (user != null) {
            initUsername(user.uid)
        }

        super.onCreate(savedInstanceState)
        layoutCreation()
    }

    fun layoutCreation() {

        setContentView(R.layout.activity_main)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // attaching data adapter to spinner
        val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
        facingSwitch.setOnCheckedChangeListener(this)
        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
        surfaceView = findViewById(R.id.surfaceView)


        // Get the Friend List of the current User
        val database = Database()
        database.readField(
            mAuth.currentUser!!.uid, "friends",
            OnCompleteListener { task: Task<DataSnapshot> ->
                if (!task.isSuccessful) {
                    Log.e("ERROR", "EREREREROOORORO")
                } else {
                    var friendsList: MutableList<String> = arrayListOf()
                    val friends =
                        task.result.value as Map<String, String>?
                    if (friends != null) {
                        friendsList = friends!!.values.toMutableList()

                    }
                    friendsList.add(0, "Anonymous")
                    friendsList.add(0, "YOU")
                    spinner = findViewById<Spinner>(R.id.friends)
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            friendsList
                        )

                    //set the spinners adapter to the previously created one.

                    spinner.adapter = adapter
                    //Set the left person
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val text = parent?.getItemAtPosition(position).toString()
                            if (friends != null)
                                leftPersonId =
                                    friends!!.filterValues { id: String -> id == text }.keys.toMutableList()
                                        .getOrElse(0, { index: Int -> text })
                            poseDetector.leftPerson = Pair(poseDetector.leftPerson.first, text)
                        }
                    }
                    spinnerCopy = findViewById<Spinner>(R.id.friendsCopy)
                    spinnerCopy.adapter = adapter
                    //Set the right person
                    spinnerCopy.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val text = parent?.getItemAtPosition(position).toString()
                                if (friends != null)
                                    rightPersonId =
                                        friends!!.filterValues { id: String -> id == text }.keys.toMutableList()
                                            .getOrElse(0, { index: Int -> text })
                                poseDetector.rightPerson =
                                    Pair(poseDetector.rightPerson.first, text)
                            }
                        }

                }
            })



        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        //It's the button to launch the kneetag Game
        val kneetagLaunchButton: Button = findViewById<Button>(R.id.startGame)
        kneetagLaunchButton.setOnClickListener {
            if (cameraSource!!.gameState == 0) {

                var text = getString(R.string.invalidPlayer)
                val left = spinner.selectedItem.toString()
                val right = spinnerCopy.selectedItem.toString()
                if (poseDetector.leftPerson.first != null
                    && poseDetector.rightPerson.first != null && !(left == "Anonymous" && right == "Anonymous")
                ) {
                    if (check(left) and check(right)) {
                        cameraSource!!.gameState = cameraSource!!.UNRANKED_GAME
                        text = getString(R.string.unrankedStart)
                        kneetagLaunchButton.text = "Fight!"
                        MediaPlayer.create(this, R.raw.notification).start()
                        spinner.isVisible = false
                        spinnerCopy.isVisible = false
                        facingSwitch.isVisible = false
                    }
                    if ((left == "YOU" && !check(right)) or (right == "YOU" && !check(left))) {
                        cameraSource!!.gameState = cameraSource!!.RANKED_GAME
                        text = getString(R.string.gameStarted)
                        kneetagLaunchButton.text = "Fight!"
                        MediaPlayer.create(this, R.raw.notification).start()
                        spinner.isEnabled = false
                        spinnerCopy.isEnabled = false
                        facingSwitch.isVisible = false
                    }
                } else
                    text = getString(R.string.notDtected)

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun check(string: String): Boolean {
        return string == "Anonymous" || string == "YOU"
    }


    override fun onStart() {
        val intent = intent
        message = intent.getBooleanExtra(CAMERA_ORIENTATION, false)

        super.onStart()
        openCamera(message)
    }

    private fun initUsername(userId: String) {

        database.readField(
            userId,
            Database.USERNAME,
            OnCompleteListener { task: Task<DataSnapshot> ->
                if (!task.isSuccessful) {
                    Log.e("firebase", "Error getting data", task.exception)
                } else {
                    name = task.result.value.toString()
                }
            })
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }


    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun gameEndedScreen(result: Int) {
        val intent = Intent(this, FinishScreen::class.java)
        //transmit information about the participant to the FinishScreen
        if (result == 1) {

            intent.putExtra("WINNER_NAME", poseDetector.leftPerson.second)
            intent.putExtra("WINNER_ID", leftPersonId)
            intent.putExtra("LOOSER_NAME", poseDetector.rightPerson.second)
            intent.putExtra("LOOSER_ID", rightPersonId)
        }
        if (result == 2) {
            intent.putExtra("WINNER_NAME", poseDetector.rightPerson.second)
            intent.putExtra("WINNER_ID", rightPersonId)
            intent.putExtra("LOOSER_NAME", poseDetector.leftPerson.second)
            intent.putExtra("LOOSER_ID", leftPersonId)
        }
        intent.putExtra("RANKED", cameraSource!!.gameState == 2)
        startActivity(intent)
        finish()

    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun openCamera(orientation: Boolean = false) {

        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(
                        surfaceView,
                        orientation,
                        this,
                        object : CameraSource.CameraSourceListener {
                            override fun onFPSListener(fps: Int) {
                                tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                                fps

                            }

                            override fun onDetectedInfo(
                                personScore: Float?,
                                poseLabels: List<Pair<String, Float>>?
                            ) {
                                tvScore.text =
                                    getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
                            }

                        }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                    cameraSource
                }
            }

            createPoseEstimator()
        }

    }


    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        poseDetector =
            MoveNetMultiPose.create(
                this,
                device,
                Type.Dynamic
            )
        poseDetector.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {
            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
