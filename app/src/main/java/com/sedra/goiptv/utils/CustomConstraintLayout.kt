package com.sedra.goiptv.utils

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class CustomConstraintLayout : ConstraintLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :
            super(context!!, attrs, defStyle)

    constructor(context: Context?, attrs: AttributeSet?) :
            super(context!!, attrs)

    constructor(context: Context) : super(context)

    override fun clearFocus() {
        if (this.parent != null) super.clearFocus()
    }
}