package ch.epfl.sdp.healthplay.ml

import android.content.Context
import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sdp.healthplay.kneetag.data.BodyPart
import ch.epfl.sdp.healthplay.kneetag.data.Device
import ch.epfl.sdp.healthplay.kneetag.ml.PoseDetector
import ch.epfl.sdp.healthplay.kneetag.ml.PoseNet
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosenetTest {

    companion object {
        private const val TEST_INPUT_IMAGE1 = "image1.png"
        private const val TEST_INPUT_IMAGE2 = "image2.jpg"
        private const val ACCEPTABLE_ERROR = 37f
    }

    private lateinit var poseDetector: PoseDetector
    private lateinit var appContext: Context
    private lateinit var expectedDetectionResult: List<Map<BodyPart, PointF>>

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        poseDetector = PoseNet.create(appContext, Device.CPU)
        expectedDetectionResult =
            EvaluationUtils.loadCSVAsset("pose_landmark_truth.csv")
    }

    @Test
    fun testPoseEstimationResultWithImage1() {
        val input = EvaluationUtils.loadBitmapAssetByName(TEST_INPUT_IMAGE1)
        val person = poseDetector.estimatePoses(input)[0]
        EvaluationUtils.assertPoseDetectionResult(
            person,
            expectedDetectionResult[0],
            ACCEPTABLE_ERROR
        )
    }

    @Test
    fun testPoseEstimationResultWithImage2() {
        val input = EvaluationUtils.loadBitmapAssetByName(TEST_INPUT_IMAGE2)
        val person = poseDetector.estimatePoses(input)[0]
        EvaluationUtils.assertPoseDetectionResult(
            person,
            expectedDetectionResult[1],
            ACCEPTABLE_ERROR
        )
    }
}