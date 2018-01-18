package com.project.blackspider.classschedule.Facebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.project.blackspider.classschedule.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mr blackSpider on 4/30/2017.
 */

public class AccessingFacebook {
    private Context context;
    private Activity activity;
    public static CallbackManager callbackManager;
    public static AccessTokenTracker accessTokenTracker;
    public static ProfileTracker profileTracker;
    public static ShareDialog shareDialog;
    public static GameRequestDialog requestDialog;

    private LoginButton fbLoginButton;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog pd;

    public FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.e("Graph req(object): ", object.toString());
                    Log.e("Graph req(response):", response.toString());
                    showFbLoginResult(object);
                    getConversions();
                    //getPosts();

                    try {
                        JSONObject allFriends = new JSONObject();
                        JSONObject allPosts = new JSONObject();
                        if(object.has("taggable_friends")) allFriends = object.getJSONObject("taggable_friends");
                        if(object.has("posts")) allPosts = object.getJSONObject("posts");
                        JSONArray friendslist = allFriends.getJSONArray("data");
                        JSONArray postlist = allPosts.getJSONArray("data");
                        ArrayList<String> friendsName = new ArrayList<>();
                        ArrayList<String> postId = new ArrayList<>();
                        for (int l=0; l < friendslist.length(); l++) {
                            friendsName.add(friendslist.getJSONObject(l).getString("name"));
                        }
                        for (int l=0; l < postlist.length(); l++) {
                            postId.add(postlist.getJSONObject(l).getString("id"));
                        }
                        Log.e("Friends: "+friendsName.size()+": ", friendsName.toString());
                        Log.e("Posts: "+postId.size()+":", postId.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle params = new Bundle();
            params.putString("fields", "id, first_name, last_name, email, birthday, gender, friends, taggable_friends, posts");
            graphRequest.setParameters(params);
            graphRequest.executeAsync();
            //getFriendLists(loginResult.getAccessToken());

            //Profile profile = Profile.getCurrentProfile();
            //showFbLoginResult(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            error.printStackTrace();
        }
    };

    public AccessingFacebook(){

    }

    public AccessingFacebook(Context context){
        this.context = context;
    }

    public AccessingFacebook(Activity activity){
        this.activity = activity;
        context = activity.getApplicationContext();
    }


    public static void accessingFriendlist(){

    }

    private void onClickRequestButton() {
        GameRequestContent content = new GameRequestContent.Builder()
                .setMessage("Come play this level with me")
                .build();
        requestDialog.show(content);
    }

    private void onClickRequestButton2() {
        GameRequestContent content = new GameRequestContent.Builder()
                .setMessage("Come play this level with me")
                .setTo("USER_ID")
                .setActionType(GameRequestContent.ActionType.SEND)
                .setObjectId("YOUR_OBJECT_ID")
                .build();
        requestDialog.show(content);
    }

    public void setUpFbLogin(LoginButton loginButton){
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        requestDialog = new GameRequestDialog(activity);
        callbackManager = CallbackManager.Factory.create();

        requestDialog.registerCallback(callbackManager,
                new FacebookCallback<GameRequestDialog.Result>() {
                    public void onSuccess(GameRequestDialog.Result result) {
                        String id = result.getRequestId();
                    }
                    public void onCancel() {}
                    public void onError(FacebookException error) {}
                }
        );
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

        fbLoginButton = loginButton;
        shareDialog = new ShareDialog(activity);

        fbLoginButton.setReadPermissions("user_friends","email","user_birthday","read_custom_friendlists","user_posts");
        fbLoginButton.registerCallback(callbackManager, callback);
    }

