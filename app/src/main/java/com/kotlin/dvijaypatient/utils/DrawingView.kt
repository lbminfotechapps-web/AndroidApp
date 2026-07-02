package com.kotlin.dvijaypatient.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kotlin.dvijaypatient.ui.fragment.signature.FragmentSignature

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mPath = Path()
    private val mPaint = Paint()
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private val circlePaint = Paint()
    private val circlePath = Path()

    init {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 12f

        circlePaint.isAntiAlias = true
        circlePaint.color = Color.BLUE
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeJoin = Paint.Join.MITER
        circlePaint.strokeWidth = 4f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.drawPath(mPath, mPaint)
        canvas.drawPath(circlePath, circlePaint)
    }

    private var mX = 0f
    private var mY = 0f
    private val TOUCH_TOLERANCE = 4f

    private fun touchStart(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y

            circlePath.reset()
            circlePath.addCircle(mX, mY, 30f, Path.Direction.CW)
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
        circlePath.reset()
        mCanvas?.drawPath(mPath, mPaint)
        mPath.reset()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        FragmentSignature.isSignatureDrawn = true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

    fun erase() {
        mCanvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}
