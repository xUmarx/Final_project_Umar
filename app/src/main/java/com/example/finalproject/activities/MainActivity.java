package com.example.finalproject.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finalproject.database.AppDatabase;
import com.example.finalproject.database.NasaImage;
import com.example.finalproject.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button btnPickDate, btnSaveFavorite, btnFavorites;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textViewDate;


    private AppDatabase db; // Room Database instance

    private final String NASA_API_KEY = "tIV5cLA8pRTMNhN53LLp5tcF0SfgFP6SxUSy0Ggx";
    private String currentHdUrl = null; // Store the HD URL for saving

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        btnPickDate = findViewById(R.id.btn_pick_date);
        btnSaveFavorite = findViewById(R.id.btn_save_favorite);
        btnFavorites = findViewById(R.id.btn_favorites); // Favorites button
        progressBar = findViewById(R.id.progress_bar);
        imageView = findViewById(R.id.image_view);
        textViewDate = findViewById(R.id.text_view_date);

        // Initialize Room Database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "nasa-database").build();

        // Set up DatePicker button
        btnPickDate.setOnClickListener(v -> showDatePicker());

        // Set up Save to Favorites button
        btnSaveFavorite.setOnClickListener(v -> saveToFavorites());

        // Set up View Favorites button
        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Format selected date
                    String selectedDate = selectedYear + "-" +
                            String.format("%02d", (selectedMonth + 1)) + "-" +
                            String.format("%02d", selectedDay);
                    textViewDate.setText(selectedDate);

                    // Fetch NASA image for selected date
                    fetchNasaImage(selectedDate);
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void fetchNasaImage(String date) {
        // Show ProgressBar while fetching
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // NASA API endpoint
        String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + NASA_API_KEY + "&date=" + date;

        new AsyncTask<String, Void, Bitmap>() {
            private String hdUrl;

            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    // Fetch JSON from NASA API
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    StringBuilder response = new StringBuilder();
                    int data;
                    while ((data = inputStream.read()) != -1) {
                        response.append((char) data);
                    }
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String imageUrl = jsonObject.getString("url");
                    hdUrl = jsonObject.optString("hdurl", imageUrl);

                    // Fetch the image bitmap
                    URL imageUrlObj = new URL(imageUrl);
                    HttpURLConnection imageConnection = (HttpURLConnection) imageUrlObj.openConnection();
                    imageConnection.connect();
                    return BitmapFactory.decodeStream(imageConnection.getInputStream());

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                progressBar.setVisibility(ProgressBar.GONE);

                if (result != null) {
                    imageView.setImageBitmap(result);
                    currentHdUrl = hdUrl;
                    Toast.makeText(MainActivity.this, "Image loaded successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load image!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(apiUrl);
    }

    private void saveToFavorites() {
        if (currentHdUrl != null && !currentHdUrl.isEmpty()) {
            String date = textViewDate.getText().toString();

            // Create a new NasaImage object
            NasaImage image = new NasaImage();
            image.date = date;
            image.imageUrl = currentHdUrl;
            image.title = date;

            // Save to Room Database
            new Thread(() -> {
                db.nasaImageDao().insert(image);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Image saved to favorites!", Toast.LENGTH_SHORT).show());
            }).start();
        } else {
            Toast.makeText(this, "No image to save!", Toast.LENGTH_SHORT).show();
        }
    }

}
