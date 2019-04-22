package com.rohail.currencyconverter.utils;

import android.text.Editable;
import android.text.TextWatcher;
import com.rohail.currencyconverter.activities.CurrencyPresenter;

public class EditTextWatcher implements TextWatcher {

    private CurrencyPresenter mPresenter;
    private double mEndValue = 1d;

    public EditTextWatcher(CurrencyPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try{
            mEndValue = Double.valueOf(s.toString());
        }catch (NumberFormatException exc){
            exc.printStackTrace();
        }
        mPresenter.setSelectedCurrencyValue(mEndValue);
    }
}
