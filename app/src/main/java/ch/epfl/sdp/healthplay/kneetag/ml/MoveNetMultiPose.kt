/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import android.os.SystemClock
import androidx.core.graphics.minus
import ch.epfl.sdp.healthplay.kneetag.data.BodyPart
import ch.epfl.sdp.healthplay.kneetag.data.Device
import ch.epfl.sdp.healthplay.kneetag.data.KeyPoint
import ch.epfl.sdp.healthplay.kneetag.data.Person
import ch.epfl.sdp.healthplay.kneetag.ml.PoseDetector
import ch.epfl.sdp.healthplay.kneetag.tracker.KeyPointsTracker
import ch.epfl.sdp.healthplay.tracker.AbstractTracker
import ch.epfl.sdp.healthplay.tracker.BoundingBoxTracker
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter

import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.ceil
import kotlin.math.min

class MoveNetMultiPose(
    private val interpreter: Interpreter,
    private val type: Type,
    private val gpuDelegate: GpuDelegate?,
) : PoseDetector {
    private val outputShape = interpreter.getOutputTensor(0).shape()
    private val inputShape = interpreter.getInputTensor(0).shape()
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var targetWidth: Int = 0
    private var targetHeight: Int = 0
    private var scaleHeight: Int = 0
    private var scaleWidth: Int = 0
    private var lastInferenceTimeNanos: Long = -1
    private var tracker: AbstractTracker? = null
    //leftPerson name is updated when the spinner change and the Person is updated when 2 persons are detected
    public var leftPerson: Pair<Person?,String> = Pair(null,"Anonymous")
    //rightPerson name is updated when the spinner change and the Person is updated when 2 persons are detected
    public var rightPerson: Pair<Person?,String> = Pair(null,"Anonymous")
    public var started = false

    companion object {
        private const val DYNAMIC_MODEL_TARGET_INPUT_SIZE = 256
        private const val SHAPE_MULTIPLE = 32.0
        private const val DETECTION_THRESHOLD = 0.11
        private const val DETECTION_SCORE_INDEX = 55
        private const val BOUNDING_BOX_Y_MIN_INDEX = 51
        private const val BOUNDING_BOX_X_MIN_INDEX = 52
        private const val BOUNDING_BOX_Y_MAX_INDEX = 53
        private const val BOUNDING_BOX_X_MAX_INDEX = 54
        private const val KEYPOINT_COUNT = 17
        private const val OUTPUTS_COUNT_PER_KEYPOINT = 3
        private const val CPU_NUM_THREADS = 4

        // allow specifying model type.
        fun create(
            context: Context,
            device: Device,
            type: Type,
        ): MoveNetMultiPose {
            val options = Interpreter.Options()
            var gpuDelegate: GpuDelegate? = null
            when (device) {
                Device.CPU -> {
                    options.setNumThreads(CPU_NUM_THREADS)
                }
                Device.GPU -> {
                    // only fixed model support Gpu delegate option.
                    if (type == Type.Fixed) {
                        gpuDelegate = GpuDelegate()
                        options.addDelegate(gpuDelegate)
                    }
                }
            }
            return MoveNetMultiPose(
                Interpreter(
                    FileUtil.loadMappedFile(
                        context,
                        if (type == Type.Dynamic)
                            "movenet_multipose_fp16.tflite" else ""
                    ), options
                ), type, gpuDelegate
            )
        }
    }

    /**
     * Convert x and y coordinates ([0-1]) returns from the TFlite model
     * to the coordinates corresponding to the input image.
     */
    private fun resizeKeypoint(x: Float, y: Float): PointF {
        return PointF(resizeX(x), resizeY(y))
    }

    private fun resizeX(x: Float): Float {
        return if (imageWidth > imageHeight) {
            val ratioWidth = imageWidth.toFloat() / targetWidth
            x * targetWidth * ratioWidth
        } else {
            val detectedWidth =
                if (type == Type.Dynamic) targetWidth else inputShape[2]
            val paddingWidth = detectedWidth - scaleWidth
            val ratioWidth = imageWidth.toFloat() / scaleWidth
            (x * detectedWidth - paddingWidth / 2f) * ratioWidth
        }
    }

    private fun resizeY(y: Float): Float {
        return if (imageWidth > imageHeight) {
            val detectedHeight =
                if (type == Type.Dynamic) targetHeight else inputShape[1]
            val paddingHeight = detectedHeight - scaleHeight
            val ratioHeight = imageHeight.toFloat() / scaleHeight
            (y * detectedHeight - paddingHeight / 2f) * ratioHeight
        } else {
            val ratioHeight = imageHeight.toFloat() / targetHeight
            y * targetHeight * ratioHeight
        }
    }

    /**
     * Prepare input image for detection
     */
    private fun processInputTensor(bitmap: Bitmap): TensorImage {
        imageWidth = bitmap.width
        imageHeight = bitmap.height

        // if model type is fixed. get input size from input shape.
        val inputSizeHeight =
            if (type == Type.Dynamic) DYNAMIC_MODEL_TARGET_INPUT_SIZE else inputShape[1]
        val inputSizeWidth =
            if (type == Type.Dynamic) DYNAMIC_MODEL_TARGET_INPUT_SIZE else inputShape[2]

        val resizeOp: ImageOperator
        if (imageWidth > imageHeight) {
            val scale = inputSizeWidth / imageWidth.toFloat()
            targetWidth = inputSizeWidth
            scaleHeight = ceil(imageHeight * scale).toInt()
            targetHeight = (ceil((scaleHeight / SHAPE_MULTIPLE)) * SHAPE_MULTIPLE).toInt()
            resizeOp = ResizeOp(scaleHeight, targetWidth, ResizeOp.ResizeMethod.BILINEAR)
        } else {
            val scale = inputSizeHeight / imageHeight.toFloat()
            targetHeight = inputSizeHeight
            scaleWidth = ceil(imageWidth * scale).toInt()
            targetWidth = (ceil((scaleWidth / SHAPE_MULTIPLE)) * SHAPE_MULTIPLE).toInt()
            resizeOp = ResizeOp(targetHeight, scaleWidth, ResizeOp.ResizeMethod.BILINEAR)
        }

        val resizeWithCropOrPad = if (type == Type.Dynamic) ResizeWithCropOrPadOp(
            targetHeight,
            targetWidth
        ) else ResizeWithCropOrPadOp(
            inputSizeHeight,
            inputSizeWidth
        )
        val imageProcessor = ImageProcessor.Builder().apply {
            add(resizeOp)
            add(resizeWithCropOrPad)
        }.build()
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        return imageProcessor.process(tensorImage)
    }

    /**
     * Run tracker (if available) and process the output.
     */
    private fun postProcess(modelOutput: FloatArray): List<Person> {

        val persons = mutableListOf<Person>()
        //Detect a maximum of 2 personss
        for (idx in (0..min(modelOutput.indices.last, outputShape[2])) step outputShape[2]) {
            val personScore = modelOutput[idx + DETECTION_SCORE_INDEX]
            if (personScore < DETECTION_THRESHOLD) continue
            val positions = modelOutput.copyOfRange(idx, idx + 51)
            val keyPoints = mutableListOf<KeyPoint>()
            for (i in 0 until KEYPOINT_COUNT) {
                val y = positions[i * OUTPUTS_COUNT_PER_KEYPOINT]
                val x = positions[i * OUTPUTS_COUNT_PER_KEYPOINT + 1]
                val score = positions[i * OUTPUTS_COUNT_PER_KEYPOINT + 2]
                keyPoints.add(KeyPoint(BodyPart.fromInt(i), PointF(x, y), score))
            }
            // The hands are a prolongation of the wrist
            keyPoints.add(KeyPoint(BodyPart.fromInt(BodyPart.LEFT_HAND.position),
                keyPoints[BodyPart.LEFT_WRIST.position].coordinate*(4.0/3.0)-keyPoints[BodyPart.LEFT_ELBOW.position].coordinate*(1.0/3.0),0F))
            keyPoints.add(KeyPoint(BodyPart.fromInt(BodyPart.RIGHT_HAND.position),
                keyPoints[BodyPart.RIGHT_WRIST.position].coordinate*(4.0/3.0)-keyPoints[BodyPart.RIGHT_ELBOW.position].coordinate*(1.0/3.0),0F))
            val yMin = modelOutput[idx + BOUNDING_BOX_Y_MIN_INDEX]
            val xMin = modelOutput[idx + BOUNDING_BOX_X_MIN_INDEX]
            val yMax = modelOutput[idx + BOUNDING_BOX_Y_MAX_INDEX]
            val xMax = modelOutput[idx + BOUNDING_BOX_X_MAX_INDEX]
            val boundingBox = RectF(xMin, yMin, xMax, yMax)
            persons.add(
                Person(
                    keyPoints = keyPoints,
                    boundingBox = boundingBox,
                    score = personScore
                )
            )
            //Initialize the left and right person if the number of person displayed is 2
            if (persons.size == 2) {


                if (persons[0].keyPoints[BodyPart.NOSE.position].coordinate.x < persons[1].keyPoints[BodyPart.NOSE.position].coordinate.x) {

                    leftPerson = Pair(persons[0],leftPerson.second)

                    rightPerson = Pair(persons[1],rightPerson.second)
                } else {

                    leftPerson = Pair(persons[1],leftPerson.second)
                    rightPerson = Pair(persons[0],rightPerson.second)
                }
            }else{
                leftPerson = Pair(null,leftPerson.second)
                rightPerson = Pair(null,rightPerson.second)
            }

        }

        if (persons.isEmpty()) return emptyList()

        if (tracker == null) {
            persons.forEach {
                it.keyPoints.forEach { key ->
                    key.coordinate = resizeKeypoint(key.coordinate.x, key.coordinate.y)
                }
            }
            return persons
        } else {
            val trackPersons = mutableListOf<Person>()
            tracker?.apply(persons, System.currentTimeMillis() * 1000)?.forEach {
                val resizeKeyPoint = mutableListOf<KeyPoint>()
                it.keyPoints.forEach { key ->
                    resizeKeyPoint.add(
                        KeyPoint(
                            key.bodyPart,
                            resizeKeypoint(key.coordinate.x, key.coordinate.y),
                            key.score
                        )
                    )
                }

                var resizeBoundingBox: RectF? = null
                it.boundingBox?.let { boundingBox ->
                    resizeBoundingBox = RectF(
                        resizeX(boundingBox.left),
                        resizeY(boundingBox.top),
                        resizeX(boundingBox.right),
                        resizeY(boundingBox.bottom)
                    )
                }
                trackPersons.add(Person(it.id, resizeKeyPoint, resizeBoundingBox, it.score))
            }
            return trackPersons
        }
    }


    /**
     * Run TFlite model and Returns a list of "Person" corresponding to the input image.
     */
    override fun estimatePoses(bitmap: Bitmap): List<Person> {
        val inferenceStartTimeNanos = SystemClock.elapsedRealtimeNanos()
        val inputTensor = processInputTensor(bitmap)
        val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

        // if model is dynamic, resize input before run interpreter
        if (type == Type.Dynamic) {
            val inputShape = intArrayOf(1).plus(inputTensor.tensorBuffer.shape)
            interpreter.resizeInput(0, inputShape, true)
            interpreter.allocateTensors()
        }
        interpreter.run(inputTensor.buffer, outputTensor.buffer.rewind())

        val processedPerson = postProcess(outputTensor.floatArray)
        lastInferenceTimeNanos =
            SystemClock.elapsedRealtimeNanos() - inferenceStartTimeNanos
        return processedPerson
    }

    override fun lastInferenceTimeNanos(): Long = lastInferenceTimeNanos

    /**
     * Close all resources when not in use.
     */
    override fun close() {
        gpuDelegate?.close()
        interpreter.close()
        tracker = null
    }
}

private operator fun PointF.times(i: Double): PointF {
    return PointF((this.x*i).toFloat(), (this.y*i).toFloat())
}

enum class Type {
    Dynamic, Fixed
}

