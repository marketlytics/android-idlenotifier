package com.marketlytics.idlenotifier;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MyOtherCustomReceiver extends BroadcastReceiver {
private static final String TAG = "MyOtherCustomReceiver";
 
  @Override
  public void onReceive(Context context, Intent intent) {
    try {    	
    	JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
  	  
        String status = json.getString("completed");
        Log.d(TAG, "got status from device: (" + status + ")");
        
        SharedPreferences  preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
	    editor.putBoolean("completed", true);
	    editor.commit();
	    
	    Toast.makeText(context, "Account linked to chrome", Toast.LENGTH_LONG).show();
      
    } catch (JSONException e) {
      Log.d(TAG, "JSONException: " + e.getMessage());
    }
  }
}