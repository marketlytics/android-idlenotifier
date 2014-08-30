package com.marketlytics.idlenotifier;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
	Parse.initialize(this, "Af5csnypaP7pFMfcstzbynrZIAP9nKKzFH9kMTri", "SpISmEk12NRnRDWRAmcZdxnG3wtbzdjNAGztNDB6"); 

	// Specify an Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, MainActivity.class);
  }
}