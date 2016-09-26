package com.example.pawan.fblogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.R.attr.name;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private CallbackManager callbackManager;
    private TextView textView, textView1;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken;
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);


            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                String email = object.getString("email");
                                Log.e("mmain", email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String name = object.getString("name");
                                Log.e("fbusername", name);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String birthday = object.getString("birthday"); // 01/31/1980 format
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if(object!=null)
                            {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                try {
                                    intent.putExtra("Fb_Mail",object.getString("email"));
                                    intent.putExtra("Fb_Name",object.getString("name"));
                                    intent.putExtra("Gender",object.getString("gender"));
                                    intent.putExtra("Birthday",object.getString("birthday"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                startActivity(intent);


                            }




                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();


        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        textView = (TextView) view.findViewById(R.id.textView);
        textView1 = (TextView) view.findViewById(R.id.textView1);

        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("email");

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            textView.setText(profile.getName());
            textView1.setText(profile.getId());
            Log.e("Id", profile.getId());
            Log.e("userprofile", profile.getProfilePictureUri(300, 300) + "");

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }
}
