package com.example.hurryup.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class User{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public long timestamp;
    public int state;

    private float x;
    private  float y;

}