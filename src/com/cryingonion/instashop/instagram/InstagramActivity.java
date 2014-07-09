package com.cryingonion.instashop.instagram;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.holder.IgCommentHolder;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;
import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;
import com.cryingonion.instashop.instagram.listener.IAuthenticationListener;
import com.cryingonion.instashop.instagram.listener.IFetchCmntsListener;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;
import com.cryingonion.instashop.instagram.listener.IFetchIgFollowsListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.listener.IReqStatusListener;
import com.cryingonion.instashop.instagram.listener.IUserInfoFetchedListener;

public class InstagramActivity  extends Activity implements IReqStatusListener{

	public static final String KEY_USER_ID = "user_id";
    public static final String KEY_MEDIA_ID = "media_id";
    public static final String KEY_CMNT_TXT = "cmnt_txt";
    public static final String KEY_NXT_PG_URL = "nxt_pg_url";
    public static final String KEY_FEED_COUNT = "feed_count";
    public static final String KEY_FOLLOW_COUNT = "follow_count";
    
	private InstagramWrapper mIgWrapper;
	private Intent mIgReqIntent;
	private int mIgReqType;
	
	private static ILikeCmntListener mIgActionListener;
    private static IFetchIgFeedsListener mIgFeedsListener;
    private static IFetchIgFollowsListener mIgFollowsListener;
    private static IFetchCmntsListener mIgFetchCmntListener;
    private static IAuthenticationListener mIgAuthListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the request type for Instagram
		mIgReqIntent = getIntent();
		mIgReqType = mIgReqIntent.getIntExtra(InstagramConstants.IG_REQUEST, 0);

		mIgWrapper = new InstagramWrapper(this,mAuthListener);	
		mIgWrapper.setReqStatusListener(this);
				
		/**
		 * Check for Instagram request type.
		 */
		switch (mIgReqType) {
		
		case InstagramConstants.IG_REQ_LOGIN:
		    // IG login request 
			mIgWrapper.loginInstagram();
		    break;

		case InstagramConstants.IG_REQ_USER_INFO:			
		    // IG user info 
			mIgWrapper.getUserInfo(getIntent()
			    .getStringExtra(KEY_USER_ID),mUserInfoListener);
		    break;
		case InstagramConstants.IG_REQ_USER_FEEDS:			
		    // IG user feeds 
//			mIgWrapper.getUserFeeds(getIntent()
//			    .getStringExtra(KEY_USER_ID),mUserFeedsListener);
			// IG user feeds
		    mIgWrapper.getUserFeeds(getIntent().getStringExtra(KEY_USER_ID),
			    getIntent().getIntExtra(KEY_FEED_COUNT, 100),
			    getIntent().getStringExtra(KEY_NXT_PG_URL),
			    mUserFeedsListener);
		    break;
		case InstagramConstants.IG_REQ_USER_FOLLOWS:			
		    // IG user feeds 
//			mIgWrapper.getUserFeeds(getIntent()
//			    .getStringExtra(KEY_USER_ID),mUserFeedsListener);
			// IG user feeds
		    mIgWrapper.getUserFollows(getIntent().getStringExtra(KEY_USER_ID),
			    getIntent().getIntExtra(KEY_FOLLOW_COUNT, 20),
			    getIntent().getStringExtra(KEY_NXT_PG_URL),
			    mUserFollowsListener);
		    break;
		case InstagramConstants.IG_REQ_GET_CMNTS:			
		    // Comments on media
			mIgWrapper.getCommentsOnMedia(getIntent()
			    .getStringExtra(KEY_MEDIA_ID),mCmntsListener);
		    break;
		    
		default:
		    break;
		}
	}	
	
	public static void setIgAuthListener(IAuthenticationListener listener) {
		mIgAuthListener = listener;
	}
	
	public static void setIgActionListener(ILikeCmntListener listener) {
		mIgActionListener = listener;
	}

	public static void setIgFeedListener(IFetchIgFeedsListener listener) {
		mIgFeedsListener = listener;
	}
	
	public static void setIgFollowListener(IFetchIgFollowsListener listener) {
		mIgFollowsListener = listener;
	}

	public static void setIgFetchCmntListener(IFetchCmntsListener listener) {
		mIgFetchCmntListener = listener;
	}

	/**
	 * Authentication success or failure listener.
	 */
	private final IAuthenticationListener mAuthListener = new IAuthenticationListener() {

		@Override
		public void onAuthSuccess() {
			Log.d(InstagramConstants.TAG, "Finishing IG activity.");
			InstagramActivity.this.finish();

			Log.d(InstagramConstants.TAG, "In IG Auth success.");
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
				InstagramActivity.this.finish();
			}
		}

		@Override
		public void onAuthFail(String error) {
			Log.d(InstagramConstants.TAG, "Auth Failure Error :" + error);
			Log.d(InstagramConstants.TAG, "Finishing IG activity.");
			
			mIgAuthListener.onAuthFail(error);
			
			InstagramActivity.this.finish();
		}
	};
	
	/**
	 * User Info success or failure listener.
	 */
	private final IUserInfoFetchedListener mUserInfoListener = new IUserInfoFetchedListener() {
		
		@Override
		public void onIgUsrInfoFetchingFailed() {
			InstagramActivity.this.finish();			
		}
		
		@Override
		public void onIgUsrInfoFetched(IgUserInfoHolder usrInfoHolder) {
			
			Log.d(InstagramConstants.TAG,
					"Full Name :" + usrInfoHolder.getmUserFullName());
			InstagramActivity.this.finish();		
		}
	};
	
	/**
	 * User Feeds success or failure listener.
	 */
	private final IFetchIgFeedsListener mUserFeedsListener = new IFetchIgFeedsListener() {
				
		@Override
		public void onIgFeedsFetched(ArrayList<IgFeedHolder> feedList,
				String nxtPgUrl) {
			
			InstagramActivity.this.finish();	
		}		
	};
	
	/**
	 * User Feeds success or failure listener.
	 */
	private final IFetchIgFollowsListener mUserFollowsListener = new IFetchIgFollowsListener() {
				
		@Override
		public void onIgFollowsFetched(ArrayList<IgFollowsHolder> followList,
				String nxtPgUrl) {
			
			InstagramActivity.this.finish();	
		}		
	};
	
	/**
	 * Comments fetch success or failure listener.
	 */
	private final IFetchCmntsListener mCmntsListener = new IFetchCmntsListener() {
		
		@Override
		public void onIgCmntsFetched(ArrayList<IgCommentHolder> cmntList) {			
			InstagramActivity.this.finish();
		}
		
		@Override
		public void onIgCmntsFailed() {			
			InstagramActivity.this.finish();
		}
	};
	
	/**
     * Post like,Comment success or failure listener.
     */
	private final ILikeCmntListener mLikeCmntListener = new ILikeCmntListener() {

		@Override
		public void onCopmplete(int reqType, int responseCode) {

			mIgActionListener.onCopmplete(reqType, responseCode);
			InstagramActivity.this.finish();
		}
	};

	@Override
	public void onSuccess(int reqType) {	
		
		Log.d(InstagramConstants.TAG, "Finishing IG activity.");
		Log.d(InstagramConstants.TAG, "Request success.");
		InstagramActivity.this.finish();
	}

	@Override
	public void onFail(int reqType, String error) {
		
		Log.d(InstagramConstants.TAG, "Finishing IG activity.");
		Log.d(InstagramConstants.TAG, "Failure Msg :"+error);
		InstagramActivity.this.finish();
	}
}
