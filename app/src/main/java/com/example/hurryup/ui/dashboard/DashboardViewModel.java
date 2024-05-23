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
    private final MutableLiveData<float[]> mPercent;

    public DashboardViewModel() {
        mPercent = new MutableLiveData<>();
        mPercent.setValue(new float[]{0});
    }

    public LiveData<float[]> drawChart() {
        return mPercent;
    }
}
