package ch.epfl.sdp.healthplay.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
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
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_barcode_scan, container, false)
    }
}