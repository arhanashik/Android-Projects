package com.project.blackspider.classschedule.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;

import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;

public class AvaterSelectionActivity extends AppCompatActivity {
    private ImageView imageViewAvater11;
    private ImageView imageViewAvater12;
    private ImageView imageViewAvater13;
    private Button buttonAvaterSelection;

    private Bitmap bitmap;
    private Intent intent;

    private String imageString;
    private final int SELECTED = 1;
    private final int NOT_SELECTED = 0;
    private int imgSelected = NOT_SELECTED;

    private CustomImageConverter customImageConverter;
    private FinalVariables finalVariables;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avater_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select Avater");

        imageViewAvater11 = (ImageView) findViewById(R.id.imageViewAvater11);
        imageViewAvater12 = (ImageView) findViewById(R.id.imageViewAvater12);
        imageViewAvater13 = (ImageView) findViewById(R.id.imageViewAvater13);
        buttonAvaterSelection = (Button) findViewById(R.id.buttonAvaterSelection);
        customImageConverter = new CustomImageConverter();
        finalVariables = new FinalVariables();
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);
        intent = new Intent();


        imageViewAvater11.setImageBitmap(prepareBitmap(R.drawable.icon_avater1));
        imageViewAvater12.setImageBitmap(prepareBitmap(R.drawable.icon_avater2));
        imageViewAvater13.setImageBitmap(prepareBitmap(R.drawable.icon_avater3));

        imageViewAvater11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvater11.setAlpha(0.4f);
                imageViewAvater12.setAlpha(1f);
                imageViewAvater13.setAlpha(1f);
                bitmap =null;
                bitmap = getBitmapFromResource(R.drawable.icon_avater1);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 200);
                imgSelected = SELECTED;
            }
        });

        imageViewAvater12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvater11.setAlpha(1f);
                imageViewAvater12.setAlpha(0.4f);
                imageViewAvater13.setAlpha(1f);
                bitmap =null;
                bitmap = getBitmapFromResource(R.drawable.icon_avater2);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 200);
                imgSelected = SELECTED;
            }
        });

        imageViewAvater13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAvater11.setAlpha(1f);
                imageViewAvater12.setAlpha(1f);
                imageViewAvater13.setAlpha(0.4f);
                bitmap =null;
                bitmap = getBitmapFromResource(R.drawable.icon_avater3);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 200);
                imgSelected = SELECTED;
            }
        });

        buttonAvaterSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgSelected == SELECTED){

                    imageString = customImageConverter.convertBitmapIntoImageString(bitmap);
                    intent.putExtra("imageString", imageString);
                    setResult(RESULT_OK, intent);
                    finish();

                }else {
                    Toast.makeText(getApplicationContext(), "No Avater Selected", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public Bitmap prepareBitmap(int imgID){
        Bitmap bmp = null;
        bmp = getBitmapFromResource(imgID);
        bmp = customImageConverter.getResizedBitmap(bmp, 100);
        bmp = customImageConverter.getCircledBitmap(bmp);

        return bmp;
    }

    public Bitmap getBitmapFromResource(int image_id){
        Bitmap bmp = null;
        bmp = BitmapFactory.decodeResource(getResources(), image_id);

        return bmp;
    }

}
