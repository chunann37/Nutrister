package com.example.nutrister.ui.suggest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SuggestViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SuggestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is suggest fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}