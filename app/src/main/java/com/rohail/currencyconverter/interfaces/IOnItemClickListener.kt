package com.rohail.currencyconverter.interfaces

import android.view.View

interface IOnItemClickListener {
    fun onItemClick(isoName: String)
    fun onFocusChange(hasFocus: Boolean, v: View)
}