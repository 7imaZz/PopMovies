package com.example.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolderView>{

    public class ReviewHolderView extends RecyclerView.ViewHolder{

        private TextView nameTextView;
        private TextView contentTextView;

        public ReviewHolderView(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_user_name);
            contentTextView = itemView.findViewById(R.id.tv_content);
        }
    }


    private Context context;
    private ArrayList<Review> reviews;

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ReviewHolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolderView holder, int position) {

        Review review = reviews.get(position);

        holder.nameTextView.setText(review.getName());
        holder.contentTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


}
