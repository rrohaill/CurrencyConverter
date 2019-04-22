package com.rohail.currencyconverter.adapter

import android.graphics.drawable.Drawable
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rohail.currencyconverter.R
import com.rohail.currencyconverter.data_files.CurrencyModel
import com.rohail.currencyconverter.data_files.Rates
import com.rohail.currencyconverter.interfaces.IOnItemClickListener
import com.rohail.currencyconverter.utils.EditTextWatcher
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import java.lang.Math.round
import java.util.ArrayList
import java.util.HashMap

class CurrencyAdapter(
    private val onItemClickListener: IOnItemClickListener,
    private val editTextWatcher: EditTextWatcher,
    private val currencyIcons: HashMap<String, Drawable>,
    private val items: HashMap<String, String>
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    private val mRatesList = ArrayList<Rates>()
    private val UNDEFINED_NAME = "-"

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        val view = inflater.inflate(R.layout.recyclerview_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rate: Rates = mRatesList.get(position)
        val isoKey = rate.isoName

        if (items.containsKey(isoKey)) {
            holder.bindView(
                rate,
                onItemClickListener,
                items.get(isoKey)!!,
                editTextWatcher
            )
        } else {
            holder.bindView(
                rate,
                onItemClickListener,
                items.get(UNDEFINED_NAME)!!,
                editTextWatcher
            )
        }
    }

    fun updateData(items: List<Rates>) {
        if (mRatesList.size == 0) {
            mRatesList.addAll(items)
            notifyItemRangeInserted(0, items.size)
        } else if (mRatesList.size == items.size) {
            mRatesList.clear()
            mRatesList.addAll(items)
            notifyItemRangeChanged(0, items.size)
        } else {
            mRatesList.clear()
            mRatesList.addAll(items)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mRatesList?.size!!
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var mLayout: ConstraintLayout? = null
        private var mCurrencyIsoName: TextView? = null
        private var mCurrencyName: TextView? = null
        private var mCurrencyLetter: TextView? = null
        private var mInputCurrencyValue: EditText? = null

        init {
            mLayout = itemView.main_layout
            mCurrencyIsoName = itemView.currency_iso_tv
            mCurrencyName = itemView.currency_name_tv
            mCurrencyLetter = itemView.country_flag_tv
            mInputCurrencyValue = itemView.currency_value
        }

        fun bindView(
            currencyRate: Rates,
            onItemClickListener: IOnItemClickListener,
            currencyName: String,
            textWatcher: EditTextWatcher
        ) {
            mCurrencyIsoName!!.text = currencyRate.isoName
            mCurrencyName!!.text = currencyName
            mCurrencyLetter!!.text = currencyRate.isoName.substring(0, 1)

            initCurrencyValue(currencyRate, onItemClickListener, textWatcher)
            mLayout!!.setOnClickListener { v ->
                v.isFocusableInTouchMode = true
                v.requestFocus()
                v.isFocusableInTouchMode = false
                onItemClickListener.onItemClick(currencyRate.isoName)
            }
        }

        private fun initCurrencyValue(
            currencyRate: Rates,
            onItemClickListener: IOnItemClickListener,
            textWatcher: EditTextWatcher
        ) {
            mInputCurrencyValue!!.removeTextChangedListener(textWatcher)
            mInputCurrencyValue!!.text =
                Editable.Factory.getInstance().newEditable(String.format("%." + 2 + "f", currencyRate.isoRate))
            mInputCurrencyValue!!.addTextChangedListener(textWatcher)
            mInputCurrencyValue!!.tag = currencyRate.isoName
            mInputCurrencyValue!!.setOnFocusChangeListener { v, hasFocus ->
                onItemClickListener.onFocusChange(
                    hasFocus,
                    v
                )
            }

            mInputCurrencyValue!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mInputCurrencyValue!!.clearFocus()
                    return@OnEditorActionListener true
                }
                false
            })

            try {
                mInputCurrencyValue!!.setSelection(mInputCurrencyValue!!.text.length)
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }
    }

}