    public void getPosts(){
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/posts", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Log.e("Graph req(Posts):", response.toString());
            }
        });
    }

    private void getFriendLists(AccessToken accessToken){
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                Log.e("Friend req(object): ", jsonObject.toString());
                Log.e("Friend req(response):", graphResponse.toString());
                try {
                    //JSONArray jsonArrayFriends = jsonObject.getJSONObject("friendlist").getJSONArray("data");
                    //JSONObject friendlistObject = jsonArrayFriends.getJSONObject(0);
                    String friendListID = jsonObject.getString("id");
                    Log.e("Friend req(response):", friendListID);
                    myNewGraphReq(friendListID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "friendlists");
        graphRequest.setParameters(param);
        graphRequest.executeAsync();
    }

    private void myNewGraphReq(String friendlistId) {
        final String graphPath = "/"+friendlistId+"/taggable_friends";
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = new GraphRequest(token, graphPath, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject object = graphResponse.getJSONObject();
                Log.e("Friendlist req: ", graphResponse.toString());
                try {
                    JSONArray arrayOfUsersInFriendList= object.getJSONArray("data");
                /* Do something with the user list */
                /* ex: get first user in list, "name" */
                    JSONObject user = arrayOfUsersInFriendList.getJSONObject(0);
                    String usersName = user.getString("name");
                    Log.e("1st friend: "+arrayOfUsersInFriendList.length()+": ", usersName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "name");
        //request.setParameters(param);
        request.executeAsync();
    }

    private void getConversions(){
        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken()
                , "/conversations", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {

                Log.e("Conversion req: ", response.toString());
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "read_mailbox, page_show_list");
        //request.setParameters(param);
        request.executeAsync();
    }

    private String generateHasKey(){
        String hasKey = "";
        try {
            String msg = "";
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
                hasKey+=Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
            Toast.makeText(context, "Msg: "+msg, Toast.LENGTH_LONG).show();
            Log.e("Msg:", msg);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("jk", "Exception(NameNotFoundException) : " + e);
            Toast.makeText(context, "Error1: "+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            Log.e("mkm", "Exception(NoSuchAlgorithmException) : " + e);
            Toast.makeText(context, "Error2: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return hasKey;
    }

    public void showFbLoginResult(Profile profile){
        String msg = "Nothing found.";
        //String hasKey = generateHasKey();
        if(profile!=null) msg = "Name: "+profile.getFirstName()+" "+profile.getLastName()+"\n"+
                "Img url: "+profile.getProfilePictureUri(200,200).toString();

        //msg+="\nHasKey: "+hasKey;

        alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Facebook Info")
                .setMessage(msg)
                .setNeutralButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder().build();
                        AccessingFacebook.shareDialog.show(shareLinkContent);
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


    private void showFbLoginResult(JSONObject object){
        String msg = "Nothing found.", userId="",propic="",firstName="",lastName="",email="",birthday="",gender="",totalFriends="", taggable="";
        //String hasKey = generateHasKey();
        if(object!=null) {
            try {
                userId = object.getString("id");
                propic = new URL("https://graph.facebook.com/"+userId+"/picture?width=500&&height=500").toString();
                if(object.has("first_name")) firstName = object.getString("first_name");
                if(object.has("last_name")) lastName = object.getString("last_name");
                if(object.has("email")) email = object.getString("email");
                if(object.has("birthday")) birthday = object.getString("birthday");
                if(object.has("gender")) gender = object.getString("gender");
                JSONObject allFriends;
                if(object.has("friends")){
                    allFriends = object.getJSONObject("friends").getJSONObject("summary");
                    totalFriends = allFriends.getString("total_count");
                }
                if(object.has("taggable_friends")){
                    allFriends = object.getJSONObject("taggable_friends");
                    JSONArray friendslist = allFriends.getJSONArray("data");
                    taggable = friendslist.length()+"";
                }

                msg="userId: "+userId+"\nImg: "+propic+"\nFirst name: "+firstName+"\nLast name: "+lastName
                        +"\nEmail: "+email+"\nBirthday: "+birthday+"\nGender: "+gender+"\nTotal friends:"+totalFriends
                        +"\nTaggable: "+taggable;
            }catch (JSONException je){
                je.printStackTrace();
            }catch (MalformedURLException mue){
                mue.printStackTrace();
            }
        }

        alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Facebook Graph Req")
                .setMessage(msg)
                .setNeutralButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("http://www.google.com"))
                                .setQuote("Integrating fb api")
                                .setShareHashtag(new ShareHashtag.Builder().setHashtag("Hi, Google").build())
                                .build();
                        shareDialog.show(shareLinkContent);
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
