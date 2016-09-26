package com.example.pawan.fblogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawan on 9/23/2016.
 */

public class HomeActivity extends AppCompatActivity {
    Button button;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    //private static final String SUBMIT_QUERY = Constants.NIIT + "/Yuva/AppSubmitQuery";
    private static final String SUBMIT_QUERY = Constants.Devicepath+"/FbUserLoginData/servlet/Fblogin";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String jsonObjectuser;
    public DataBeans databeans = new DataBeans();
    Gson gson = new Gson();

   /* public String getJsonObjectuser() {
        return jsonObjectuser;
    }

    public void setJsonObjectuser(String jsonObjectuser) {
        this.jsonObjectuser = jsonObjectuser;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        final Bundle extras = getIntent().getExtras();
        String email = extras.getString("Fb_Mail");
        String name = extras.getString("Fb_Name");
        String gender = extras.getString("Gender");
        String birthday = extras.getString("Birthday");


        databeans.setFbuseremail(email);
        databeans.setFbusername(name);
        databeans.setFbusergender(gender);
        databeans.setFbuserbirthday(birthday);


        jsonObjectuser = gson.toJson(databeans);
        Log.e("valofjson", jsonObjectuser);


       // setJsonObjectuser(jsonObjectuser);

        // DataBeans dataofuser = gson.fromJson(jsonObjectuser, DataBeans.class);


         button = (Button) findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (isInternetOn()) {

                    Toast.makeText(getApplicationContext(),"testttt",Toast.LENGTH_SHORT).show();


                    new AttemptRegister().execute();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "No internet connection !!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });


                }
            }
        });
    }

    public class AttemptRegister extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Register...please wait!!!!");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //String paramskey = getJsonObjectuser();
                //Log.e("objectuserof", paramskey);

                params.add(new BasicNameValuePair("regireralldata", jsonObjectuser));
                Log.d("request!", "starting");
                Log.e("objectofjson", jsonObjectuser);
                JSONObject json = jsonParser.makeHttpRequest(SUBMIT_QUERY, "POST", params);
                Log.e("JSONStatus", json.toString());
                success = json.getInt(TAG_SUCCESS);
                Log.e("logg",success+"");
                if (success == 1) {
                    Log.d("Registered successfully", json.toString());

                    return json.getString(TAG_MESSAGE);

                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String message) {

            dismissProgressDialog();

            if (message != null) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Feedback sent successfully.", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.com_facebook_blue));
                textView.setTextColor(Color.rgb(24, 124, 255));
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Feedback unable to sent successfully.", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                textView.setTextColor(Color.rgb(24, 124, 255));
                snackbar.show();
            }


        }

        private void dismissProgressDialog() {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

    }

    public boolean isInternetOn() {
        boolean status;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            status = true;
        } else {
            status = false;
        }
        return status;
    }
}
