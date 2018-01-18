package com.project.blackspider.musician.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.blackspider.musician.model.Audio;
import com.project.blackspider.musician.model.Playlist;
import com.project.blackspider.musician.variables.FinalVariables;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Mr blackSpider on 9/4/2017.
 */

public class StorageUtil {
    private SharedPreferences preferences;
    private Context context;

    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storePlaylistTable(List<Playlist> playlistTable) {
        preferences = context.getSharedPreferences(FinalVariables.PLAY_LIST_TABLE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlistTable);
        editor.putString("audioPlayListTable", json);
        editor.apply();
    }

    public List<Playlist> loadPlaylistTable() {
        preferences = context.getSharedPreferences(FinalVariables.PLAY_LIST_TABLE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioPlayListTable", null);
        Type type = new TypeToken<List<Playlist>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storePlaylist(List<Integer> playlist) {
        preferences = context.getSharedPreferences(FinalVariables.PLAY_LIST, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        editor.putString("audioPlayList", json);
        editor.apply();
    }

    public List<Playlist> loadPlaylist() {
        preferences = context.getSharedPreferences(FinalVariables.PLAY_LIST, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioPlayList", null);
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeAudio(List<Audio> audioList) {
        preferences = context.getSharedPreferences(FinalVariables.STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(audioList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }

    public List<Audio> loadAudio() {
        preferences = context.getSharedPreferences(FinalVariables.STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<List<Audio>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(FinalVariables.STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(FinalVariables.STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void storeLastAudioIndex(int index) {
        preferences = context.getSharedPreferences(FinalVariables.INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadLastAudioIndex() {
        preferences = context.getSharedPreferences(FinalVariables.INFO, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", 0);//return 0 if no data found
    }

    public void storeLastAudioResumePosition(int resumePosition) {
        preferences = context.getSharedPreferences(FinalVariables.INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("resumePosition", resumePosition);
        editor.apply();
    }

    public int loadLastAudioResumePosition() {
        preferences = context.getSharedPreferences(FinalVariables.INFO, Context.MODE_PRIVATE);
        return preferences.getInt("resumePosition", 0);//return 0 if no data found
    }

    public void storeAudioPlayStyle(int playStyle) {
        preferences = context.getSharedPreferences(FinalVariables.INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playStyle", playStyle);
        editor.apply();
    }

    public int loadAudioPlayStyle() {
        preferences = context.getSharedPreferences(FinalVariables.INFO, Context.MODE_PRIVATE);
        return preferences.getInt("playStyle", FinalVariables.REPEAT_ALL);//return 0 if no data found
    }

    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(FinalVariables.STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
