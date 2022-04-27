package ch.epfl.sdp.healthplay.kneetag

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import ch.epfl.sdp.healthplay.R
import ch.epfl.sdp.healthplay.database.Database
import ch.epfl.sdp.healthplay.database.Friend
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


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"

    }

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default device is CPU */
    private var device = Device.CPU
    private var friends: MutableList<String> = mutableListOf()
    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var database: Database


    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var poseDetector: MoveNetMultiPose
    private lateinit var tvClassificationValue1: TextView
    private lateinit var tvClassificationValue2: TextView
    private lateinit var tvClassificationValue3: TextView
    private val friendList: MutableList<Friend> = ArrayList()
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


    override fun onCreate(savedInstanceState: Bundle?) {

        // Inflate the layout for this fragment
        database = Database()
        val user = mAuth.currentUser
        if (user != null) {
            initUsername(user.uid)
        }
        // Get the Friend List of the current User
        if (user != null) {
            database.readField(
                user.uid, "friends"
            ) { task ->
                val mut = task.result.value as Map<String, Boolean>
                friends.addAll(mut.keys)
                for (friend in mut.keys) {

                    friendList.add(Friend(friend))
                }
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
        surfaceView = findViewById(R.id.surfaceView)

        friends = mutableListOf("Anonymous", "YOU")
        var spinner = findViewById<Spinner>(R.id.friends)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, friends)
//set the spinners adapter to the previously created one.
//set the spinners adapter to the previously created one.
        spinner.adapter = adapter
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val text = parent?.getItemAtPosition(position).toString()
                poseDetector.leftPerson=Pair(poseDetector.leftPerson.first,text)
            }
        }
        var spinnerCopy = findViewById<Spinner>(R.id.friendsCopy)
        spinnerCopy.adapter = adapter
        spinnerCopy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val text = parent?.getItemAtPosition(position).toString()
                poseDetector.rightPerson=Pair(poseDetector.rightPerson.first,text)
            }
        }
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        //It's the button to launch the kneetag Game
        val kneetagLaunchButton: Button = findViewById<Button>(R.id.startGame)
        kneetagLaunchButton.setOnClickListener {
            var text = "The 2 players are not valid"
            val left = spinner.selectedItem.toString()
            val right = spinnerCopy.selectedItem.toString()
            if (check(left) and check(right)) {
                text = "Unranked game started"
                poseDetector.started = true
            }
            if ((left == "YOU" && !check(right)) or (right == "YOU" && !check(left))) {
                poseDetector.started = true
                text = "Ranked game started"
            }
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }

    }

    fun check(string: String): Boolean {
        return string == "Anonymous" || string == "YOU"
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onStart() {
        super.onStart()
        openCamera()
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

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }


    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                        }
                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
                            tvScore.text = getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
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

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
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
