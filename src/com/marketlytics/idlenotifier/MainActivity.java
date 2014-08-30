package com.marketlytics.idlenotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;

public class MainActivity extends Activity {


	SharedPreferences preferences;
	
	Spinner sAccountList, activeModeSpinner, defaultModeSpinner;
	TextView tvLinkedToChrome; 
	ToggleButton toggleButtonService;
	
	int countInitalSaves = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Track app opens.
		ParseAnalytics.trackAppOpened(getIntent());
		
		Context context = getApplicationContext();
		
		sAccountList = (Spinner) findViewById(R.id.spinnerAccountList);
		activeModeSpinner= (Spinner) findViewById(R.id.activeModeSpinner);
		defaultModeSpinner = (Spinner) findViewById(R.id.defaultModeSpinner);
		
		
		tvLinkedToChrome = (TextView) findViewById(R.id.textViewLinkedToChrome);
		toggleButtonService = (ToggleButton) findViewById(R.id.toggleButtonService);
		
		// to be used for the ToggleButton
	    preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);  
	    
	    
	    // Create an ArrayAdapter using the string array and a default spinner layout
	    List<String> accountsList = new ArrayList<String>();
	    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
	    Account[] accounts = AccountManager.get(context).getAccounts();
	    for (Account account : accounts) {
	        if (emailPattern.matcher(account.name).matches()) {
	        	if(!accountsList.contains(account.name))
	        		accountsList.add(account.name);
	        }
	    }
	    
	    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
	    		android.R.layout.simple_spinner_item, accountsList);
	    // Specify the layout to use when the list of choices appears
	    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    sAccountList.setAdapter(dataAdapter);
	    
	    

		 // Create an ArrayAdapter using the string array and a default spinner layout
	    List<String> modes = new ArrayList<String>();
        modes.add("Silent");
        modes.add("Vibrate");
        modes.add("Normal");
        
        ArrayAdapter<String> anotherDataAdapter = new ArrayAdapter<String>(this,
	    		android.R.layout.simple_spinner_item, modes);
	    // Specify the layout to use when the list of choices appears
	    anotherDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    defaultModeSpinner.setAdapter(anotherDataAdapter);
	    activeModeSpinner.setAdapter(anotherDataAdapter);
		
		toggleButtonService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonview, boolean isChecked){
        	    
            	SharedPreferences.Editor editor = preferences.edit();
        	    editor.putBoolean(Constants.KEY_ID, isChecked);
        	    editor.commit();
        	    
        	    // reset to original user state
        	    if(!isChecked) {
        	    	AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        	    	am.setRingerMode((int) preferences.getInt(Constants.DEFAULT_MODE, 0));
        	    }
            }
         });
		
	}
	
	public void showHelp(View view) {
		startActivity(new Intent(getApplicationContext(), HelpActivity.class));
	}

	@Override
	public void onStart() {
		super.onStart();
		refreshUserProfile();
		OnItemSelectedListener listener = new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	saveUserAccount();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	saveUserAccount();
		    }

		};
	    
		sAccountList.setOnItemSelectedListener(listener);
		defaultModeSpinner.setOnItemSelectedListener(listener);
		activeModeSpinner.setOnItemSelectedListener(listener);
	}

	// Save the user's profile to their installation.
	private void saveUserAccount() {	
		if(countInitalSaves > 2) { // we dont want to save the first 3 selection at init
			ParseInstallation.getCurrentInstallation().put(Constants.KEY_ID, (String) sAccountList.getSelectedItem());
			ParseInstallation.getCurrentInstallation().put(Constants.DEFAULT_MODE, (String) defaultModeSpinner.getSelectedItem());
			ParseInstallation.getCurrentInstallation().put(Constants.ACTIVE_MODE, (String) activeModeSpinner.getSelectedItem());
			
			SharedPreferences.Editor editor = preferences.edit();
    	    editor.putInt(Constants.DEFAULT_MODE, defaultModeSpinner.getSelectedItemPosition());
    	    editor.putInt(Constants.ACTIVE_MODE, activeModeSpinner.getSelectedItemPosition());
    	    editor.commit();
			
			ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						Toast toast = Toast.makeText(getApplicationContext(), R.string.alert_dialog_success, Toast.LENGTH_SHORT);
						toast.show();
					} else {
						e.printStackTrace();
	
						Toast toast = Toast.makeText(getApplicationContext(), R.string.alert_dialog_failed, Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
		}
		countInitalSaves++;
	}
	
	// Refresh the UI with the data obtained from the current ParseInstallation object.
	private void displayUserProfile() {
		String account = ParseInstallation.getCurrentInstallation().getString(Constants.KEY_ID);
		String activeMode = ParseInstallation.getCurrentInstallation().getString(Constants.ACTIVE_MODE);
		String defaultMode = ParseInstallation.getCurrentInstallation().getString(Constants.DEFAULT_MODE);
		
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) sAccountList.getAdapter();
		ArrayAdapter<String> anotherAdapter = (ArrayAdapter<String>) activeModeSpinner.getAdapter();
		
		int spinnerPosition = adapter.getPosition(account);
		int activeModeSpinnerPosition = anotherAdapter.getPosition(activeMode);
		int defaultModeSpinnerPosition = anotherAdapter.getPosition(defaultMode);
		
		sAccountList.setSelection(spinnerPosition);
		activeModeSpinner.setSelection(activeModeSpinnerPosition);
		defaultModeSpinner.setSelection(defaultModeSpinnerPosition);
		
	}
	
	// Get the latest values from the ParseInstallation object.
	private void refreshUserProfile() {
		toggleButtonService.setChecked(preferences.getBoolean(Constants.KEY_ID, false));
		
		if(preferences.getBoolean(Constants.COMPLETED, false)) {
			tvLinkedToChrome.setText("Your account is linked to Chrome.");
		}
		
		ParseInstallation.getCurrentInstallation().refreshInBackground(new RefreshCallback() {
			
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					displayUserProfile();
				}
			}
		});
	}
}
