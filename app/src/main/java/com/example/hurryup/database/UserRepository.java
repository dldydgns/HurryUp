package com.example.hurryup.database;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

public class UserRepository {
    private UserDao mUserDao;

    private LiveData<List<StateCount>> today_statecount;
    private LiveData<List<DayCount>> week_statecount;
    private LiveData<Integer> recentstate;

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
    public LiveData<List<StateCount>> getTodayStateCount() {
        today_statecount = mUserDao.getTodayStateCount();
        return today_statecount;
    }

    public LiveData<List<DayCount>> getWeekCountByState(int state) {
        week_statecount = mUserDao.getWeekCountByState(state);
        return week_statecount;
    }

    public LiveData<Integer> getRecentState() {
        recentstate = mUserDao.getRecentState();
        return recentstate;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(User user) {
        UserDB.databaseWriteExecutor.execute(() -> {
            try {
                mUserDao.insert(user);
            } catch (Exception e) {
                // 삽입이 실패함을 로그에 기록
                Log.e("Insertion", "Failed to insert user", e);
            }
        });
    }

    public void deleteAll() {
        UserDB.databaseWriteExecutor.execute(() -> {
            try {
                mUserDao.deleteAll();
            } catch (Exception e) {
                Log.e("Deletion", "Failed to delete all users", e);
            }
        });
    }
}
