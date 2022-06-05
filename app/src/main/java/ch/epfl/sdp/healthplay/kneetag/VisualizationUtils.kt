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

package  ch.epfl.sdp.healthplay.kneetag

import android.graphics.*
import ch.epfl.sdp.healthplay.kneetag.data.BodyPart
import ch.epfl.sdp.healthplay.kneetag.data.Person
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 6f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 4f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    private const val TRESHOLD_TO_WIN = 30f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE),
        //add the hand joins to the model
        Pair(BodyPart.LEFT_HAND, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_HAND, BodyPart.RIGHT_WRIST)
    )

    // Draw line and point indicate body pose
    //result indicate if there is a winner and if the winner is the left or right player
    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        leftPerson: Pair<Person?, String>,
        rightPerson: Pair<Person?, String>,
        gameStarted: Boolean,
    ): Pair<Bitmap, Int> {

        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.BLUE
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.BLUE
            style = Paint.Style.STROKE
        }
        val paintCircleKnee = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL
        }

        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)
        persons.forEach {
            //add the position of the hand

                person ->

            // draw person id if tracker is enable

            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }


            person.keyPoints.forEach { point ->
                originalSizeCanvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
                //exception with red color to get a better view of the knee.
                if (point.bodyPart == BodyPart.LEFT_KNEE || point.bodyPart == BodyPart.RIGHT_KNEE)
                    originalSizeCanvas.drawCircle(
                        point.coordinate.x,
                        point.coordinate.y,
                        CIRCLE_RADIUS * 2,
                        paintCircleKnee
                    )

            }
        }
        var result = 0
        // display the name of the player at their positions
        // compute who wins
        if (persons.size == 2) {
            originalSizeCanvas.drawText(
                leftPerson.second,
                leftPerson.first!!.keyPoints[BodyPart.NOSE.position].coordinate.x,
                leftPerson.first!!.keyPoints[BodyPart.NOSE.position].coordinate.y - 30,
                paintText
            )
            originalSizeCanvas.drawText(
                rightPerson.second,
                rightPerson.first!!.keyPoints[BodyPart.NOSE.position].coordinate.x,
                rightPerson.first!!.keyPoints[BodyPart.NOSE.position].coordinate.y - 30,
                paintText
            )

            if (gameStarted) {
                //if a bodyPart of one player is close to an other player then end the game.
                for (bodyPart in leftPerson.first!!.keyPoints)

                    if (distance(
                            rightPerson.first!!.keyPoints[BodyPart.LEFT_KNEE.position].coordinate,
                            bodyPart.coordinate
                        ) < TRESHOLD_TO_WIN
                        || distance(
                            rightPerson.first!!.keyPoints[BodyPart.RIGHT_KNEE.position].coordinate,
                            bodyPart.coordinate
                        ) < TRESHOLD_TO_WIN
                    )
                        result = 1


                for (bodyPart in rightPerson.first!!.keyPoints)

                    if (distance(
                            leftPerson.first!!.keyPoints[BodyPart.LEFT_KNEE.position].coordinate,
                            bodyPart.coordinate
                        ) < TRESHOLD_TO_WIN
                        || distance(
                            leftPerson.first!!.keyPoints[BodyPart.RIGHT_KNEE.position].coordinate,
                            bodyPart.coordinate
                        ) < TRESHOLD_TO_WIN
                    )
                        result = 2
            }

        }


        return Pair(output, result)
    }

    fun distance(a: PointF, b: PointF): Double {
        return sqrt(pow((a.x - b.x).toDouble(), 2.0) + pow((a.y - b.y).toDouble(), 2.0))
    }
}
