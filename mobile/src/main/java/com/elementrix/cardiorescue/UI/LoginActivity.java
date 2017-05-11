package com.elementrix.cardiorescue.UI;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elementrix.cardiorescue.Constants;
import com.elementrix.cardiorescue.GPS.GPSService;
import com.elementrix.cardiorescue.R;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elementrix.cardiorescue.Constants.ISREGISTER_PREF;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public String usename;
    public String password;
    public String uniqueId;
    public EditText user;
    public EditText pwd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startService(new Intent(getApplicationContext(), GPSService.class));

        sharedPreferences=getSharedPreferences(Constants.sharedPreferencs,MODE_PRIVATE);
        editor=sharedPreferences.edit();



        String isLogedIn=sharedPreferences.getString(Constants.ISLOGIN_PREF,"null");
        Constants.uniqueID = sharedPreferences.getString(Constants.FIREBASE_UNIQUE_ID_PREF,"null");

        if(!isLogedIn.equals("null")){
            //Loged In
            if(!isMyServiceRunning(Srvices.class)) {
                startService(new Intent(getApplicationContext(), Srvices.class));
            }
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        Firebase.setAndroidContext(getApplicationContext());
    }

    public void Login(View v){


        user= (EditText) findViewById(R.id.username);
        pwd= (EditText) findViewById(R.id.password);

        usename=user.getText().toString();
        password=pwd.getText().toString();

        sendDataToServerLogin(usename,password);
    }

    public void SignupButton(View v){

        if(sharedPreferences.getString(Constants.ISREGISTER_PREF, "false").equals("true")){
            Toast.makeText(getApplicationContext(),"Device Already Registered",Toast.LENGTH_LONG).show();
            return;
        }
        user= (EditText) findViewById(R.id.username);
        pwd= (EditText) findViewById(R.id.password);

        usename=user.getText().toString();
        password=pwd.getText().toString();

        sendDataToServerSignup(usename,password);
    }

    public void sendDataToServerLogin(final String usename, final String password){
        //Creating a progress dialog to show while it is storing the data on server
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Siging In...");
        progressDialog.show();

        //getting the email entered
        //final String email = usename;
        //final String password = password;

        //Creating a string request
        StringRequest req = new StringRequest(Request.Method.POST, Constants.Login_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();
                        String token="";

                        //if the server returned the string success
                        if (!response.trim().equals("false")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                token = jsonObject.getString("token");

                            }catch(JSONException e) {
                            System.out.print(e.toString());
                            }
                            //Displaying a success toast
                            Toast.makeText(getApplicationContext(), "Signed in successfully !", Toast.LENGTH_SHORT).show();

                            //Opening shared preference
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencs, MODE_PRIVATE);

                            //Opening the shared preferences editor to save values
                            SharedPreferences.Editor editor = sharedPreferences.edit();


                            editor.putString(Constants.LOGIN_TOKEN_PREF, token);
                            editor.putString(Constants.ISLOGIN_PREF,"YES");
                            editor.putString(Constants.EMAIL_PREF,usename);

                            //Applying the changes on sharedpreferences
                            Toast.makeText(getApplicationContext(),sharedPreferences.getString(Constants.LOGIN_TOKEN_PREF,null),Toast.LENGTH_LONG).show();
                            editor.apply();
                            if(!isMyServiceRunning(Srvices.class)) {
                                startService(new Intent(getApplicationContext(), Srvices.class));
                            }
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            finish();

                            //Starting our listener service once the device is registered
                            // startService(new Intent(getBaseContext(), NotificationListener.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //adding parameters to post request as we need to send firebase id and email
                params.put("email", usename);
                params.put("password", password);
                //Toast.makeText(getApplicationContext(),params.toString(),Toast.LENGTH_LONG).show();
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }




    public void sendDataToServerSignup(final String usename, final String password){

        uniqueId=getFirebaseID();

        //Creating a progress dialog to show while it is storing the data on server
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();



        //Creating a string request
        StringRequest req2 = new StringRequest(Request.Method.POST, Constants.Register_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();

                        //if the server returned the string success
                        if (!response.trim().equals("failed")) {
                            //Displaying a success toast
                            //uniqueId = getFirebaseID();
                            Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();

                            //Opening shared preference
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencs, MODE_PRIVATE);

                            //Opening the shared preferences editor to save values
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Storing the unique id

                            //Saving the boolean as true i.e. the device is registered
                            editor.putString(Constants.ISREGISTER_PREF, "true");

                            //Applying the changes on sharedpreferences
                            editor.apply();

                            //Starting our listener service once the device is registered
                            // startService(new Intent(getBaseContext(), NotificationListener.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Choose a different email", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //adding parameters to post request as we need to send firebase id and email
                params.put("firebaseID", uniqueId);
                params.put("email", usename);
                params.put("password",password);
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(req2);
    }

    public String getFirebaseID()
    {
        if(!sharedPreferences.getString(ISREGISTER_PREF,"false").equals("true"))
        {
            Firebase firebase = new Firebase(Constants.FirebaseURL);
            Firebase newfirebase = firebase.push();
            Data data = new Data();
            data.setAlert("NONE");
            data.setMsg("NONE");
            newfirebase.child("Data").setValue(data);
            editor.putString(Constants.FIREBASE_UNIQUE_ID_PREF, newfirebase.getKey() );
            editor.apply();
            Constants.uniqueID=newfirebase.getKey();
            return newfirebase.getKey();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Already registered..",Toast.LENGTH_LONG).show();
            return null;
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}


