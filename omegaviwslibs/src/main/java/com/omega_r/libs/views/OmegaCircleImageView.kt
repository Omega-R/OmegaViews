package com.omega_r.libs.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import de.hdodenhof.circleimageview.CircleImageView

open class OmegaCircleImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.omegaCircleImageViewStyle
) : CircleImageView(context, attrs, defStyleAttr) {

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (SDK_INT >= LOLLIPOP) invalidateOutline()
    }

}