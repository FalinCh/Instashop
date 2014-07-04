package com.cryingonion.instashop;

import com.cryingonion.instashop.instagram.*;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.listener.IAuthenticationListener;
import com.cryingonion.instashop.instagram.listener.IReqStatusListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class LoginActivity extends Activity implements IReqStatusListener{

	// layout assets
	
	private ImageButton mButtonLoginInstagram;
	
	// Instagram variable
	
	public static final String KEY_USER_ID = "user_id";
    public static final String KEY_MEDIA_ID = "media_id";
    public static final String KEY_CMNT_TXT = "cmnt_txt";
    public static final String KEY_NXT_PG_URL = "nxt_pg_url";
    public static final String KEY_FEED_COUNT = "feed_count";
    
    private InstagramWrapper mIgWrapper;
	
	private Intent mIgReqIntent;
	private int mIgReqType;
	
    private static IAuthenticationListener mIgAuthListener;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mIgWrapper = new InstagramWrapper(this,mAuthListener);	
		mIgWrapper.setReqStatusListener(this);
		
		mButtonLoginInstagram = (ImageButton) findViewById(R.id.buttonLoginInstagram);
		mButtonLoginInstagram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIgWrapper.loginInstagram();
			}
		});
		
		/*
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
			navigateToMainActivity();
		}
		*/
	}

	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	*/
	
	/**
	 * Authentication success or failure listener.
	 */
	private final IAuthenticationListener mAuthListener = new IAuthenticationListener() {

		@Override
		public void onAuthSuccess() {
			Log.d(InstagramConstants.TAG, "Finishing IG activity.");
			LoginActivity.this.finish();

			Log.d(InstagramConstants.TAG, "In IG Auth success.");
			
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			
			/*
			if (mIgReqType == InstagramConstants.IG_REQ_POST_LIKE) {

				mIgWrapper.postLikeOnMedia(InstagramConstants.IG_REQ_POST_LIKE,
						getIntent().getStringExtra(KEY_MEDIA_ID),
						mLikeCmntListener);
			} else if (mIgReqType == InstagramConstants.IG_REQ_POST_CMNT) {

				mIgWrapper.postCmntOnMedia(InstagramConstants.IG_REQ_POST_CMNT,
						getIntent().getStringExtra(KEY_CMNT_TXT), getIntent()
								.getStringExtra(KEY_MEDIA_ID),
						mLikeCmntListener);
			} else {

				if (null != mIgAuthListener) {
					mIgAuthListener.onAuthSuccess();
					mIgAuthListener = null;
				}
				Log.d(InstagramConstants.TAG, "Finishing IG activity.");
				LoginActivity.this.finish();
			}
			
			*/
		}

		@Override
		public void onAuthFail(String error) {
			Log.d(InstagramConstants.TAG, "Auth Failure Error :" + error);
			Log.d(InstagramConstants.TAG, "Finishing IG activity.");
			
			mIgAuthListener.onAuthFail(error);
			
			LoginActivity.this.finish();
		}
	};
	
	@Override
	public void onSuccess(int reqType) {	
		
		Log.d(InstagramConstants.TAG, "Finishing IG activity.");
		Log.d(InstagramConstants.TAG, "Request success.");
		LoginActivity.this.finish();
	}

	@Override
	public void onFail(int reqType, String error) {
		
		Log.d(InstagramConstants.TAG, "Finishing IG activity.");
		Log.d(InstagramConstants.TAG, "Failure Msg :"+error);
		LoginActivity.this.finish();
	}	
}
