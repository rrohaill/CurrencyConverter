package com.rohail.currencyconverter.activities

import com.rohail.currencyconverter.data_files.CurrencyModel
import com.rohail.currencyconverter.data_files.Rates
import com.rohail.currencyconverter.interfaces.ICurrencyView
import com.rohail.currencyconverter.utils.ApiUtils
import com.rohail.currencyconverter.utils.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CurrencyPresenter(private val view: ICurrencyView) {
    private var mDisposable: CompositeDisposable? = null
    private var mTimerDisposable: Disposable? = null

    private var mBaseCurrencyValue = 1.0

    private var mSelectedCurrency = "EUR"
    private var mSelectedCurrencyValue = 1.0
    private var mSelectedCurrencyRate = 1.0

    fun getRates() {
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onUpdate() }
    }

    private fun onUpdate() {
        mDisposable = CompositeDisposable()
        mDisposable!!.add(
            ApiUtils.getApiService()
                .latestCurrencies(Constants.BASE_CURRENCY_TYPE)
                .subscribeOn(Schedulers.io())
                .flattenAsObservable(Function<CurrencyModel, Iterable<Map.Entry<String, Double>>> { rates -> rates.rates!!.entries })
                .map(Function<Map.Entry<String, Double>, Rates> { entry ->
                    if (mSelectedCurrency == entry.key) {
                        mSelectedCurrencyRate = entry.value
                        //entry.value=mSelectedCurrencyValue
                        return@Function Rates(entry.key, mSelectedCurrencyValue)
                    }

                    if (Constants.BASE_CURRENCY_TYPE == mSelectedCurrency) {
                        mBaseCurrencyValue = mSelectedCurrencyValue
                    } else {
                        mBaseCurrencyValue = 1 / mSelectedCurrencyRate * mSelectedCurrencyValue
                    }
                    Rates(entry.key, entry.value * mBaseCurrencyValue)
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Consumer<MutableList<Rates>> { rates -> view.updateRates(rates) },
                    Consumer<Throwable> { throwable ->
                        throwable.printStackTrace()
                        view.showError(throwable.message!!)
                    })
        )
    }

    fun disposeAll() {
        mTimerDisposable!!.dispose()
        mDisposable!!.dispose()
    }

    fun setBaseCurrency(baseCurrency: String) {
        Constants.BASE_CURRENCY_TYPE = baseCurrency
    }

    fun getBaseCurrency(): String {
        return Constants.BASE_CURRENCY_TYPE
    }

    fun getBaseCurrencyValue(): Double {
        return mBaseCurrencyValue
    }

    fun setSelectedCurrency(selectedCurrency: String) {
        mSelectedCurrency = selectedCurrency
    }

    fun setSelectedCurrencyValue(selectedCurrencyValue: Double) {
        mSelectedCurrencyValue = selectedCurrencyValue
    }
}