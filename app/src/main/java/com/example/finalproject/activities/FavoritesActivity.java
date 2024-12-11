package com.example.finalproject.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.finalproject.database.AppDatabase;
import com.example.finalproject.database.NasaImage;
import com.example.finalproject.R;
import com.example.finalproject.adapters.FavoritesAdapter;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerView_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Room Database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "nasa-database").build();

        // Load data from database
        new Thread(() -> {
            List<NasaImage> nasaImages = db.nasaImageDao().getAllImages();
            runOnUiThread(() -> {
                FavoritesAdapter adapter = new FavoritesAdapter(this, nasaImages, db);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
