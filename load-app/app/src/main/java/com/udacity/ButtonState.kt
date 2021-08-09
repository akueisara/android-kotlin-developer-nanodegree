package com.udacity


sealed class ButtonState {
    object Failed : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}