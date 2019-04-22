package com.rohail.currencyconverter.data_files

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CurrencyModel(
    var base: String,
    var date: String,
    //val rates: Rates
    @SerializedName("rates")
    @Expose
    var rates: Map<String, Double>? = null
)