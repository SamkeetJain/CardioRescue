package com.elementrix.cardiorescue.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.elementrix.cardiorescue.HeartSensor.DataLayerListenerService;
import com.elementrix.cardiorescue.R;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public RelativeLayout mainLayout;
    public TextView currentValue;

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private MiniDrawer miniResult = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    private Toolbar toolbar;

    public ArrayList<String> arrayOfBeeps=new ArrayList<String>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences=getSharedPreferences(Constants.sharedPreferencs,MODE_PRIVATE);
        editor=sharedPreferences.edit();

        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        currentValue = (TextView) findViewById(R.id.currentValue);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
//                .withHeaderBackground(R.drawable.header)
                /// TODO: 19-Oct-16
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerLayout(R.layout.crossfade_material_drawer)
                .withHasStableIds(true)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_first).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_second).withIcon(MaterialDesignIconic.Icon.gmi_http).withIdentifier(2),
                        //new PrimaryDrawerItem().withName(R.string.drawer_item_third).withIcon(MaterialDesignIconic.Icon.gmi_navigation).withIdentifier(3),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_fourth).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_fifth).withIcon(FontAwesome.Icon.faw_android).withIdentifier(5)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1) {
                            Intent obj = new Intent(getApplicationContext(), ProfileInfo.class);
                            startActivity(obj);
                        }
                        if (drawerItem.getIdentifier() == 2) {
                            Intent obj = new Intent(getApplicationContext(), EmergencyContacts.class);
                            startActivity(obj);


                        }
//                        if (drawerItem.getIdentifier() == 3) {
//                            Intent obj = new Intent(getApplicationContext(), Statistics.class);
//                            startActivity(obj);
//                        }
                        if (drawerItem.getIdentifier() == 4) {
                            Intent obj = new Intent(getApplicationContext(), HowItWorks.class);
                            startActivity(obj);
                        }
                        if (drawerItem.getIdentifier() == 5) {
                            Intent obj = new Intent(getApplicationContext(), AboutUs.class);
                            startActivity(obj);
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
//        view.setBackgroundColor(Color.BLACK);
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });

        //hook to the crossfade event
        crossfadeDrawerLayout.withCrossfadeListener(new CrossfadeDrawerLayout.CrossfadeListener() {
            @Override
            public void onCrossfade(View containerView, float currentSlidePercentage, int slideOffset) {
                //Log.e("CrossfadeDrawerLayout", "crossfade: " + currentSlidePercentage + " - " + slideOffset);
            }
        });


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // message from API client! message from wear! The contents is the heartbeat.
            if (currentValue != null) {
                int value=msg.what;
                currentValue.setText(Integer.toString(msg.what));
                if(value<60){
                    mainLayout.setBackgroundColor(Color.parseColor("#00ff00"));
                }else if(value>110){
                    mainLayout.setBackgroundColor(Color.parseColor("#ff0000"));
                }
                else {
                    value=value-60;
                    value=value/2;
                    --value;
                    mainLayout.setBackgroundColor(Color.parseColor(Constants.ArrayColurs[value]));
                }

                /*arrayOfBeeps.add(String.valueOf(value));

                if(arrayOfBeeps.size()>=100){

                    int sum=0;
                    float avg=0;
                    for(int i=0;i<arrayOfBeeps.size();i++){
                        sum+=Integer.parseInt(arrayOfBeeps.get(i));
                    }
                    avg=sum/arrayOfBeeps.size();
                    arrayOfBeeps.clear();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    String timestamp = simpleDateFormat.format(new Date());

                    sendDataToServer(timestamp,avg);

                }*/
            }

        }
    };

    public void sendDataToServer(String s){
        final String ssss=s;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Data...");
        progressDialog.show();

        //getting the email entered
        //final String email = usename;
        //final String password = password;

        //Creating a string request
        StringRequest req = new StringRequest(Request.Method.POST, Constants.putData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();

                        //if the server returned the string success
                        if (!response.trim().equals("false")) {

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
                params.put("email", ssss);
                params.put("message","Alert");

                //Toast.makeText(getApplicationContext(),params.toString(),Toast.LENGTH_LONG).show();
                return params;
            }
        };

        //Adding the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DataLayerListenerService.setHandler(handler);
    }

    public void ALERT(View v){
        sendDataToServer("qaws");
        sendDataToServer("zxsa");
    }
}
