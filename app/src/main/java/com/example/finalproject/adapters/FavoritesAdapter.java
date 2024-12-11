package com.example.finalproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.database.AppDatabase;
import com.example.finalproject.database.NasaImage;

import java.net.URL;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final Context context;
    private final List<NasaImage> nasaImages;
    private final AppDatabase db;

    public FavoritesAdapter(Context context, List<NasaImage> nasaImages, AppDatabase db) {
        this.context = context;
        this.nasaImages = nasaImages;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NasaImage image = nasaImages.get(position);

        // Set title and date
        holder.textViewTitle.setText(image.title);
        holder.textViewDate.setText(image.date);

        // Load image with resizing
        new Thread(() -> {
            try {
                URL url = new URL(image.imageUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
                holder.imageView.post(() -> holder.imageView.setImageBitmap(resizedBitmap));
            } catch (Exception e) {
                e.printStackTrace();
                holder.imageView.post(() -> holder.imageView.setImageResource(R.drawable.placeholder_image));
            }
        }).start();

        // Delete button logic
        holder.buttonDelete.setOnClickListener(v -> {
            new Thread(() -> {
                db.nasaImageDao().delete(image);
                runOnUiThread(() -> {
                    nasaImages.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, nasaImages.size());
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return nasaImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewDate;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_favorite);
            textViewTitle = itemView.findViewById(R.id.text_item_title);
            textViewDate = itemView.findViewById(R.id.text_item_date);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }

    private void runOnUiThread(Runnable action) {
        ((android.app.Activity) context).runOnUiThread(action);
    }
}
