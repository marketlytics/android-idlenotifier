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
      if(preferences.getBoolean("account_id", false)) {
    	  JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
    	  
          String status = json.getString("status");
          Log.d(TAG, "got status from device: (" + status + ")");
                
          AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);   

          if(status.equals("active")) {
        	  Toast.makeText(context, "Device is now slient.", Toast.LENGTH_LONG).show();
        	  am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
          } 
          else {
        	  Toast.makeText(context, "Restoring normal mode.", Toast.LENGTH_LONG).show();
        	  am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
          }
      }
      
      
    } catch (JSONException e) {
      Log.d(TAG, "JSONException: " + e.getMessage());
    }
  }
}