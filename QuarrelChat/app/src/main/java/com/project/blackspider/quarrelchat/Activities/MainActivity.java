package com.project.blackspider.quarrelchat.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Fragments.Account_Fragment;
import com.project.blackspider.quarrelchat.Fragments.Notifications_Fragment;
import com.project.blackspider.quarrelchat.Fragments.Search_Soulmates_Fragment;
import com.project.blackspider.quarrelchat.Fragments.Soulmates_Fragment;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CustomImageConverter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView navigation;
    public static Menu navigationMenu;
    private static TextView actionBarTitle;
    public static ImageView optionMenu;

    public static FragmentManager fragmentManager;
    public static final int left = 0;
    public static final int right = 1;
    public static int nextTransitionDirection = left;

    public static Bitmap bitmap;
    public static PopupMenu popupMenu;

    public static ArrayList<String> myInfo = new ArrayList<>();

    public static CustomImageConverter customImageConverter;

    // # milliseconds, desired time passed between two back presses.
    private static final int TIME_INTERVAL = 2500;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=21)
        {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.shared_element_transation));
        }

        init();
        setListener();

        if (savedInstanceState == null) {
            setTitle("Soulmates");
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content, new Soulmates_Fragment(),
                            FinalVariables.Soulmates_Fragment).commit();
        }
    }

    private void init(){
        myInfo = getIntent().getStringArrayListExtra("my_info");

        actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        actionBarTitle.setText("Soulmates");

        optionMenu = (ImageView) findViewById(R.id.option_menu);
        popupMenu = new PopupMenu(this, optionMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_view_type, popupMenu.getMenu());
        setForceShowIcon(popupMenu);

        fragmentManager = getSupportFragmentManager();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //do nothing
            }
        });
        navigationMenu = navigation.getMenu();
    }

    private void setListener(){
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_list:
                        Soulmates_Fragment.setListView();
                        break;
                    case R.id.action_grid:
                        Soulmates_Fragment.setGridView();
                        break;
                    case R.id.action_extended_list:
                        Soulmates_Fragment.setExtendedListView();
                        break;
                }
                return true;
            }
        });
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_soulmates:
                    actionBarTitle.setText("Soulmates");
                    optionMenu.setVisibility(View.VISIBLE);
                    nextTransitionDirection = left;
                    replaceFragment(new Soulmates_Fragment(),
                            FinalVariables.Soulmates_Fragment);
                    return true;

                case R.id.navigation_search:
                    actionBarTitle.setText("Search");
                    optionMenu.setVisibility(View.GONE);
                    replaceFragment(new Search_Soulmates_Fragment(),
                            FinalVariables.Search_Soulmates_Fragment);
                    return true;

                case R.id.navigation_notifications:
                    actionBarTitle.setText("Notifications");
                    optionMenu.setVisibility(View.GONE);
                    replaceFragment(new Notifications_Fragment(),
                            FinalVariables.Notification_Fragment);
                    return true;

                case R.id.navigation_account:
                    actionBarTitle.setText("Account");
                    optionMenu.setVisibility(View.GONE);
                    nextTransitionDirection = right;
                    replaceFragment(new Account_Fragment(),
                            FinalVariables.Account_Fragment);

                    return true;
            }
            return false;
        }

    };

    // Replace Login Fragment with animation
    public void replaceFragment(Fragment fragment, String tag) {
        fragmentManager
                .beginTransaction()
                //.setCustomAnimations(animIn, animOut)
                .replace(R.id.content, fragment,
                        tag).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments){
            if(fragment != null ) fragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            //super.onBackPressed();
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
            return;
        }
        else {
            Snackbar.make(navigation, "Click again to exit", Snackbar.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

}
