package com.marketlytics.idlenotifier;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class MyCustomReceiver extends BroadcastReceiver {
private static final String TAG = "MyCustomReceiver";
 
  @Override
  public void onReceive(Context context, Intent intent) {
    try {
    	
      SharedPreferences  preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
      // Only configure if the service is "ON"
      if(preferences.getBoolean(Constants.KEY_ID, false)) {
    	  JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
    	  
          String status = json.getString("status");
          Log.d(TAG, "got status from device: (" + status + ")");
                
          AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);   

          if(status.equals("active")) {
        	  Toast.makeText(context, "Your computer is active.", Toast.LENGTH_LONG).show();
        	  am.setRingerMode(preferences.getInt(Constants.ACTIVE_MODE, 0));
          } 
          else {
        	  Toast.makeText(context, "Your computer is now idle.", Toast.LENGTH_LONG).show();
        	  am.setRingerMode(preferences.getInt(Constants.DEFAULT_MODE, 0));
          }
      }
      
      
    } catch (JSONException e) {
      Log.d(TAG, "JSONException: " + e.getMessage());
    }
  }
}