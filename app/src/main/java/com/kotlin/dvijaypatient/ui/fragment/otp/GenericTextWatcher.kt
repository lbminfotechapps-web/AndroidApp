package com.kotlin.dvijaypatient.ui.fragment.otp

import android.text.Editable
import android.text.TextWatcher
import android.view.View


class GenericTextWatcher(private val currentView: View?, private val nextView: View?) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        if (editable.length == 1 && nextView != null) {
            nextView.requestFocus()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}