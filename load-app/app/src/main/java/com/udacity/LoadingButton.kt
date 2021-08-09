package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.udacity.utils.dpToPx
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private val marginBetweenTextAndCircleProgress = 8f.dpToPx
        private val circleProgressRadius = 15f.dpToPx
    }

    private var widthSize = 0
    private var heightSize = 0

    private var buttonTextSize = 0f
    private var buttonText = context.getString(R.string.button_name)

    private var textBounds = Rect()
    private var rectProgressBounds = Rect()
    private var ovalProgressBounds = RectF()

    private var valueAnimator = ValueAnimator.ofFloat(0f, 100f)
    private var currentProgressValue = 0f
    private var currentOvalAngle = 0f

    private var isloading = false

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> {
                isloading = true
                buttonText = context.getString(R.string.button_loading)
                invalidate()
            }
            ButtonState.Completed -> {
                finishProgressAnimation()
            }
            ButtonState.Failed -> {
                initState()
            }
        }
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private val rectProgressPaint = Paint().apply {
        color = context.getColor(R.color.colorPrimaryDark)
    }

    private val ovalProgressPaint = Paint().apply {
        color = context.getColor(R.color.colorAccent)
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    init {
        val defTextSize = context.resources.getDimension(R.dimen.default_text_size)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {
            try {
                buttonTextSize = getDimension(R.styleable.LoadingButton_android_textSize, defTextSize)
                textPaint.textSize = buttonTextSize
            } finally {
                recycle()
            }
        }
        ovalProgressBounds = RectF(0f, 0f, circleProgressRadius * 2f, circleProgressRadius * 2f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Button background color
        canvas?.drawColor(context.getColor(R.color.colorPrimary))

        // Rectangle loading progress
        if (isloading) {
            canvas?.drawRect(rectProgressBounds, rectProgressPaint)
        }

        // Button text
        textPaint.getTextBounds(buttonText,0, buttonText.length, textBounds)
        val textXPosition = widthSize.toFloat() / 2
        val textYPosition = heightSize.toFloat() / 2 - (textPaint.descent() + textPaint.ascent()) / 2
        canvas?.drawText(buttonText, textXPosition, textYPosition, textPaint)

        // Circle loading progress
        if (isloading) {
            canvas?.save()
            canvas?.translate(
                widthSize / 2f + (textBounds.right.toFloat() / 2) + marginBetweenTextAndCircleProgress,
                heightSize / 2f - circleProgressRadius
            )
            canvas?.drawArc(ovalProgressBounds, 0f, currentOvalAngle, true, ovalProgressPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun finishProgressAnimation() {
        if(currentProgressValue < 100f) {
            valueAnimator = ValueAnimator.ofFloat(currentProgressValue, 100f)
            valueAnimator.apply {
                duration = 500
                interpolator = LinearOutSlowInInterpolator()
                addUpdateListener { valueAnimator ->
                    if (valueAnimator.animatedValue as Float != 100f) {
                        setLoadingProgress(valueAnimator.animatedValue as Float)
                    } else {
                        initState()
                    }
                }
            }.start()
        } else {
            initState()
        }
    }

    private fun initState() {
        isloading = false
        rectProgressBounds = Rect(0, 0, 0, heightSize)
        currentOvalAngle = 0f
        buttonText = context.getString(R.string.button_name)
        invalidate()
    }

    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

    fun setLoadingProgress(value: Float) {
        currentProgressValue = value
        val rectMultiplier = widthSize.toFloat() / 100
        val ovalMultiplier = 360.toFloat() / 100
        rectProgressBounds = Rect(0, 0, (currentProgressValue * rectMultiplier).toInt(), heightSize)
        currentOvalAngle = currentProgressValue * ovalMultiplier
        invalidate()
    }
}