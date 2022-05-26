package ch.epfl.sdp.healthplay.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import ch.epfl.sdp.healthplay.BarcodeScanFragment
import ch.epfl.sdp.healthplay.R

/**
 * A simple [Fragment] subclass.
 * Use the [BarcodeScanFragmentTest.newInstance] factory method to
 * create an instance of this fragment.
 */
class BarcodeScanFragmentTest : BarcodeScanFragment() {

    override fun createCamera(cameraProvider: ProcessCameraProvider?, preview: Preview?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barcode_scan, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BarcodeScanFragmentTest.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = Unit
    }
}