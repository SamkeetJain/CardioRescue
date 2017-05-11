package com.elementrix.cardiorescue.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elementrix.cardiorescue.Constants;
import com.elementrix.cardiorescue.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public TextView mName,mNumber;
    String name,number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_address);


        sharedPreferences=getSharedPreferences(Constants.sharedPreferencs,MODE_PRIVATE);
        editor=sharedPreferences.edit();

        mName= (TextView) findViewById(R.id.name);
        mNumber= (TextView) findViewById(R.id.number);

    }

    public void addContact(View v){
        String email=sharedPreferences.getString(Constants.EMAIL_PREF,null);

        name=mName.getText().toString();
        number=mNumber.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding In...");
        progressDialog.show();

        //getting the email entered
        //final String email = usename;
        //final String password = password;

        //Creating a string request
        StringRequest req = new StringRequest(Request.Method.POST, Constants.addContactURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();
                        String token="";

                        //if the server returned the string success
                        if (!response.trim().equals("failed")) {

                            //Displaying a success toast
                            Toast.makeText(getApplicationContext(), "Added successfully !", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Add", Toast.LENGTH_SHORT).show();
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
                params.put("uemail", sharedPreferences.getString(Constants.EMAIL_PREF,"null"));
                params.put("phone", number);
                params.put("ename",name);
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);


    }
}
