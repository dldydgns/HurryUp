package com.example.hurryup.ui.home;

import android.app.Application;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.hurryup.database.Converters;
import com.example.hurryup.database.User;
import com.example.hurryup.database.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    UserRepository userRepository;
    private final MutableLiveData<Bias> mCircleBias;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);

        mCircleBias = new MutableLiveData<>(new Bias(0.5, 0.5));

        userRepository.getRecentState().observeForever(state -> {
            if(state != null) {
                mCircleBias.setValue(new Bias(state));
            }
        });
    }

    public LiveData<Bias> getCircleBias() {
        return mCircleBias;
    }
}

