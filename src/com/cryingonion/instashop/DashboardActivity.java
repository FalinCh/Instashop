package com.cryingonion.instashop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class DashboardActivity extends Activity {
	public static final String TAG = DashboardActivity.class.getSimpleName();
	
	// Parse.com
	public String PARSE_APPLICATION_ID = "y6RzPzQ7v2Pz9HimuPoZA3jNyc3N4zQHapASaIf7";
	public String PARSE_CLIENT_KEY = "bTpn9m8Ggu7z21dHaU6CRbmhF3oLxB0y0OvcmEIS";
	
	// Instagram
	private String userId;
	private InstagramWrapper mIgWrapper;
	private InstagramManager mIgManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		ParseAnalytics.trackAppOpened(getIntent());
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		mIgWrapper = new InstagramWrapper(this);
		
		if (!mIgWrapper.isLoggedIn() && currentUser == null) {
			navigateToLogin();
		}
		else
		{
			navigateToMainPage();
			Log.i(TAG, "Success Login");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public void navigateToLogin ()
	{
		Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
	
	public void navigateToMainPage()
	{
		mIgManager = new InstagramManager(this);
		userId = mIgManager.getUserId();
		
		//Intent intent = new Intent(this, TimelineActivity.class);
		//Intent intent = new Intent(this, ListFollowsActivity.class);
		//Intent intent = new Intent(this, SearchPeopleActivity.class);
		//Intent intent = new Intent(this, ListProductActivity.class);
		
		Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
		intent.putExtra(InstagramActivity.KEY_USER_ID, userId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
	
	
}
