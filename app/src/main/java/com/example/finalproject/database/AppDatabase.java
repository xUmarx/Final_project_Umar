package com.example.finalproject.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//@Database(entities = {NasaImage.class}, version = 1)
@Database(entities = {NasaImage.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract NasaImageDao nasaImageDao();
}
