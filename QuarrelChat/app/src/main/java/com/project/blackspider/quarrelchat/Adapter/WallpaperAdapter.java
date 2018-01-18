package com.project.blackspider.quarrelchat.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blackspider.quarrelchat.Interface.WallpaperAdapterListener;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.ViewHolder.WallpaperViewHolder;

import java.util.ArrayList;
import java.util.List;


public class WallpaperAdapter extends RecyclerView.Adapter {

    private List<Integer> wallpapers = new ArrayList<>();
    private String selectedWallpaper;
    private Context mContext;
    private SparseBooleanArray selectedItems;
    private Drawable drawable;

    public static WallpaperAdapterListener mListener;

    public WallpaperAdapter(Context context, WallpaperAdapterListener listener, String selectedWallpaper) {
        this.mContext = context;
        this.mListener = listener;
        this.selectedWallpaper = selectedWallpaper;
        selectedItems = new SparseBooleanArray();


        wallpapers.add(R.drawable.default_wallpaper);
        wallpapers.add(R.drawable.dragonfly);
        wallpapers.add(R.drawable.creative);
        wallpapers.add(R.drawable.flower_white);
        wallpapers.add(R.drawable.cat_wallpaper);
        wallpapers.add(R.drawable.love);
        wallpapers.add(R.drawable.indian);
        wallpapers.add(R.drawable.ice_cream);
        wallpapers.add(R.drawable.giraffe);
        wallpapers.add(R.drawable.lizard);
        wallpapers.add(R.drawable.plant);

        for (int i = 0; i<wallpapers.size(); i++){
            selectedItems.put(i, false);
        }
    }

    @Override
    public long getItemId(int position) {
        if(position==0) return 0;
        else {
            if(selectedItems.get(position)) return wallpapers.get(position);
            else return -1;
        }
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        if (holder == null) {
            return;
        }
        final WallpaperViewHolder wvh = (WallpaperViewHolder) holder;

        loadWallpaper(wvh.wallpaper, wallpapers.get(listPosition));

        if(selectedItems.get(listPosition)){
            drawable = ContextCompat.getDrawable(mContext, R.color.transparent_50);
            wvh.wallpaperContainer.setForeground(drawable);
            wvh.wallpaperSelected.setVisibility(View.VISIBLE);
        }else {
            drawable = ContextCompat.getDrawable(mContext, R.color.transparent_100);
            wvh.wallpaperContainer.setForeground(drawable);
            wvh.wallpaperSelected.setVisibility(View.GONE);
        }

        wvh.wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onWallpaperClicked(listPosition);
            }
        });
    }

    private void loadWallpaper(ImageView imgView, int imgId) {
        Glide.with(mContext).load(imgId)
                .thumbnail(0.4f)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgView);
    }

    public void toggleSelection(int position){
        boolean state = selectedItems.get(position);
        for (int i = 0; i<wallpapers.size(); i++){
            selectedItems.put(i, false);
        }
        selectedItems.put(position, !state);
        notifyDataSetChanged();
    }

}