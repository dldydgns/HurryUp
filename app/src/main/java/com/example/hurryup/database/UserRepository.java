package com.example.hurryup.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UserRepository {
    private UserDao mUserDao;

    private LiveData<int[]> today_statecount;
    private LiveData<int[]> week_statecount;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UserRepository(Context context) {
        UserDB db = UserDB.getDatabase(context);
        mUserDao = db.userDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<int[]> getTodayStateCount(int state) {
        today_statecount = mUserDao.getTodayStateCount(state);
        return today_statecount;
    }
    public LiveData<int[]> getWeekStateCount(int state) {
        week_statecount = mUserDao.getWeekStateCount(state);
        return week_statecount;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(User user) {
        UserDB.databaseWriteExecutor.execute(() -> {
            mUserDao.insert(user);
        });
    }
}
