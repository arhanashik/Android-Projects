package com.project.blackspider.quarrelchat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.R;

/**
 * Created by Mr blackSpider on 8/26/2017.
 */

public class WallpaperViewHolder extends RecyclerView.ViewHolder{
    public FrameLayout wallpaperContainer;
    public ImageView wallpaper, wallpaperSelected;

    public WallpaperViewHolder(View itemView) {
        super(itemView);

        this.wallpaperContainer = (FrameLayout) itemView.findViewById(R.id.wallpaper_container);
        this.wallpaper = (ImageView) itemView.findViewById(R.id.img_wallpaper);
        this.wallpaperSelected = (ImageView) itemView.findViewById(R.id.wallpaper_selected);
    }
}
