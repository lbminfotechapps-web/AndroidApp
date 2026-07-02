package com.kotlin.dvijaypatient.ui.fragment.otp

import android.view.KeyEvent
import android.view.View
import android.widget.EditText


class GenericKeyEvent(
    private val currentView: EditText,
    private val previousView: EditText
) : View.OnKeyListener {

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

        if (event?.action == KeyEvent.ACTION_DOWN &&
            keyCode == KeyEvent.KEYCODE_DEL &&
            currentView.text.toString().isEmpty()
        ) {

            previousView.requestFocus()

            // move cursor to end
            previousView.setSelection(previousView.text.length)

            // optional → clear previous value
            previousView.text.clear()

            return true
        }
        return false
    }
}