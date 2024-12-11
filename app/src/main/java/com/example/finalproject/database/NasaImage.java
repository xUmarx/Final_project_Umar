package com.example.finalproject.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NasaImage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public String imageUrl;
    public String hdUrl;
    public String title;
}
