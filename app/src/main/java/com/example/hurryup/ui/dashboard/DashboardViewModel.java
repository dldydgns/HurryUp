package com.example.hurryup.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hurryup.database.Converters;
import com.example.hurryup.database.User;
import com.example.hurryup.database.UserDB;
import com.example.hurryup.database.UserRepository;

import java.util.Date;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
