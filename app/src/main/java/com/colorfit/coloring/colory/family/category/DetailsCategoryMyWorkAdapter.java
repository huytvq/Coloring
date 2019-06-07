package com.colorfit.coloring.colory.family.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.colorfit.coloring.colory.family.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsCategoryMyWorkAdapter extends RecyclerView.Adapter<DetailsCategoryMyWorkAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mCategories;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public void setOnRecycleViewItemClickListener(OnRecycleViewItemClickListener onRecycleViewItemClickListener) {
        this.onRecycleViewItemClickListener = onRecycleViewItemClickListener;
    }

    public DetailsCategoryMyWorkAdapter(Context context, ArrayList<String> categories) {
        mContext = context;
        mCategories = categories;
    }

    @NonNull
    @Override
    public DetailsCategoryMyWorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.item_details_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DetailsCategoryMyWorkAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext).load(mCategories.get(position)).into(holder.mImageCategory);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImageCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageCategory = itemView.findViewById(R.id.image_category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRecycleViewItemClickListener != null)
                        onRecycleViewItemClickListener.recycleViewItemClickListener(mImageCategory, mCategories.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnRecycleViewItemClickListener {
        void recycleViewItemClickListener(View view, String file);
    }
}
