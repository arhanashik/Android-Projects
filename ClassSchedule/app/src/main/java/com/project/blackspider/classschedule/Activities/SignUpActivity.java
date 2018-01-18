package com.project.blackspider.classschedule.Activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.classschedule.Facebook.AccessingFacebook;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Utils.ImageSelection;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Utils.DateAndTime;
import com.project.blackspider.classschedule.Utils.UploadFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.project.blackspider.classschedule.R;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText editTextName;
    private EditText editTextID;
    private EditText editTextReg;
    private Spinner facultySelectionSpinner;
    private Spinner sessionSelectionSpinner;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextImagePath;
    private ImageButton buttonCamera;
    private Button buttonCreateID;
    private View toastView;
    private TextView textViewToastMessage;
    private Toast toast;
    private Intent intent;

    private List<String> allFaculty;
    private List<String> allSession;
    private ArrayAdapter<String> adapter;
    private LayoutInflater layoutInflater;
    private Bitmap bitmap;
    private ProgressDialog pd;

    private String userGivenName;
    private String userGivenID;
    private String userGivenReg;
    private String userGivenFaculty;
    private String userGivenSession;
    private String userGivenPhone;
    private String userGivenEmail;
    private String userGivenPassword;
    private String userGivenConfirmPassword;
    private String userGivenImagePath;
    private String userDeviceRegID;
    private String userStatus;
    private String spinnerSelectedFaculty;
    private String spinnerSelectedSession;
    private String imageName;
    private String encodedImage = "";
    private Uri fileUri;

    private CustomButtonPressedStateChanger customeButtonPressedStateChanger;
    private FinalVariables finalVariables;
    private CustomAlertDialogBox customAlertDialogBox;
    private ImageSelection imageSelection;
    private DBHelper dbHelper;
    private User user;
    private UploadFile uploadFile;
    private DateAndTime dateAndTime;
    private CustomImageConverter customImageConverter;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private LoginButton loginButton;
    public static CallbackManager callbackManager;
    public static AccessTokenTracker accessTokenTracker;
    public static ProfileTracker profileTracker;
    public static ShareDialog shareDialog;
    public FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.e("Graph req(object): ", object.toString());
                    Log.e("Graph req(response):", response.toString());
                    showFbLoginResult(object);
                }
            });
            Bundle params = new Bundle();
            params.putString("fields", "id, first_name, last_name, email, birthday, gender");
            graphRequest.setParameters(params);
            graphRequest.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            error.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFields();
        prepareData();
        facultySelectionSpinner.setOnItemSelectedListener(this);
        sessionSelectionSpinner.setOnItemSelectedListener(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allFaculty);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        facultySelectionSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allSession);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        sessionSelectionSpinner.setAdapter(adapter);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectImage();
                galleryPermissionDialog(FinalVariables.GALLERY_ACCESS_PERMISSION_REQUEST_CODE);
            }
        });

        buttonCreateID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createID();

            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                //showFbLoginResult(currentProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        callbackManager = CallbackManager.Factory.create();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_fb_login_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutInflater = LayoutInflater.from(SignUpActivity.this);
                View v = layoutInflater.inflate(R.layout.prompts_connect_fb, null);
                loginButton = (LoginButton) v.findViewById(R.id.fb_login_button);
                //loginButton.setReadPermissions("email","user_birthday");
                //loginButton.registerCallback(callbackManager, callback);
                AccessingFacebook accessingFacebook = new AccessingFacebook(SignUpActivity.this);
                accessingFacebook.setUpFbLogin(loginButton);
                alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                alertDialogBuilder.setView(v);
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFields(){
        editTextName = (EditText) findViewById(R.id.nameEditText);
        editTextID = (EditText) findViewById(R.id.idEditText);
        editTextReg = (EditText) findViewById(R.id.regEditText);
        facultySelectionSpinner = (Spinner) findViewById(R.id.facultySelectionSpinner);
        sessionSelectionSpinner = (Spinner) findViewById(R.id.sessionSelectionSpinner);
        editTextPhone = (EditText) findViewById(R.id.phoneNumberEditText);
        editTextEmail = (EditText) findViewById(R.id.emailEditText);
        editTextPassword = (EditText) findViewById(R.id.passwordEditText);
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmPasswordEditText);
        editTextImagePath = (EditText) findViewById(R.id.imageSelectionEditText);
        buttonCamera = (ImageButton) findViewById(R.id.imageSelectionButton);
        buttonCreateID = (Button) findViewById(R.id.createIDButton);

        customeButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);
        customeButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonCamera);
        customeButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonCreateID);

        layoutInflater = getLayoutInflater();
        toastView = layoutInflater.inflate(R.layout.view_toast,
                (ViewGroup) findViewById(R.id.custom_toast_layout));
        textViewToastMessage = (TextView) toastView.findViewById(R.id.textViewToastMessage);
        toast = new Toast(getApplicationContext());
        toast.setView(toastView);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);

        finalVariables = new FinalVariables();

        userGivenName = "";
        userGivenID = "";
        userGivenReg = "";
        userGivenFaculty = "";
        userGivenSession = "";
        userGivenPhone = "";
        userGivenEmail = "";
        userGivenPassword = "";
        userGivenConfirmPassword = "";
        userGivenImagePath = "";
        spinnerSelectedFaculty = "";
        spinnerSelectedSession = "";
        imageName = "";

        customAlertDialogBox = new CustomAlertDialogBox(this);
        imageSelection = new ImageSelection(this);
        dbHelper = new DBHelper(this);
        dateAndTime = new DateAndTime();
        customImageConverter = new CustomImageConverter();
        sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_IMAGE_SELECTION_INFO, Context.MODE_PRIVATE);
    }

    private void prepareData(){
        allFaculty = new ArrayList<>();
        allSession = new ArrayList<>();

        allFaculty.add(FinalVariables.FACULTY);
        allFaculty.add(FinalVariables.FACULTY_AG);
        allFaculty.add(FinalVariables.FACULTY_CSE);
        allFaculty.add(FinalVariables.FACULTY_BAM);
        allFaculty.add(FinalVariables.FACULTY_FISH);
        allFaculty.add(FinalVariables.FACULTY_DM);
        allFaculty.add(FinalVariables.FACULTY_NFS);
        allFaculty.add(FinalVariables.FACULTY_LM);
        allFaculty.add(FinalVariables.FACULTY_DVM);
        allFaculty.add(FinalVariables.FACULTY_AH);
        allFaculty.add(FinalVariables.FACULTY_NOT_LISTED);

        allSession.add(new FinalVariables().SESSION);
        allSession.add("2012-13");
        allSession.add("2013-14");
        allSession.add("2014-15");
        allSession.add("2015-16");
        allSession.add("2016-17");
        allSession.add("2017-18");
        allSession.add("2018-19");
        allSession.add("2019-20");
        allSession.add("Not listed");
    }

    private void createID(){
        userGivenName = editTextName.getText().toString().trim();
        userGivenID = editTextID.getText().toString().trim();
        userGivenReg = editTextReg.getText().toString().trim();
        userGivenFaculty = spinnerSelectedFaculty.trim();
        userGivenSession = spinnerSelectedSession.trim();
        userGivenPhone = editTextPhone.getText().toString().trim();
        userGivenEmail = editTextEmail.getText().toString().toLowerCase().trim().toLowerCase();
        userGivenPassword = editTextPassword.getText().toString().trim();
        userGivenConfirmPassword = editTextConfirmPassword.getText().toString().trim();
        boolean isImageSelected = sharedPreferences2.getBoolean(FinalVariables.IS_IMAGE_SELECTED, false);

        //Getting Image Name
        imageName = "img_"+userGivenID+"_"+userGivenSession+".jpeg";
        userGivenImagePath = FinalVariables.IMAGE_PATH +imageName;

        if(userGivenName.isEmpty() || userGivenID.isEmpty() || userGivenReg.isEmpty()
                || userGivenFaculty.isEmpty() || userGivenSession.isEmpty() || userGivenPhone.isEmpty()
                || userGivenEmail.isEmpty() || userGivenPassword.isEmpty() || userGivenConfirmPassword.isEmpty()){
            textViewToastMessage.setText("Fill up all fields");
            toast.show();

        }else {
            if (userGivenPassword.length()>8){
                textViewToastMessage.setText("Password too long");
                toast.show();

            }else {
                if (!userGivenPassword.equals(userGivenConfirmPassword)){
                    textViewToastMessage.setText("Password mismatch");
                    toast.show();

                }else {
                    if (bitmap==null) {
                        textViewToastMessage.setText("No Image Selected");
                        editTextImagePath.setText("No Image Selected");
                        toast.show();
                    }else {
                        checkEmailValidity();
                        //saveData();
                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        if(item.equals(FinalVariables.FACULTY) || item.equals(FinalVariables.SESSION)){
            spinnerSelectedFaculty = "";
            spinnerSelectedSession = "";

        }else if(item.equals(FinalVariables.FACULTY_AG) || item.equals(FinalVariables.FACULTY_CSE)
                || item.equals(FinalVariables.FACULTY_BAM) || item.equals(FinalVariables.FACULTY_FISH)
                || item.equals(FinalVariables.FACULTY_DM) || item.equals(FinalVariables.FACULTY_NFS)
                || item.equals(FinalVariables.FACULTY_LM) || item.equals(FinalVariables.FACULTY_DVM)
                || item.equals(FinalVariables.FACULTY_AH)){
            spinnerSelectedFaculty = item;

        }else if(item.equals(FinalVariables.FACULTY_NOT_LISTED)){
            spinnerSelectedFaculty = "";
            spinnerSelectedSession = "";

        } else {
            spinnerSelectedSession = item;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void checkEmailValidity(){
        pd = new ProgressDialog(this);
        pd.setMessage("Checking Email Validity...");
        pd.setCancelable(false);
        pd.show();

        doVolleyRequest(FinalVariables.EMAIL_VALIDATION_URL, FinalVariables.REQUEST_TYPE_EMAIL_VALIDITY_CHECK);

    }


    private void saveData(){
        encodedImage = customImageConverter.convertBitmapIntoImageString(bitmap);
        userStatus = "Available";
        pd = new ProgressDialog(this);
        pd.setMessage("Signing up...");
        pd.setCancelable(false);
        pd.show();

        doVolleyRequest(FinalVariables.SIGN_UP_URL, FinalVariables.REQUEST_TYPE_SAVE_DATA);
    }

    private void doVolleyRequest(String url, final int code){
        stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty())
                    Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, code);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.d("Error: ",volleyError.toString());
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                userDeviceRegID = FirebaseInstanceId.getInstance().getToken();
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                if(code == FinalVariables.REQUEST_TYPE_EMAIL_VALIDITY_CHECK){
                    params.put(FinalVariables.KEY_EMAIL, userGivenEmail);

                }else if(code == FinalVariables.REQUEST_TYPE_SAVE_DATA){
                    params.put(FinalVariables.KEY_NAME, userGivenName);
                    params.put(FinalVariables.KEY_ID, userGivenID);
                    params.put(FinalVariables.KEY_REG, userGivenReg);
                    params.put(FinalVariables.KEY_FACULTY, userGivenFaculty);
                    params.put(FinalVariables.KEY_SESSION, userGivenSession);
                    params.put(FinalVariables.KEY_PHONE, userGivenPhone);
                    params.put(FinalVariables.KEY_EMAIL, userGivenEmail);
                    params.put(FinalVariables.KEY_PASSWORD, userGivenPassword);
                    params.put(FinalVariables.KEY_IMAGE_PATH, userGivenImagePath);
                    params.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, userDeviceRegID);
                    params.put(FinalVariables.KEY_IMAGE, encodedImage);
                    params.put(FinalVariables.KEY_IMAGE_NAME, imageName);
                    params.put(FinalVariables.KEY_STATUS, "Available");
                }

                //returning parameters
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String message = "";
    private String success = "";
    private ArrayList<String> allInfo;

    private void handleJSON(JSONObject jsonObject, final int code){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){

                if(code== FinalVariables.REQUEST_TYPE_EMAIL_VALIDITY_CHECK){
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    saveData();

                }else if(code== FinalVariables.REQUEST_TYPE_SAVE_DATA){
                    success = FinalVariables.SUCCESS;
                    message = jsonObject.getString("message");

                    alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Confirmation Message")
                            .setMessage(message+"\n\n" +
                                    "Please, request your CR to confirm your id for awesome functions.")
                            .setCancelable(false)
                            .setPositiveButton("Oky", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    void galleryPermissionDialog(int requestCode) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
            return;

        }else customAlertDialogBox.imageSelectionMethodAlertDialog();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        Map<String, Integer> perms = new HashMap<String, Integer>();
        // Initial
        perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        // Fill with results
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);
        // Check for READ_EXTERNAL_STORAGE

        boolean showRationale = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted
                customAlertDialogBox.imageSelectionMethodAlertDialog();

            } else {
                showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (showRationale) {
                    Toast.makeText(this, "Please enable the Read Storage permission to change image", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Please Enable the Read Storage permission in permission", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, requestCode);

                    //proceed with logic by disabling the related features or quit the app.
                }


            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == FinalVariables.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                editTextImagePath.setEnabled(true);
                editTextImagePath.setText("Camera Used");
                bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                customAlertDialogBox.imagePreview(bitmap);

                //Toast.makeText(getApplicationContext(), "Image selected", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image selection
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

            //else if the result is gallery Image
        } else if (requestCode == FinalVariables.GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully selected the image
                editTextImagePath.setEnabled(true);
                editTextImagePath.setText("Gallery Used");
                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                    customAlertDialogBox.imagePreview(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image selection
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == FinalVariables.AVATER_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully selected the image
                editTextImagePath.setEnabled(true);
                editTextImagePath.setText("Avater Used");
                String imageString = data.getStringExtra("imageString");
                bitmap = customImageConverter.convertStringIntoBitmap(imageString);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                customAlertDialogBox.imagePreview(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image selection
                Toast.makeText(getApplicationContext(),
                        "User cancelled avater", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to select avater", Toast.LENGTH_SHORT)
                        .show();
            }

        }else {
            super.onActivityResult(requestCode, resultCode, data);
            //Facebook login
            AccessingFacebook.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    private void showFbLoginResult(JSONObject object){
        String msg = "Nothing found.", userId="",propic="",firstName="",lastName="",email="",birthday="",gender="";

        if(object!=null) {
            try {
                userId = object.getString("id");
                propic = new URL("https://graph.facebook.com/"+userId+"/picture?width=500&&height=500").toString();
                if(object.has("first_name")) firstName = object.getString("first_name");
                if(object.has("last_name")) lastName = object.getString("last_name");
                if(object.has("email")) email = object.getString("email");
                if(object.has("birthday")) birthday = object.getString("birthday");
                if(object.has("gender")) gender = object.getString("gender");

                msg="userId: "+userId+"\nImg: "+propic+"\nFirst name: "+firstName+"\nLast name: "+lastName
                        +"\nEmail: "+email+"\nBirthday: "+birthday+"\nGender: "+gender;
            }catch (JSONException je){
                je.printStackTrace();
            }catch (MalformedURLException mue){
                mue.printStackTrace();
            }
        }

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Facebook Graph Req")
                .setMessage(msg)
                .setNeutralButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
