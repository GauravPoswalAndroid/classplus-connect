package com.fankonnect.app.util

import android.view.View

// extension to compare two integers
infix fun Int.not(value: Int): Boolean = this != value

// region View extensions
fun View.hide() {
    if (this.visibility not View.GONE)
        this.visibility = View.GONE
}

fun View.show() {
    if (this.visibility not View.VISIBLE)
        this.visibility = View.VISIBLE
}