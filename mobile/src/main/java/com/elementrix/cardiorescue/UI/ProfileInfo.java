package com.elementrix.cardiorescue.UI;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileInfo extends AppCompatActivity {

    public TextView mName,mMobile,mEmail,mAddress,mEmergencyMessage;
    public String name,mobile,email,address,emergencyMessage,sex;
    public RadioButton mMale,mFemale;
    public Button EditSave;

    String type="EDIT";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        sharedPreferences=getSharedPreferences(Constants.sharedPreferencs,MODE_PRIVATE);
        editor=sharedPreferences.edit();

        mName= (TextView) findViewById(R.id.name);
        mMobile= (TextView) findViewById(R.id.mobile);
        mEmail= (TextView) findViewById(R.id.email);
        mAddress= (TextView) findViewById(R.id.address);
        mEmergencyMessage= (TextView) findViewById(R.id.emgMsg);
        mMale= (RadioButton) findViewById(R.id.maleButton);
        mFemale= (RadioButton) findViewById(R.id.femaleButton);
        EditSave= (Button) findViewById(R.id.editsave);
        GetDataFromServerProfile();
        setSave();
    }

    public void BackButton(View v){
        finish();
    }

    public void Logout(View v){
        stopService(new Intent(this,Srvices.class));
    }

    public void EditSave(View v){
        if(type.equals("EDIT")){
            setEdit();
            EditSave.setText("SAVE");
            type="SAVE";

        }else if(type.equals("SAVE")){
            setSave();
            EditSave.setText("EDIT");
            type="EDIT";
            SendDataToServerProfile();
        }
    }

    public void SendDataToServerProfile(){
        name=mName.getText().toString();
        email=mEmail.getText().toString();
        mobile=mMobile.getText().toString();
        emergencyMessage=mEmergencyMessage.getText().toString();
        address=mAddress.getText().toString();
        if(mMale.isChecked()){
            sex="M";
        }else {
            sex="F";
        }

        //SEND DATA HERE

        //Creating a progress dialog to show while it is storing the data on server
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Making changes...");
        progressDialog.show();


        //Creating a string request
        StringRequest req = new StringRequest(Request.Method.POST, Constants.putProfilrURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();

                        //if the server returned the string success
                        if (!response.equals("success")) {
                            //Displaying a success toast
                            Toast.makeText(getApplicationContext(), "Changes done", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
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
                params.put("name",name);
                params.put("email", email);
                params.put("phone", mobile);
                params.put("sex",sex);
                params.put("age"," ");
                params.put("address",address);
                params.put("e_msg",emergencyMessage);
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }



    public void GetDataFromServerProfile(){
        //GET DATA FROM HERE

        //Creating a progress dialog to show while it is storing the data on server
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();



        //Creating a string request
        StringRequest req = new StringRequest(Request.Method.POST, Constants.get_profileURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();

                        //if the server returned the string success
                        if (!response.trim().equals("not results found in profile table")) {
                            //Displaying a success toast
                            Toast.makeText(getApplicationContext(), "Data fetched successfully", Toast.LENGTH_SHORT).show();

                            //fetch object
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                mName.setText(jsonObject.getString("name"));
                                mMobile.setText(jsonObject.getString("mobile"));
                                String temp = jsonObject.getString("sex");
                                if(temp.equals("M"))
                                    mMale.setChecked(true);
                                else
                                    mFemale.setChecked(true);
                                //jsonObject.getString("age");
                                mAddress.setText(jsonObject.getString("address"));
                                mEmergencyMessage.setText(jsonObject.getString("e_msg"));
                                mEmail.setText(jsonObject.getString("email"));

                            }catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to get data for this email", Toast.LENGTH_SHORT).show();
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
                params.put("email", sharedPreferences.getString(Constants.EMAIL_PREF,"null"));
                //params.put("email", null);
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }



    public void setSave(){
        mName.setEnabled(false);
        mEmail.setEnabled(false);
        mMobile.setEnabled(false);
        mEmergencyMessage.setEnabled(false);
        mAddress.setEnabled(false);
        mMale.setEnabled(false);
        mFemale.setEnabled(false);
    }

    public void setEdit(){
        mName.setEnabled(true);
        mEmail.setEnabled(true);
        mMobile.setEnabled(true);
        mEmergencyMessage.setEnabled(true);
        mAddress.setEnabled(true);
        mMale.setEnabled(true);
        mFemale.setEnabled(true);
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
