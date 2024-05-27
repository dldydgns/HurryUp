package com.example.hurryup.database;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

public class UserRepository {
    private UserDao mUserDao;

    private LiveData<List<StateCount>> today_statecount;
    private LiveData<Integer> week_statecount;

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

    public LiveData<Integer> getStateCountFromDay(int state, long timestamp, int day) {
        long startTime = getStartOfDay(timestamp, day); // 해당 날짜의 일요일 시작 시간
        long endTime = getEndOfDay(timestamp, day); // 해당 날짜의 일요일 끝 시간

        week_statecount = mUserDao.getStateCountFromTime(state, startTime, endTime);
        return week_statecount;
    }

    private long getStartOfDay(long timestamp, int day) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    private long getEndOfDay(long timestamp, int day) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
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
