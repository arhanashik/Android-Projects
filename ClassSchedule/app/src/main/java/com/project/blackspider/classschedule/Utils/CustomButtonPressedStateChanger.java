package com.project.blackspider.classschedule.Utils;

import android.view.MotionEvent;
import android.view.View;
import android.content.Context;

/**
 * Created by Mr blackSpider on 12/15/2016.
 */

public class CustomButtonPressedStateChanger {
    private Context context;

    public CustomButtonPressedStateChanger(Context context){
        this.context = context;

    }

    public void buttonPressStateWithOnTouchMethod(final View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.4f);
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1f);
                        break;

                    case MotionEvent.ACTION_BUTTON_PRESS:
                        view.setAlpha(0.4f);
                        break;

                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        view.setAlpha(1f);
                        break;

                    case MotionEvent.ACTION_HOVER_ENTER:
                        view.setAlpha(0.4f);
                        break;

                    case MotionEvent.ACTION_HOVER_MOVE:
                        view.setAlpha(0.4f);
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                        view.setAlpha(1f);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }
}
