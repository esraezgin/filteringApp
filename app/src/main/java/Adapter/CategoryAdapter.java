package Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Model.Category;
import esrae.grass.com.filteringapp.MainActivity;
import esrae.grass.com.filteringapp.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private ArrayList<Category> categories;
    private int row_index = 0;
    private OnTextClickListener onTextClickListener;
    public static Boolean click=false;


    public interface OnTextClickListener {
        void onTextClick(String data);
    }

    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, parent, false);
        return new CategoryHolder(view);
    }

    public CategoryAdapter(ArrayList<Category> categories,OnTextClickListener onTextClickListener) {
        this.categories = categories;
        this.onTextClickListener=onTextClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, final int position) {

        final Category items = categories.get(position);
        holder.catagoriesName.setText(items.getCategory_name());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click=true;
                row_index = position;
                notifyDataSetChanged();
                onTextClickListener.onTextClick(items.getCategory_name());
            }
        });


        if (row_index == position) {
            holder.catagoriesName.setTextColor(Color.BLACK);
        } else {
            holder.catagoriesName.setTextColor(Color.parseColor("#939797"));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        TextView catagoriesName;

        public CategoryHolder(@NonNull final View itemView) {
            super(itemView);
            catagoriesName = itemView.findViewById(R.id.categoryTypeTV);
        }
    }
}
