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

    @Query("SELECT state, COUNT(id) FROM User WHERE DATE(timestamp / 1000, 'unixepoch') = DATE('now') AND state = :state GROUP BY state")
    LiveData<int[]> getTodayStateCount(int state);

    @Query("SELECT state, COUNT(id) FROM User WHERE strftime('%Y-%W', timestamp / 1000, 'unixepoch') = strftime('%Y-%W', 'now') AND state = :state GROUP BY state")
    LiveData<int[]> getWeekStateCount(int state);
}