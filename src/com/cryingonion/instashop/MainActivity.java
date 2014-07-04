package com.cryingonion.instashop;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramSession;
import com.cryingonion.instashop.instagram.InstagramWrapper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	public String PARSE_APPLICATION_ID = "y6RzPzQ7v2Pz9HimuPoZA3jNyc3N4zQHapASaIf7";
	public String PARSE_CLIENT_KEY = "bTpn9m8Ggu7z21dHaU6CRbmhF3oLxB0y0OvcmEIS";
	
	private InstagramWrapper mIgWrapper;
	private InstagramManager mIgManager;
	
	private String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		
		/*
		ParseUser user = new ParseUser();
		user.setUsername("my name");
		user.setPassword("my pass");
		user.setEmail("email@example.com");
		  
		// other fields can be set just like with ParseObject
		user.put("phone", "650-555-0000");
		  
		user.signUpInBackground(new SignUpCallback() {
		  public void done(ParseException e) {
		    if (e == null) {
		      // Hooray! Let them use the app now.
		    } else {
		      // Sign up didn't succeed. Look at the ParseException
		      // to figure out what went wrong
		    }
		  }
		});
		*/
		
		mIgWrapper = new InstagramWrapper(this);
		
		if (!mIgWrapper.isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
		else
		{
			mIgManager = new InstagramManager(this);
			userId = mIgManager.getUserId();
			
			//Intent intent = new Intent(this, TimelineActivity.class);
			//Intent intent = new Intent(this, ListFollowsActivity.class);
			//Intent intent = new Intent(this, SearchPeopleActivity.class);
			Intent intent = new Intent(this, ListProductActivity.class);
			intent.putExtra(InstagramActivity.KEY_USER_ID, userId);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}
