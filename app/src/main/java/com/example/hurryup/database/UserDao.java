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

    @Query("SELECT COUNT(id) FROM User WHERE state = :state AND timestamp >= :startOfTime AND timestamp <= :endOfTime")
    LiveData<Integer> getStateCountFromTime(int state, long startOfTime, long endOfTime);
}