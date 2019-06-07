package com.colorfit.coloring.colory.family.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.colorfit.coloring.colory.family.R;
import com.colorfit.coloring.colory.family.model.Category;
import com.colorfit.coloring.colory.family.model.GridViewActivityModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<Category> mCategories;
    private OnClickListener mOnClickListener;

    public CategoryAdapter(Context context, List<Category> categories) {
        mContext = context;
        mCategories = categories;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_category, parent, false);

        return new ViewHolder(itemView);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        if (mCategories != null) {
            String path = "assets://Image/";
            holder.mTitleCategory.setText(mCategories.get(position).getName());
            if (mCategories.get(position).getName().equalsIgnoreCase("MyWork")) {
                Glide.with(mContext).load(mCategories.get(position).getPhoto()).into(holder.mImageCategory);
            } else {
                GridViewActivityModel.getInstance().showGridLocalImageAsyn(holder.mImageCategory, path + mCategories.get(position).getPhoto());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleCategory;
        CircleImageView mImageCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleCategory = itemView.findViewById(R.id.text_title);
            mImageCategory = itemView.findViewById(R.id.image_category);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(mCategories.get(getAdapterPosition()).getName());
                    }
                }
            });
        }
    }

    public interface OnClickListener {
        void onClick(String title);
    }
}
