package com.example.hurryup.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class UserDB extends RoomDatabase {
    public abstract UserDao userDao();

    private static volatile UserDB INSTANCE = null;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserDB getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (UserDB.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDB.class, "user.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
