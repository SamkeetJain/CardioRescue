package com.elementrix.cardiorescue.UI;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.Toast;

import com.elementrix.cardiorescue.Constants;
import com.elementrix.cardiorescue.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Sam on 04-Nov-16.
 */

public class Srvices extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Firebase.setAndroidContext(getBaseContext());


        Firebase firebase = new Firebase(Constants.FirebaseURL + Constants.uniqueID);
        System.out.print(Constants.FirebaseURL + Constants.uniqueID);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot

                    //Map<String, String> data = (Map<String, String>) postSnapshot.getValue();
                    Data data = postSnapshot.getValue(Data.class);
                    //MainActivity.t1.setText(data.getmsg());
                        Toast.makeText(getApplicationContext(),data.getMsg(),Toast.LENGTH_LONG).show();
                    if(!data.getMsg().equals("NONE")) {
                        showNotification(data.getMsg());
                        //showNotification2(data.getmsg());
                        if (data.getMsg().equals("Alert")) {


                            Intent i = new Intent();
                            i.setClass(getBaseContext(), Alert.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                            startActivity(i);
                        }
                    }
                    //AutoUpdateService.setData(data.getMsg());
                    //showNotification(data.getMsg());
                    //Adding it to a string
                    // String string = "Name: "+person.getName()+"\nAddress: "+person.getAddress()+"\n\n";

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private void showNotification(String msg){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.simplifiedcoding.net"));
        Intent intent = new Intent(this, Alert.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Emergency Notification !");
        builder.setContentText(msg);
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setSound(alarmSound);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);
        //notificationManager.notify(1, builder.build());
        Notification notification = builder.build();

        startForeground(1337, notification);
        // this.startForeground(1,notificationManager);

    }

    /*private void showNotification2(String data)
    {
        Intent notificationIntent = new Intent(this, alert.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Appulance Running.....")
                .setContentText(data)
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }*/
}