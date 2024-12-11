package com.example.finalproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NasaImageDao {
    @Insert
    void insert(NasaImage image);

    @Query("SELECT * FROM NasaImage")
    List<NasaImage> getAllImages();

    @Delete
    void delete(NasaImage image);
}
