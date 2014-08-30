package com.marketlytics.idlenotifier;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.suredigit.inappfeedback.*;

public class HelpActivity extends Activity {
	
	private FeedbackDialog feedBackDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_help);
	    feedBackDialog = new FeedbackDialog(this, "AF-DFCA7D764650-5B");
	}
	
	public void getFeedback(View view) {
		feedBackDialog.show();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    feedBackDialog.dismiss();
	}

}
