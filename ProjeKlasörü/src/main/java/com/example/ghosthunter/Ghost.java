package com.example.ghosthunter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class Ghost {
    private final ImageView imageView;
    private final int points;
    private final int durationMs;
    private final String type;

    public Ghost(ImageView imageView, int points, int durationMs, String type) {
        this.imageView = imageView;
        this.points = points;
        this.durationMs = durationMs;
        this.type = type;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getPoints() {
        return points;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public String getType() {
        return type;
    }
}