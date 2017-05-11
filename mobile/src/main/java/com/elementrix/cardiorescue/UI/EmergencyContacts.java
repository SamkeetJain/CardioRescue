package com.elementrix.cardiorescue.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmergencyContacts extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ArrayList<String> namelist = new ArrayList<String>();
    ArrayList<String> numberlist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        sharedPreferences = getSharedPreferences(Constants.sharedPreferencs, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        fab = (FloatingActionButton) findViewById(R.id.addButtton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getDataFromServerEmergency();


    }

    public void BackButton(View v) {
        finish();
    }

    public void getDataFromServerEmergency() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding In...");
        progressDialog.show();

        //getting the email entered
        //final String email = usename;
        //final String password = password;

        //Creating a string request
        StringRequest req = new StringRequest(Request.Method.POST, Constants.getContactURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();


                        //if the server returned the string success
                        if (!response.trim().equals("not results found in profile table")) {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("c_name");
                                    String number = jsonObject.getString("c_num");
                                    namelist.add(name);
                                    numberlist.add(number);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            forward();
                            //Displaying a success toast
                            Toast.makeText(getApplicationContext(), "Loaded successfully !", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Load", Toast.LENGTH_SHORT).show();
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
                params.put("email", sharedPreferences.getString(Constants.EMAIL_PREF, "null"));
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
//

    }

    public void forward() {

        if (numberlist.size() > 0 || namelist.size() > 0) {

            mAdapter = new EmergencyAdapter(namelist.toArray(new String[namelist.size()]), numberlist.toArray(new String[numberlist.size()]));
            mRecyclerView.setAdapter(mAdapter);
        }
    }


}
