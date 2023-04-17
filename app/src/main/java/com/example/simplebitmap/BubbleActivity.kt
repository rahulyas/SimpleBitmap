package com.example.simplebitmap

import android.content.ClipData
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.TouchImageView
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class BubbleActivity : AppCompatActivity() {

    var paint: Paint? = null
    var bitmap: Bitmap? = null


    val TAG = "MainActivity"
    var width = 0
    var height = 0
    var C3 = 0f
    var C2 = 0f
    var BubbleBackground = 0f
    var BubbleOutline = 0f

    var C1 = 20f
    var centreX = 0f
    var centreY = 0f
    var ivOne: ImageView? = null
    var btnChange: Button? = null
    lateinit var paintInCentre: Paint
    lateinit var paintOuter: Paint
    lateinit var paintInner: Paint
    lateinit var paint2: Paint
    lateinit var linePaint: Paint
    lateinit var linePaint2: Paint


    private var rect = Rect()
    private var rectF = RectF()
    private var rectOuter = Rect()
    private var rectFOuter = RectF()
    private var rectInner = Rect()
    private var rectFInner = RectF()
    private var rectInner1 = Rect()
    private var rectFInner1 = RectF()
    var drawn = true

    lateinit var sharedPreferences: SharedPreferences
    private var mDeviceAddress: String? = null
    //    val newouterColor= #5165F1
    var projectName = ""
    var pitch = 0.0
    var roll = 0.0
    var newX_roll = 0.0
    var newY_pitch = 0.0

    //PD----POINT DISTANCE
    var pD = 0.0
    var theta = 0.0
    var tempRad = 0.0
    var rollPitchArray: ArrayList<String> = ArrayList()
    lateinit var hashMap: HashMap<String, Double>

    var modelName=""

    var relativeLayout: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        relativeLayout = findViewById<View>(R.id.relative_layout) as RelativeLayout

        ivOne = findViewById<ImageView>(R.id.imagebitmap)
        ivOne!!.setImageBitmap(bitmap)

        width = size.x
        height = width
        centreX = (width / 2).toFloat()
        centreY = (height / 2).toFloat()
        C2 = (C1*5).toFloat()
        C3 = C2 + C1
        BubbleBackground = C1 * 1.25.toFloat()
        BubbleOutline = C1 * 1.25.toFloat()
        drawBubble()

        relativeLayout!!.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> Log.d(
                    TAG,
                    "Action is DragEvent.ACTION_DRAG_STARTED"
                )
                DragEvent.ACTION_DRAG_ENTERED -> Log.d(
                    TAG,
                    "Action is DragEvent.ACTION_DRAG_ENTERED"
                )
                DragEvent.ACTION_DRAG_EXITED -> Log.d(
                    TAG,
                    "Action is DragEvent.ACTION_DRAG_EXITED"
                )
                DragEvent.ACTION_DRAG_LOCATION -> Log.d(
                    TAG,
                    "Action is DragEvent.ACTION_DRAG_LOCATION"
                )
                DragEvent.ACTION_DRAG_ENDED -> Log.d(
                    TAG,
                    "Action is DragEvent.ACTION_DRAG_ENDED"
                )
                DragEvent.ACTION_DROP -> {
                    Log.d(TAG, "ACTION_DROP event")
                    val tvState = event.localState as View
                    Log.d(TAG, "onDrag:viewX" + event.x + "viewY" + event.y)
                    Log.d(TAG, "onDrag: Owner->" + tvState.parent)
                    val tvParent = tvState.parent as ViewGroup
                    tvParent.removeView(tvState)
                    val container = v as RelativeLayout
                    container.addView(tvState)
                    tvParent.removeView(tvState)
                    tvState.x = event.x
                    tvState.y = event.y
                    (v as RelativeLayout).addView(tvState)
                    v.setVisibility(View.VISIBLE)
                }
                else -> {}
            }
            true
        }

        ivOne!!.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = DragShadowBuilder(ivOne)
                v.startDrag(data, shadowBuilder, v, 0)
                v.visibility = View.VISIBLE
                true
            } else {
                false
            }
        })


    }

    private fun drawBubble() {

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val cv = Canvas(bmp)
        val color = Color.WHITE
//              BUBBLE COLOR
        paint = Paint()
        paint!!.color = color
        rect = Rect(0, 0, width, height)
        rectF = RectF(rect)
        paint!!.color = Color.parseColor("#BD0707")
        paint!!.isAntiAlias = true

        //             Inner BUBBLE COLOR
        paintInCentre = Paint()
        paintInCentre.color = color
        paintInCentre.color = Color.parseColor("#65CB2B")
        paintInCentre.isAntiAlias = true

//             OUTER RING COLOR
        paintOuter = Paint()
        paintOuter.color = color
        paintOuter.color = Color.parseColor("#2862A4")
        paintOuter.isAntiAlias = true


        paintInner = Paint()
        paintInner.color = color
        paintInner.color = Color.parseColor("#E3DEDE")
        paintInner.isAntiAlias = true

        paint2 = Paint()
        paint2.color = color
        paint2.color = Color.parseColor("#FFFFFF")
        paint2.isAntiAlias = true
        paint2.style = Paint.Style.FILL

        linePaint = Paint()
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = 4f
        linePaint.color = Color.BLACK
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE

        linePaint2 = Paint()
        linePaint2.isAntiAlias = true
        linePaint2.strokeWidth = 4f
        linePaint2.color = Color.RED
        linePaint2.isAntiAlias = true
        linePaint2.style = Paint.Style.STROKE


        //OuterCircle
        cv.drawCircle(
            (width / 2.toFloat()),
            (height / 2).toFloat(),
            C3,
            paintOuter
        )
        //FIRST INNER CIRCLE
        cv.drawCircle(
            (width / 2.toFloat()),
            (height / 2).toFloat(),
            C2,
            paintInner
        )
        //Division Lines
        cv.drawLine(
            (width / 2).toFloat(),
            ((height / 2) + C2),
            (width / 2).toFloat(),
            ((height / 2) - C2),
            linePaint
        )
        cv.drawLine(
            width / 2 - C2,
            (height / 2).toFloat(),
            width / 2 + C2,
            (height / 2).toFloat(),
            linePaint
        )
        //SECOND INNER CIRCLE
        cv.drawCircle((width / 2.toFloat()), (height / 2).toFloat(), BubbleBackground, paint2)
        cv.drawCircle(
            (width / 2.toFloat()),
            (height / 2).toFloat(),
            BubbleOutline,
            linePaint
        )
        //Bubble
        cv.drawCircle((width / 2.toFloat()), (height / 2).toFloat(), C1, paintInCentre)

        ivOne!!.setImageBitmap(bmp)
        drawn = true
    }

    private fun reDrawBitmap(roll1 :Double,pitch1 : Double) {

        roll =roll1
        pitch =pitch1
        val newbmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val newcv = Canvas(newbmp)
        //OuterCircle
        newcv.drawCircle((width / 2.toFloat()), (height / 2).toFloat(), C3, paintOuter)
        //InnerCircle1
        newcv.drawCircle((width / 2.toFloat()), (height / 2).toFloat(), C2, paintInner)
        newcv.drawLine((width / 2).toFloat(), ((height / 2) + C2), (width / 2).toFloat(), ((height / 2) - C2), linePaint)

        newcv.drawLine(width / 2 - C2, (height / 2).toFloat(), width / 2 + C2, (height / 2).toFloat(), linePaint)

        //InnerCircle2

        newcv.drawCircle(
            (width / 2.toFloat()),
            (height / 2).toFloat(),
            BubbleBackground,
            paint2
        )
        newcv.drawCircle(
            (width / 2.toFloat()),
            (height / 2).toFloat(),
            BubbleOutline,
            linePaint
        )



        roll *= C1
        pitch *= C1
        Log.d(TAG, "reDrawBitmap:  roll-$roll     pitch-$pitch")
//--------------------POINT DISTANCE-------------------------------

        pD = sqrt(roll.pow(2.0) + pitch.pow(2.0))

        theta = Math.atan2(pitch - 0.0, roll - 0.0) * 180 / Math.PI

        Log.d(TAG, "Theta : " + theta)

        if (theta < 0) {
            theta += 360
        }
        val newPlotX = centreX + roll
        val newPlotY = height - (centreY + pitch)

        Log.d(TAG, "theta  : " + theta)


        Log.d(TAG, " OUTER_CIRCLE_RADIUS  " + C2)

        Log.d(TAG, "PD: " + pD)


        if ((pD <= C2)) {
            if((C1+pD)<BubbleOutline){
                Log.d(TAG, "C1+pD "+(C1+pD))
                Log.d(TAG, "reDrawBitmap:  BubbleOutline $BubbleOutline ")
                Log.d(TAG, "IF Less: $newPlotX  $newPlotY")
                newcv.drawCircle(newPlotX.toFloat(), newPlotY.toFloat(), C1, paintInCentre)
            }else{
                Log.d(TAG, "else Less: $newPlotX  $newPlotY")
                newcv.drawCircle(newPlotX.toFloat(), newPlotY.toFloat(), C1, paint!!)}

            //Bubble

            Log.d(TAG, "IF CONDITION: $newPlotX  $newPlotY")
        } else {

            val tempRad = Math.toRadians(theta)
            val newCalculatedX = C2 * cos(tempRad)
            val newCalCulatedY = C2 * sin(tempRad)
            Log.d(TAG, "Else CONDITION: $newCalculatedX  $newCalCulatedY")


            val calpx = centreX + newCalculatedX
            val calpy = height - (centreY + newCalCulatedY)
            Log.d(TAG, "Else CONDITION: $calpx  $calpy")
            //Bubble
            newcv.drawCircle(calpx.toFloat(), calpy.toFloat(), C1, paint!!)
        }

        ivOne!!.setImageBitmap(newbmp)

    }
}