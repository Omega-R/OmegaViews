package com.omega_r.libs.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.res.ResourcesCompat
import com.omega_r.libs.omegatypes.*


/**
 * Created by Anton Knyazev on 18.05.2019.
 */
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
    private val endTextStyle = Style()

    private var initData: Boolean = true

    init {
        initData = false
        if (attrs != null) {
            initWithAttributes(context, attrs, defStyleAttr)
        }
        initData = true
        updateAllText()
    }

    @SuppressLint("RestrictedApi")
    private fun initWithAttributes(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) {
        val a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.OmegaTextView, defStyleAttr, 0)

        for (i in 0 until a.indexCount) {
            when (val attr = a.getIndex(i)) {
                R.styleable.OmegaTextView_startText -> {
                    startText = a.getText(attr).toText()
                }
                R.styleable.OmegaTextView_endText -> {
                    endText = a.getText(attr).toText()
                }
                R.styleable.OmegaTextView_startTextSize -> {
                    startTextStyle.textSize = Size.from(a.getDimension(attr, 0f), Size.Unit.PX)
                }
                R.styleable.OmegaTextView_endTextSize -> {
                    endTextStyle.textSize = Size.from(a.getDimension(attr, 0f), Size.Unit.PX)
                }
                R.styleable.OmegaTextView_startTextStyle -> {
                    startTextStyle.style = a.getInt(attr, STYLE_NONE)
                }
                R.styleable.OmegaTextView_endTextStyle -> {
                    endTextStyle.style = a.getInt(attr, STYLE_NONE)
                }
                R.styleable.OmegaTextView_startTextColor -> {
                    startTextStyle.textColor = a.getColor(attr, startTextStyle.textColor)
                }
                R.styleable.OmegaTextView_endTextColor -> {
                    endTextStyle.textColor = a.getColor(attr, endTextStyle.textColor)
                }
                R.styleable.OmegaTextView_startTextFontFamily -> {
                    obtainFont(intoStyle = startTextStyle, typedArray = a, attr = attr)
                }
                R.styleable.OmegaTextView_endTextFontFamily -> {
                    obtainFont(intoStyle = endTextStyle, typedArray = a, attr = attr)
                }
                R.styleable.OmegaTextView_startDelimiter -> {
                    a.getString(attr)?.let {
                        startSpaceText = Text.from(it)
                    }
                }
                R.styleable.OmegaTextView_endDelimiter -> {
                    a.getString(attr)?.let {
                        endSpaceText = Text.from(it)
                    }
                }

            }
        }
        a.recycle()
    }

    @SuppressLint("RestrictedApi")
    private fun obtainFont(intoStyle: Style, typedArray: TintTypedArray, attr: Int) {
        if (!context.isRestricted) {
            try {
                intoStyle.fontTypeface = typedArray.getFont(attr, intoStyle.style, null)
            } catch (e: UnsupportedOperationException) {
                // Expected if it is not a font resource.
            } catch (e: Resources.NotFoundException) {
                // Expected if it is not a font resource.
            }
        }
        if (intoStyle.fontTypeface == null) {
            intoStyle.fontFamily = typedArray.getString(attr)
        }
    }

    override fun getText(): CharSequence? {
        super.getText()
        return text?.getCharSequence(context)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        this.text = text?.toText()
    }

    private fun updateAllText(force: Boolean = false) {
        if (initData || force) {
            val allText = (startText + startTextStyle) + startSpaceText + text + endSpaceText + (endText + endTextStyle)
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

}