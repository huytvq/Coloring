package com.colorfit.coloring.colory.family.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.colorfit.coloring.colory.family.MyApplication;
import com.colorfit.coloring.colory.family.R;
import com.colorfit.coloring.colory.family.model.GridViewActivityModel;
import com.colorfit.coloring.colory.family.model.PictureBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsCategoryAdapter extends RecyclerView.Adapter<DetailsCategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<PictureBean.Picture> mCategories;
    private String mCategory;

    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public void setOnRecycleViewItemClickListener(OnRecycleViewItemClickListener onRecycleViewItemClickListener) {
        this.onRecycleViewItemClickListener = onRecycleViewItemClickListener;
    }

    public DetailsCategoryAdapter(Context context, List<PictureBean.Picture> categories, String category) {
        mContext = context;
        mCategories = categories;
        this.mCategory = category;
    }

    @NonNull
    @Override
    public DetailsCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.item_details_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DetailsCategoryAdapter.ViewHolder holder, final int position) {
        String url = null;
        String path = null;
        if (mCategory.equalsIgnoreCase("Animal")) {
            path = MyApplication.DATALOCATION + MyApplication.ANIMAL;
        } else if (mCategory.equalsIgnoreCase("Animal Cute")) {
            path = MyApplication.DATALOCATION + MyApplication.ANIMAL_CUTE;
        } else if (mCategory.equalsIgnoreCase("Flower")) {
            path = MyApplication.DATALOCATION + MyApplication.FLOWER;
        } else if (mCategory.equalsIgnoreCase("Girl")) {
            path = MyApplication.DATALOCATION + MyApplication.GIRL;
        } else if (mCategory.equalsIgnoreCase("Art Flower")) {
            path = MyApplication.DATALOCATION + MyApplication.ART_FLOWER;
        } else if (mCategory.equalsIgnoreCase("Mandalas")) {
            path = MyApplication.DATALOCATION + MyApplication.MANDALAS;
        } else if (mCategory.equalsIgnoreCase("Matrix")) {
            path = MyApplication.DATALOCATION + MyApplication.MATRIX;
        }
        url = path + mCategories.get(position).getUri();
        GridViewActivityModel.getInstance().showGridLocalImageAsyn(holder.mImageCategory, url);
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
                        onRecycleViewItemClickListener.recycleViewItemClickListener(mImageCategory, getAdapterPosition());
                }
            });
        }
    }

    public interface OnRecycleViewItemClickListener {
        void recycleViewItemClickListener(View view, int i);
    }
}
