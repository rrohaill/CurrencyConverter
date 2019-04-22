package com.rohail.currencyconverter.interfaces

import com.rohail.currencyconverter.data_files.CurrencyModel
import com.rohail.currencyconverter.utils.Constants
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RevolutApiService {

    @GET("latest")
    fun latestCurrencies(@Query("base") action: String): Single<CurrencyModel>
            //:CompositeDisposable
            //Observable<CurrencyModel>
}