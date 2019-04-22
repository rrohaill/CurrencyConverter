package com.rohail.currencyconverter.activities

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.rohail.currencyconverter.R
import com.rohail.currencyconverter.adapter.CurrencyAdapter
import com.rohail.currencyconverter.data_files.CurrencyModel
import com.rohail.currencyconverter.data_files.Rates
import com.rohail.currencyconverter.interfaces.ICurrencyView
import com.rohail.currencyconverter.interfaces.IOnItemClickListener
import com.rohail.currencyconverter.utils.ApiUtils
import com.rohail.currencyconverter.utils.Constants
import com.rohail.currencyconverter.utils.EditTextWatcher
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), IOnItemClickListener, ICurrencyView {

    private lateinit var mCurrencyNames: HashMap<String, String>
    private lateinit var mCurrencyIcons: HashMap<String, Drawable>
    private var isCurrencyValueEditing = false

    private lateinit var mPresenter: CurrencyPresenter
    private var mTimerDisposable: Disposable? = null
    private var disposable: CompositeDisposable? = null

    private var mTextWatcher: EditTextWatcher? = null
    private var mAdapter: CurrencyAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPresenter = CurrencyPresenter(this@MainActivity)
        mTextWatcher = EditTextWatcher(mPresenter)
        this.initAdapter()
    }

    private fun initAdapter() {
        initHashMaps()
        mAdapter = CurrencyAdapter(this@MainActivity, this!!.mTextWatcher!!, mCurrencyIcons, mCurrencyNames)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.setAdapter(mAdapter)
        try {
            (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    private fun initHashMaps() {
        mCurrencyIcons = HashMap()
        mCurrencyNames = HashMap()
        val currencyMachineReadableNames = resources.getStringArray(R.array.machine_readable_currency_names)
        val currencyHumanReadableNames = resources.getStringArray(R.array.human_readable_currency_names)
        for (i in currencyMachineReadableNames.indices) {
            mCurrencyNames.put(currencyMachineReadableNames[i], currencyHumanReadableNames[i])
        }
    }

    override fun showError(errorText: String) {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this@MainActivity)
        // Set the alert dialog title
        builder.setTitle("Alert")
        // Display a message on alert dialog
        builder.setMessage(errorText)
        // Display a negative button on alert dialog
        builder.setNegativeButton("Close") { dialog, which ->
            finish()
        }
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }

    override fun updateRates(rates: MutableList<Rates>) {
        recyclerView.setVisibility(View.VISIBLE)
        if (!isCurrencyValueEditing) {
            if (mPresenter.getBaseCurrency() != null) {
                rates.add(0, Rates(mPresenter.getBaseCurrency(), mPresenter.getBaseCurrencyValue()))
            }
            mAdapter!!.updateData(rates)
        }
    }

    override fun onItemClick(isoName: String) {
        mPresenter.setBaseCurrency(isoName)
        mPresenter.setSelectedCurrency(isoName)
    }

    override fun onFocusChange(hasFocus: Boolean, v: View) {
        isCurrencyValueEditing = hasFocus
        if (hasFocus) {
            mPresenter.setSelectedCurrency(v.tag.toString())
        }

        if (!hasFocus) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        mTimerDisposable?.dispose()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getRates()
    }
}
