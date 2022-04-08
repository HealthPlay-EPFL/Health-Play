

package ch.epfl.sdp.healthplay.ml

import android.content.Context
import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sdp.healthplay.kneetag.VisualizationUtils
import ch.epfl.sdp.healthplay.kneetag.data.Device
import ch.epfl.sdp.healthplay.kneetag.ml.PoseNet
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test is used to visually verify detection results by the models.
 * You can put a breakpoint at the end of the method, debug this method, than use the
 * "View Bitmap" feature of the debugger to check the visualized detection result.
 */
@RunWith(AndroidJUnit4::class)
class VisualizationTest {

    companion object {
        private const val TEST_INPUT_IMAGE = "image2.jpg"
    }

    private lateinit var appContext: Context
    private lateinit var inputBitmap: Bitmap

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        inputBitmap = EvaluationUtils.loadBitmapAssetByName(TEST_INPUT_IMAGE)
    }

    @Test
    fun testPosenet() {
        val poseDetector = PoseNet.create(appContext, Device.CPU)
        val person = poseDetector.estimatePoses(inputBitmap)[0]
        val outputBitmap = VisualizationUtils.drawBodyKeypoints(inputBitmap, arrayListOf(person))
        assertThat(outputBitmap).isNotNull()
    }




}