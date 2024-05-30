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
            "COUNT(CASE WHEN state = :state THEN 1 ELSE NULL END) * 100.0 / COUNT(*) as ratio " +
            "FROM User " +
            "WHERE DATE('now','-7 days','start of day') < DATE(timestamp / 1000, 'unixepoch')" +
            "GROUP BY day " +
            "ORDER BY day")
    LiveData<List<DayCount>> getWeekCountByCount(int state);
}