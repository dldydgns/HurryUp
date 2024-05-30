package com.example.hurryup.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
    @Delete
    void delete(User user);
    @Query("DELETE FROM User")
    void deleteAll(); // 모든 사용자 데이터 삭제

    @Query("SELECT state, COUNT(*) AS count FROM User WHERE DATE(timestamp / 1000, 'unixepoch') = DATE('now') GROUP BY state ORDER BY state")
    LiveData<List<StateCount>> getTodayStateCount();

    @Query("SELECT strftime('%w', datetime(timestamp / 1000, 'unixepoch')) as day, " +
            "       (COUNT(CASE WHEN state = :state THEN 1 ELSE NULL END) * 1.0 / " +
            "       (SELECT COUNT(*) FROM User u2 WHERE strftime('%w', datetime(u2.timestamp / 1000, 'unixepoch')) = strftime('%w', datetime(User.timestamp / 1000, 'unixepoch')))) * 100 AS ratio " +
            "FROM User " +
            "GROUP BY day " +
            "HAVING User.timestamp >= strftime('%s', 'now', '-6 days')" +
            "ORDER BY day")
    LiveData<List<DayCount>> getWeekCountByCount(int state);
}