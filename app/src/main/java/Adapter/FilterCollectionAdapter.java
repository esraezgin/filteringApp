package Adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.FilterModel;
import esrae.grass.com.filteringapp.MainActivity;
import esrae.grass.com.filteringapp.R;

public class FilterCollectionAdapter extends RecyclerView.Adapter<FilterCollectionAdapter.FilterView> {

    private List<FilterModel> filterModelsList;
    private OnItemClickListener mListener;
    private int row_index = 0;
    MainActivity mainActivity;

    public FilterCollectionAdapter(List<FilterModel> filterModels) {
        this.filterModelsList = filterModels;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener=listener;
        mainActivity=new MainActivity();
    }

    @Override
    public FilterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_cat_items, parent, false);
        return new FilterView(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final FilterView holder, final int position) {
        FilterModel item= filterModelsList.get(position);
        holder.filterName.setText(item.getString());
        holder.filteredImage.setImageBitmap(item.getBitmap());

        if (mainActivity.change_pos == position) {
            holder.filterName.setBackgroundColor(Color.parseColor("#74CE14"));
        } else {
            holder.filterName.setBackgroundColor(Color.parseColor("#8F512866"));
        }



    }

    @Override
    public int getItemCount() {
        return filterModelsList.size();
    }

    public static class FilterView extends RecyclerView.ViewHolder {

       public TextView filterName;
         public ImageView filteredImage;

        public FilterView(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            filteredImage=itemView.findViewById(R.id.filteredImageTV);
            filterName=itemView.findViewById(R.id.filteredTextTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null)
                    {
                        int pos=getAdapterPosition();
                        if(pos !=RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
