package com.emojigame.android.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:tint")
fun setImageViewTint(view: ImageView, color: Int) {
    view.setColorFilter(color)
}
