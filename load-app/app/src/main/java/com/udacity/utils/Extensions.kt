package com.udacity.utils

import android.content.res.Resources

val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density