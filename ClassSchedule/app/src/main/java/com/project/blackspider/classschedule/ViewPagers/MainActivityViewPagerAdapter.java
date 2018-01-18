package com.project.blackspider.classschedule.ViewPagers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.blackspider.classschedule.Fragments.AllChatsFragment;
import com.project.blackspider.classschedule.Fragments.AllNewsFeedPostsFragment;
import com.project.blackspider.classschedule.Fragments.AllSchedulePostsFragment;

import java.util.ArrayList;

/**
 * Created by blackSpider on 11/10/2016.
 */
public class MainActivityViewPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    private ArrayList<String> allInfo;
    private Bundle bundle;

    public MainActivityViewPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<String> allInfo) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.allInfo = allInfo;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AllNewsFeedPostsFragment tab1 = new AllNewsFeedPostsFragment();
                return tab1;
            case 1:
                AllSchedulePostsFragment tab2 = new AllSchedulePostsFragment();
                return tab2;
            case 2:
                bundle = new Bundle();
                bundle.putStringArrayList("allInfo", allInfo);
                AllChatsFragment tab3 = new AllChatsFragment();
                tab3.setArguments(bundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
