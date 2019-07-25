package com.example.popmovies;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, int position, boolean hasLong);
}
