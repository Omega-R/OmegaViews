package com.omega_r.libs.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.TintTypedArray
import androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes
import com.omega_r.libs.omegatypes.*

/**
 * Created by Anton Knyazev on 18.05.2019.
 */
@SuppressLint("RestrictedApi")
open class OmegaTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        const val STYLE_NONE = 0
        const val STYLE_BOLD = 1
        const val STYLE_ITALIC = 2
        const val STYLE_UNDERLINE = 4
        const val STYLE_STTIKETHROUGH = 8
    }

    var startText: Text? = null
        set(value) {
            field = value
            updateAllText()
        }

    var text: Text? = null
        set(value) {
            field = value
            updateAllText()
        }

    var endText: Text? = null
        set(value) {
            field = value
            updateAllText()

        }

    var startSpaceText: Text? = null
        set(value) {
            field = value
            updateAllText()
        }

    var endSpaceText: Text? = null
        set(value) {
            field = value
            updateAllText()
        }

    private val startTextStyle = Style()
    private val middleTextStyle = Style()
    private val endTextStyle = Style()

    private var initData: Boolean = true

    init {
        initData = false
        if (attrs != null) initWithAttributes(context, attrs, defStyleAttr)
        initData = true
        updateAllText()
    }

    private fun initWithAttributes(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.textViewStyle
    ) {
        initWithAttributes(obtainStyledAttributes(context, attrs, R.styleable.OmegaTextView, defStyleAttr, 0))
    }

    private fun initWithAttributes(a: TintTypedArray) {
        if (a.hasValue(R.styleable.OmegaTextView_startTextAppearance)) {
            updateTextAppearance(
                a.getResourceId(R.styleable.OmegaTextView_startTextAppearance, -1),
                startTextStyle
            )
        }
        if (a.hasValue(R.styleable.OmegaTextView_middleTextAppearance)) {
            updateTextAppearance(
                a.getResourceId(R.styleable.OmegaTextView_middleTextAppearance, -1),
                middleTextStyle
            )
        }
        if (a.hasValue(R.styleable.OmegaTextView_endTextAppearance)) {
            updateTextAppearance(
                a.getResourceId(R.styleable.OmegaTextView_endTextAppearance, -1),
                endTextStyle
            )
        }
        for (i in 0 until a.indexCount) {
            when (val attr = a.getIndex(i)) {
                R.styleable.OmegaTextView_startText -> startText = a.getText(attr).toText()
                R.styleable.OmegaTextView_endText -> endText = a.getText(attr).toText()
                R.styleable.OmegaTextView_startTextSize -> startTextStyle.textSize = a.getSize(attr)
                R.styleable.OmegaTextView_middleTextSize -> middleTextStyle.textSize = a.getSize(attr)
                R.styleable.OmegaTextView_endTextSize -> endTextStyle.textSize = a.getSize(attr)
                R.styleable.OmegaTextView_startTextStyle -> startTextStyle.style = a.getStyle(attr)
                R.styleable.OmegaTextView_middleTextStyle -> middleTextStyle.style = a.getStyle(attr)
                R.styleable.OmegaTextView_endTextStyle -> endTextStyle.style = a.getStyle(attr)
                R.styleable.OmegaTextView_startTextColor -> {
                    startTextStyle.textColor = a.getColor(attr, startTextStyle.textColor)
                }
                R.styleable.OmegaTextView_middleTextColor -> {
                    middleTextStyle.textColor = a.getColor(attr, middleTextStyle.textColor)
                }
                R.styleable.OmegaTextView_endTextColor -> {
                    endTextStyle.textColor = a.getColor(attr, endTextStyle.textColor)
                }
                R.styleable.OmegaTextView_startTextFontFamily -> obtainFont(startTextStyle, a, attr)
                R.styleable.OmegaTextView_middleTextFontFamily -> obtainFont(middleTextStyle, a, attr)
                R.styleable.OmegaTextView_endTextFontFamily -> obtainFont(endTextStyle, a, attr)
                R.styleable.OmegaTextView_startDelimiter -> {
                    a.getString(attr)?.let { startSpaceText = Text.from(it) }
                }
                R.styleable.OmegaTextView_endDelimiter -> {
                    a.getString(attr)?.let { endSpaceText = Text.from(it) }
                }
            }
        }
        a.recycle()
    }

    @SuppressLint("PrivateResource")
    private fun updateTextAppearance(resId: Int, style: Style) {
        if (resId == -1) return
        val a = obtainStyledAttributes(context, resId, R.styleable.TextAppearance)
        for (i in 0 until a.indexCount) {
            when (val attr = a.getIndex(i)) {
                R.styleable.TextAppearance_android_textColor -> style.textColor = a.getColor(attr, style.textColor)
                R.styleable.TextAppearance_android_textSize -> style.textSize = a.getSize(attr)
                R.styleable.TextAppearance_android_fontFamily -> obtainFont(style, a, attr)
                R.styleable.TextAppearance_android_textStyle -> style.fontTypeface = getFontTypeface(style, a, attr)
            }
        }
        a.recycle()
    }

    @SuppressLint("RestrictedApi")
    private fun obtainFont(intoStyle: Style, typedArray: TintTypedArray, attr: Int) {
        if (!context.isRestricted) {
            intoStyle.fontTypeface = getFontTypeface(intoStyle, typedArray, attr)
        }
        if (intoStyle.fontTypeface == null) {
            intoStyle.fontFamily = typedArray.getString(attr)
        }
    }

    private fun getFontTypeface(intoStyle: Style, typedArray: TintTypedArray, attr: Int): Typeface? {
        return try {
            typedArray.getFont(attr, intoStyle.style, null)
        } catch (e: UnsupportedOperationException) {
            // Expected if it is not a font resource.
            null
        } catch (e: Resources.NotFoundException) {
            // Expected if it is not a font resource.
            null
        }
    }

    override fun getText(): CharSequence? {
        super.getText()
        return text?.getCharSequence(context)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        this.text = text?.toText()
    }

    override fun setTextAppearance(context: Context, resId: Int) {
        super.setTextAppearance(context, resId)
        invalidateTextAppearance(resId)
    }

    override fun setTextAppearance(resId: Int) {
        super.setTextAppearance(resId)
        invalidateTextAppearance(resId)
    }

    private fun invalidateTextAppearance(resId: Int) {
        initData = false
        initWithAttributes(obtainStyledAttributes(context, resId, R.styleable.OmegaTextView))
        initData = true
        updateAllText()
    }

    private fun updateAllText(force: Boolean = false) {
        if (initData || force) {
            val allText = (startText + startTextStyle) +
                    startSpaceText +
                    (text + middleTextStyle) +
                    endSpaceText +
                    (endText + endTextStyle)
            super.setText(allText?.getCharSequence(context), BufferType.NORMAL)
        }
    }

    private inner class Style {

        var textSize: Size? = null
            set(value) {
                field = value
                updateAllText()
            }

        var style: Int = STYLE_NONE
            set(value) {
                field = value
                updateAllText()
            }

        var textColor: Int = 0
            set(value) {
                field = value
                updateAllText()
            }

        var fontFamily: String? = null
            set(value) {
                field = value
                updateAllText()
            }

        var fontTypeface: Typeface? = null
            set(value) {
                field = value
                updateAllText()
            }

        fun createTextStyle(): TextStyle? {
            var result: TextStyle? = null
            textSize?.let {
                result += TextStyle.size(it)
            }

            if (style > 0) {
                if (containsFlag(style, STYLE_BOLD)) {
                    result += TextStyle.bold()
                }
                if (containsFlag(style, STYLE_ITALIC)) {
                    result += TextStyle.italic()
                }
                if (containsFlag(style, STYLE_STTIKETHROUGH)) {
                    result += TextStyle.strikethrough()
                }
                if (containsFlag(style, STYLE_UNDERLINE)) {
                    result += TextStyle.underline()
                }
            }

            if (textColor != 0) {
                result += TextStyle.color(textColor)
            }

            fontTypeface?.let {
                result += TextStyle.font(it)
            }

            fontFamily?.let {
                result += TextStyle.font(it)
            }

            return result
        }

        private fun containsFlag(flagSet: Int, flag: Int): Boolean {
            return flagSet or flag == flagSet
        }

        private fun addFlag(flagSet: Int, flag: Int): Int {
            return flagSet or flag
        }

        private fun removeFlag(flagSet: Int, flag: Int): Int {
            return flagSet and flag.inv()
        }

    }

    private operator fun Text?.plus(style: Style): Text? {
        return this + style.createTextStyle()
    }

    private fun TintTypedArray.getSize(attr: Int) = Size.from(getDimension(attr, 0f), Size.Unit.PX)

    private fun TintTypedArray.getStyle(attr: Int) = getInt(attr, STYLE_NONE)

}