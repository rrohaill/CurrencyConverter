package com.rohail.currencyconverter.interfaces

import com.rohail.currencyconverter.data_files.Rates

interface ICurrencyView {
    fun updateRates(rates: MutableList<Rates>)
    fun showError(errorText: String)
}