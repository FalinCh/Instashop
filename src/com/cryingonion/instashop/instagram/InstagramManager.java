package com.cryingonion.instashop.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cryingonion.instashop.instagram.listener.IAuthenticationListener;
import com.cryingonion.instashop.instagram.listener.IFetchCmntsListener;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;
import com.cryingonion.instashop.instagram.listener.IFetchIgFollowsListener;
import com.cryingonion.instashop.instagram.listener.IFollowUserListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.listener.IProductInfoFetchedListener;
import com.cryingonion.instashop.instagram.listener.IReqStatusListener;
import com.cryingonion.instashop.instagram.listener.IUserInfoFetchedListener;
import com.cryingonion.instashop.instagram.InstagramLoginDialog.OAuthDialogListener;
import com.cryingonion.instashop.instagram.model.IgCommentModel;
import com.cryingonion.instashop.instagram.model.IgFeedModel;
import com.cryingonion.instashop.instagram.model.IgFollowsModel;
import com.cryingonion.instashop.instagram.model.IgProductInfoModel;
import com.cryingonion.instashop.instagram.model.IgUserInfoModel;
import com.cryingonion.instashop.model.ParseDataManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class InstagramManager {
	
	private static final String TAG = "InstagramAPI";

	private static final String HTTP_POST = "POST";
	private static final String HTTP_GET = "GET";
	private static final String HTTP_DELETE = "DELETE";
	
	private Context mContext;
	private InstagramSession mSession;
	private InstagramLoginDialog mDialog;
	private IAuthenticationListener mAuthListener;
	private IReqStatusListener mReqStatusListener;
	private ProgressDialog mPrgrsDlg;
	
	// instagram data
	
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	public static final String API_URL = "https://api.instagram.com/v1";
	
	// parse
	
	public ParseDataManager parseManager;
	
	private String mAuthUrl;
	private String mTokenUrl;
	private String mAccessToken;
	
	public InstagramManager(Context context) {
		
		mContext = context;
		
		mSession = new InstagramSession(context);			
		mPrgrsDlg = new ProgressDialog(context);
		mPrgrsDlg.setCancelable(false);
	}
	
	public void setAuthListener(IAuthenticationListener listener) {
		mAuthListener = listener;
	}
	
	public void setReqStatusListener(IReqStatusListener listener) {
		mReqStatusListener = listener;
	}
	
	/**
	 * Get the Token URL
	 * @return
	 */
	public void getTokenUrl() {

		mTokenUrl = TOKEN_URL + "?client_id=" + InstagramConstants.CLIENT_ID
				+ "&client_secret=" + InstagramConstants.CLIENT_SECRET
				+ "&redirect_uri=" + InstagramConstants.CALLBACK_URL
				+ "&grant_type=authorization_code";
	}
	
	/**
	 * Get the Authentication url to get the accesstoken.
	 * 
	 * @return
	 */
	public void getAuthUrl() {

		mAuthUrl = AUTH_URL
				+ "?client_id="
				+ InstagramConstants.CLIENT_ID
				+ "&redirect_uri="
				+ InstagramConstants.CALLBACK_URL
				+ "&response_type=code&display=touch&scope=likes+comments+relationships";
	}
	
	/**
	 * Check if access token is valid
	 * 
	 * @return
	 */
	public boolean hasAccessToken() {
		return (mSession.getAccessToken() == null) ? false : true;
	}
	
	/**
	 * Get the access token.
	 * @return
	 */
	public String getAccessToken(){
		return mSession.getAccessToken();
	}
	
	/**
     * Reset Access token
     */
	public void resetAccessToken() {
		mSession.resetAccessToken();
	}
	
	/**
	 * Get the user id.
	 * @return
	 */
	public String getUserId(){
		return mSession.getId();
	}
	
	/**
     * authorizes the user Login
     */
    public void showLoginDialog() {
    	
    	OAuthDialogListener listener = new OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				new GetAccessTokenTask().execute(code);
			}

			@Override
			public void onError(String error) {
				mAuthListener.onAuthFail("Authorization failed");
			}
			
			@Override
		    public void onIgLoginDlgBackPressed(boolean loginInterrupted) {
			mAuthListener.onAuthFail("Authorization Interrupted.");		
		    }
		};

		getAuthUrl();
		
		mDialog = new InstagramLoginDialog(mContext, mAuthUrl, listener);
		mDialog.show();
    }
    
    /**
     * Get the user information.
     * 
     * @param userId - Id of the user whose information is to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getUserinfo(String userId , IUserInfoFetchedListener listener){
    	new GetUserInfoTask(listener).execute(userId);
    }
    
    /**
     * Get the users posts.
     * 
     * @param userId- Id of the user whose posts are to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getUserFeeds(String userId, IFetchIgFeedsListener listener){
    	new GetUserImagesTask(listener).execute(userId);
    }
    
    /**
     * Get the users posts with pagination.
     * 
     * @param userId - Id of the user whose posts are to be fetched.
     * @param nxtPgUrl - Next page url, obtained in previous response.
     * @param listener - Callback to send the result back.
     */
    public void getUserFeeds(String userId, int feedCount,String nxtPgUrl,IFetchIgFeedsListener listener) {
	new GetUserImagesTask(feedCount,nxtPgUrl,listener).execute(userId);
    }
    
    /**
     * Get the product information.
     * 
     * @param userId - Id of the user whose information is to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getProductinfo(String mediaId , IProductInfoFetchedListener listener){
    	new GetMediaInfoTask(listener).execute(mediaId);
    }
    
    /**
     * Get the comments on a media.
     * 
     * @param mediaId  - Id of the media for which comments are to be fetched.
     * @param listener - Callback to send the result back.
     */
    public void getCommentsOnMedia(String mediaId,IFetchCmntsListener listener){
		new GetCmntOnMediaTask(listener).execute(mediaId);
	}
    
    /**
     * To Post a like on a media.
     * 
     * @param mediaId - Id of the media to be liked.
     * @param listener - Callback to send the result back.
     */
//    public void postLikeOnMedia(String mediaId,ILikeCmntListener listener){
//    	new PostLikeOnMediaTask(listener).execute(mediaId);
//    }
    
    /**
     * To Follow User.
     * 
     * @param reqType - type of the request ie. like
     * @param userId - Id of the user to be followed.
     * @param listener - Callback to send the result back.
     */
    public void followUser(String userId, IFollowUserListener listener) {
    	new FollowUserTask(listener).execute(userId);
    }
    
    /**
     * To Post a like on a media.
     * 
     * @param reqType - type of the request ie. like
     * @param mediaId - Id of the media to be liked.
     * @param listener - Callback to send the result back.
     */
    public void postLikeOnMedia(int reqType,String mediaId, ILikeCmntListener listener) {
	new PostLikeOnMediaTask(reqType,listener).execute(mediaId);
    }
    
    /**
     * To Post a Comment on a media.
     * 
     * @param reqType - type of request ie. Comment
     * @param cmntTxt - Comment text.
     * @param mediaId - Id of the media for which comment is to be posted.
     * @param listener - Callback to send the result back.
     */
    public void postCmntOnMedia(int reqType,String cmntTxt,String mediaId,ILikeCmntListener listener){
    	new PostCmntOnMediaTask(reqType,cmntTxt,listener).execute(mediaId);
    }
    
    /**
     * To undo like on a post.
     * 
     * @param mediaId - Id of the media for which like is to be undone.
     * @param listener - Callback to send the result back.
     */
    public void deleteLikeOnMedia(String mediaId,ILikeCmntListener listener){
    	
    }
    
    /**
     * Get the users follows.
     * 
     * @param userId- Id of the user whose follows are to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getUserFollows(String userId, IFetchIgFollowsListener listener){
		new GetUserFollowsTask(listener).execute(userId);
    }
    
    /**
     * Get the users follows with pagination.
     * 
     * @param userId - Id of the user whose follows are to be fetched.
     * @param nxtPgUrl - Next page url, obtained in previous response.
     * @param listener - Callback to send the result back.
     */
    public void getUserFollows(String userId, int followCount,String nxtPgUrl,IFetchIgFollowsListener listener) {
    	new GetUserFollowsTask(followCount,nxtPgUrl,listener).execute(userId);
    }


    /**
     * Search people.
     * 
     * @param userId- Id of the user whose follows are to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void searchPeople(String keyword, IFetchIgFollowsListener listener){
		new SearchPeopleTask(listener).execute(keyword);
    }
    
    /**
     * Search people with pagination.
     * 
     * @param userId - Id of the user whose follows are to be fetched.
     * @param nxtPgUrl - Next page url, obtained in previous response.
     * @param listener - Callback to send the result back.
     */
    public void searchPeople(String keyword, int peopleCount,String nxtPgUrl,IFetchIgFollowsListener listener) {
    	new SearchPeopleTask(peopleCount,nxtPgUrl,listener).execute(keyword);
    }

    /**
     * Search product tag.
     * 
     * @param userId- Id of the user whose follows are to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void searchTag(String keyword, IFetchIgFeedsListener listener){
		new SearchTagTask(listener).execute(keyword);
    }
    
    /**
     * Search product tag with pagination.
     * 
     * @param userId - Id of the user whose follows are to be fetched.
     * @param nxtPgUrl - Next page url, obtained in previous response.
     * @param listener - Callback to send the result back.
     */
    public void searchTag(String keyword, int productCount,String nxtPgUrl,IFetchIgFeedsListener listener) {
    	new SearchTagTask(productCount,nxtPgUrl,listener).execute(keyword);
    }

    /**
     * Get the users wishlist.
     * 
     * @param userId- Id of the user whose posts are to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getWishlist(String userId, IFetchIgFeedsListener listener){
    	new GetWishlistTask(listener).execute(userId);
    }
    
    
    
    
    /**
     * Async Task to get the AccessToken  
     */    
    class GetAccessTokenTask extends AsyncTask<String, Boolean, Boolean> {
    	
    	@Override
    	protected void onPreExecute() {
    	    super.onPreExecute();
    	    mPrgrsDlg.setMessage("Getting access token ...");
    	    // If activity is finishing , don't show the dialog
    	    // as it will cause bad window token exception
    	    if (!((Activity) mContext).isFinishing())
    		mPrgrsDlg.show();
    	}
    	
    	@Override
    	protected Boolean doInBackground(String... params) {
    		
    		Log.d(TAG, "Getting access token");
			
			try {
				URL url = new URL(TOKEN_URL);				
				Log.d(TAG, "Opening Token URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("client_id="+InstagramConstants.CLIENT_ID+
							"&client_secret="+InstagramConstants.CLIENT_SECRET+
							"&grant_type=authorization_code" +
							"&redirect_uri="+InstagramConstants.CALLBACK_URL+
							"&code=" + params[0]);				
				
			    writer.flush();
			    
				String response = streamToString(urlConnection.getInputStream());
				Log.d(TAG, "response " + response);
				
				JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
				
				mAccessToken = jsonObj.getString("access_token");
				Log.i(TAG, "Got access token: " + mAccessToken);
				
				String id = jsonObj.getJSONObject("user").getString("id");
				String user = jsonObj.getJSONObject("user").getString("username");
				String name = jsonObj.getJSONObject("user").getString("full_name");					
				String userImage = jsonObj.getJSONObject("user").getString(
                        "profile_picture");
				String bio = jsonObj.getJSONObject("user").getString("bio");
				
				mSession.storeAccessToken(mAccessToken, id, user, name, userImage);
				
				signUpToParse(mAccessToken, id, user, name, userImage, bio);
				
				
				return true;
			} catch (Exception ex) {				
				ex.printStackTrace();
				return false;
			}			
    	}
    	
    	@Override
		protected void onPostExecute(Boolean result) {

    		if(null != mPrgrsDlg && mPrgrsDlg.isShowing())
    			mPrgrsDlg.dismiss();
			if (result) {
				if(null != mAuthListener)
				mAuthListener.onAuthSuccess();
			} else {
				if(null != mAuthListener)
				mAuthListener.onAuthFail("Failed to get access token");
			}
		}
    }
    
    
    public void signUpToParse (String accessToken, String id, String username, String name, String userImage, String bio)
	{
		ParseObject user = new ParseObject("User");
		user.put("username",username);
		user.put("userId",id);
		user.put("name",name);
		user.put("instagramToken",accessToken);
		user.put("userImage",userImage);
		user.put("userBio",bio);
		  
		user.saveInBackground();
	}
	
    /**
     * Async Task to get the user info.  
     */ 
	class GetUserInfoTask extends AsyncTask<String, IgUserInfoModel, IgUserInfoModel> {

		IUserInfoFetchedListener mUserInfoListener;		
		
		public GetUserInfoTask(){			
		}
		
        public GetUserInfoTask(IUserInfoFetchedListener listener){
        	mUserInfoListener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Getting user info ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}

		@Override
		protected IgUserInfoModel doInBackground(String... params) {

			Log.d(TAG, "Fetching user info");

			try {
				URL url = new URL(API_URL + "/users/" + params[0]
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Fetch user info response :" + response);

				IgUserInfoModel userInfoModel = new IgUserInfoModel(response);	

				return userInfoModel;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		}

		@Override
		protected void onPostExecute(IgUserInfoModel model) {

			if(null != mPrgrsDlg && mPrgrsDlg.isShowing())
    			mPrgrsDlg.dismiss();
			
			if (null != model) {
				mUserInfoListener.onIgUsrInfoFetched(model.getDataHolder());				
			} else {
				mUserInfoListener.onIgUsrInfoFetchingFailed();
			}		
		}
	}
	
	 /**
     * Async Task to get the Images (Feeds).  
     */
    
	class GetUserImagesTask extends AsyncTask<String, IgFeedModel, IgFeedModel>{
		
		IFetchIgFeedsListener mFeedListener;
		String mNxtUrl;
		String mCount = "100"; // Default feedcount.	
		
		public GetUserImagesTask (){
			
		}
		
		public GetUserImagesTask(IFetchIgFeedsListener listener){
			mFeedListener = listener;
		}
		
		public GetUserImagesTask(int feedCount,String nxtUrl ,IFetchIgFeedsListener listener) {
		    this(listener);
		    mCount = feedCount+"";
		    mNxtUrl = nxtUrl;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			mPrgrsDlg.setMessage("Getting user photos ...");
//			// If activity is finishing , don't show the dialog
//		    // as it will cause bad window token exception
//		    if (!((Activity) mContext).isFinishing())
//			mPrgrsDlg.show();
		}
		
		@Override
		protected IgFeedModel doInBackground(String... params) {
			
			Log.d(TAG, "Fetching user photos");
			
			try {
				URL url = null;
				
				if (null == mNxtUrl) {
					
					if (hasAccessToken())
						url = new URL(API_URL + "/users/" + params[0]
								+ "/media/recent" + "/?access_token="
								+ getAccessToken());
					else
						url = new URL(API_URL + "/users/" + params[0]
								+ "/media/recent" + "/?client_id="
								+ InstagramConstants.CLIENT_ID);
				} else {
					url = new URL(mNxtUrl);
				}
				
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				urlConnection.connect();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "User photos response :" + response);	
				
				IgFeedModel feedModel = new IgFeedModel(response);					
				
				return feedModel;
			}catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		};
		
		@Override
		protected void onPostExecute(IgFeedModel feedModel) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
			
			if (null != feedModel) {
				mFeedListener.onIgFeedsFetched(feedModel.getUserIgFeeds(),
						feedModel.getIgNxtPageUrl());
			} else {
				mFeedListener.onIgFeedsFetched(null, null);
			}
		};
	}
	
	/**
     * Async Task to get the Comments on a Media.  
     */
    
	class GetCmntOnMediaTask extends AsyncTask<String, IgCommentModel, IgCommentModel>{
		
		IFetchCmntsListener mCmntListener;
		public GetCmntOnMediaTask(){}
		
		public GetCmntOnMediaTask(IFetchCmntsListener listener){
			mCmntListener = listener;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Getting comments on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected IgCommentModel doInBackground(String... params) {
			
			Log.d(TAG, "Fetching comments on media");

			try {
				URL url = new URL(API_URL + "/media/" + params[0] +"/comments"
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Fetch comment response :" + response);

				IgCommentModel cmntModel = new IgCommentModel(response);				
				return cmntModel;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}		
			
		}
		
		@Override
		protected void onPostExecute(IgCommentModel model) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
            
            if(null != model){
            	mCmntListener.onIgCmntsFetched(model.getCommentList());
            }else{
            	mCmntListener.onIgCmntsFailed();
            }
		}
	}
	
	
	
	/**
	 * Asynctask to post like on a media.
	 * 
	 * @author falinch
	 *
	 */
	class PostLikeOnMediaTask extends AsyncTask<String, Integer, Integer>{
		
		ILikeCmntListener mLikeListener;
		int mReqType;
		
		public PostLikeOnMediaTask(){}
		
		public PostLikeOnMediaTask(ILikeCmntListener listener) {
			mLikeListener = listener;
		}
		
		public PostLikeOnMediaTask(int reqType, ILikeCmntListener listener) {
			this(listener);
			mReqType = reqType;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Posting like on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
		
			Log.d(TAG, "Posting like on media");

			try {
				
				URL url = new URL(API_URL + "/media/" + params[0] + "/likes");

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);				
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				
				/*List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair("access_token", getAccessToken()));
				
				OutputStream os = urlConnection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(postParams));
				writer.flush();
				writer.close();
				os.close();
				
				urlConnection.connect();*/
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("access_token="+getAccessToken());				
				
			    writer.flush();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Post like response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
            mPrgrsDlg.dismiss();
			
			mLikeListener.onCopmplete(mReqType,responseCode);
		}
	}
	
	/**
	 * Asynctask to post comment on a media.
	 * 
	 * @author falinch
	 *
	 */
	class PostCmntOnMediaTask extends AsyncTask<String, Integer, Integer>{
		
		ILikeCmntListener mCmntListener;
		int mReqType;
		String mCmntTxt;
		
		public PostCmntOnMediaTask(){}
		
		public PostCmntOnMediaTask(ILikeCmntListener listener) {
			mCmntListener = listener;
		}
		
		public PostCmntOnMediaTask(int reqType,String cmntTxt,ILikeCmntListener listener) {
		    this(listener);
		    mReqType = reqType;
		    mCmntTxt = cmntTxt;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Posting Commment on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
		
			Log.d(TAG, "Posting cmnt on media");

			try {
				
				URL url = new URL(API_URL + "/media/" + params[0] + "/comments");

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);				
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);		
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("access_token="+getAccessToken()+
						"&text="+mCmntTxt);				
				
			    writer.flush();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Post cmnt response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();

			mCmntListener.onCopmplete(mReqType, responseCode);
		}
	}
	
	/**
	 * Asynctask to undo like on a media.
	 * 
	 * @author falinch
	 *
	 */
	class DeleteLikeOnPostTask extends AsyncTask<String, Integer, Integer>{
		
		ILikeCmntListener mCmntListener;
		int mReqType;
		
		public DeleteLikeOnPostTask(){}
		
		public DeleteLikeOnPostTask(ILikeCmntListener listener) {
			mCmntListener = listener;
		}
		
		public DeleteLikeOnPostTask(int reqType,ILikeCmntListener listener) {
		    this(listener);
		    mReqType = reqType;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Undoing like on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			
			Log.d(TAG, "Undoing like on media");

			try {
				URL url = new URL(API_URL + "/media/" + params[0] +"/likes"
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_DELETE);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				
				String response = streamToString(urlConnection.getInputStream());
				
                Log.d(TAG, "Undo like response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}	
			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();

			mCmntListener.onCopmplete(mReqType, responseCode);
		}
	}
	
	/**
	 * To convert Stream to String.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
    private String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}
    
	/*private String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}*/
	
    
	class GetUserFollowsTask extends AsyncTask<String, IgFollowsModel, IgFollowsModel>{
			
			IFetchIgFollowsListener mFollowsListener;
			String mNxtUrl;
			String mCount = "20"; // Default feedcount.	
			
			public GetUserFollowsTask (){
				
			}
			
			public GetUserFollowsTask(IFetchIgFollowsListener listener){
				mFollowsListener = listener;
			}
			
			public GetUserFollowsTask(int followCount,String nxtUrl ,IFetchIgFollowsListener listener) {
			    this(listener);
			    mCount = followCount+"";
			    mNxtUrl = nxtUrl;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mPrgrsDlg.setMessage("Getting user follows ...");
				// If activity is finishing , don't show the dialog
			    // as it will cause bad window token exception
			    if (!((Activity) mContext).isFinishing())
				mPrgrsDlg.show();
			}
			
			@Override
			protected IgFollowsModel doInBackground(String... params) {
				
				Log.d(TAG, "Getting user data");
				
				try {
					URL url = null;
					
					if (null == mNxtUrl) {
						
						if (hasAccessToken())
							url = new URL(API_URL + "/users/" + params[0]
									+ "/follows" + "/?access_token=" + getAccessToken());
						else
							url = new URL(API_URL + "/users/" + params[0]
									+ "/follows" + "/?access_token=" + getAccessToken());
					} else {
						url = new URL(mNxtUrl);
					}
					
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setRequestMethod(HTTP_GET);
					urlConnection.connect();
					
					String response = streamToString(urlConnection.getInputStream());
	
					Log.d(TAG, "User follows response :" + response);	
					
					IgFollowsModel followsModel = new IgFollowsModel(response);					
					
					return followsModel;
					
				}catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}			
			};
			
			protected void onPostExecute(IgFollowsModel followsModel) {
				
				if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
					mPrgrsDlg.dismiss();
				
				if (null != followsModel) {
					mFollowsListener.onIgFollowsFetched(followsModel.getUserIgFollows(),
							followsModel.getIgNxtPageUrl());
				} else {
					mFollowsListener.onIgFollowsFetched(null, null);
				}
			};
		}
	    
	class SearchPeopleTask extends AsyncTask<String, IgFollowsModel, IgFollowsModel>{
		
		IFetchIgFollowsListener mFollowsListener;
		String mNxtUrl;
		String mCount = "20"; // Default feedcount.	
		
		public SearchPeopleTask (){
			
		}
		
		public SearchPeopleTask(IFetchIgFollowsListener listener){
			mFollowsListener = listener;
		}
		
		public SearchPeopleTask(int followCount,String nxtUrl ,IFetchIgFollowsListener listener) {
		    this(listener);
		    mCount = followCount+"";
		    mNxtUrl = nxtUrl;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			mPrgrsDlg.setMessage("Searching Store ...");
//			// If activity is finishing , don't show the dialog
//		    // as it will cause bad window token exception
//		    if (!((Activity) mContext).isFinishing())
//			mPrgrsDlg.show();
		}
		
		@Override
		protected IgFollowsModel doInBackground(String... params) {
			
			Log.d(TAG, "Getting user data");
			
			try {
				URL url = null;
				
				if (null == mNxtUrl) {
					
					if (hasAccessToken())
						url = new URL(API_URL + "/users/"
								+ "search?q=" + params[0] + "&access_token=" + getAccessToken());
					else
						url = new URL(API_URL + "/users/"
								+ "search?q=" + params[0] + "&access_token=" + getAccessToken());
				} else {
					url = new URL(mNxtUrl);
				}
				
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				urlConnection.connect();
				
				String response = streamToString(urlConnection.getInputStream());
	
				Log.d(TAG, "User follows response :" + response);	
				
				IgFollowsModel followsModel = new IgFollowsModel(response);					
				
				return followsModel;
				
			}catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		};
		
		protected void onPostExecute(IgFollowsModel followsModel) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
			
			if (null != followsModel) {
				mFollowsListener.onIgFollowsFetched(followsModel.getUserIgFollows(),
						followsModel.getIgNxtPageUrl());
			} else {
				mFollowsListener.onIgFollowsFetched(null, null);
			}
		};
	}
   
	class SearchTagTask extends AsyncTask<String, IgFeedModel, IgFeedModel>{
		
		IFetchIgFeedsListener mFeedListener;
		String mNxtUrl;
		String mCount = "100"; // Default feedcount.	
		
		public SearchTagTask (){
			
		}
		
		public SearchTagTask(IFetchIgFeedsListener listener){
			mFeedListener = listener;
		}
		
		public SearchTagTask(int feedCount,String nxtUrl ,IFetchIgFeedsListener listener) {
		    this(listener);
		    mCount = feedCount+"";
		    mNxtUrl = nxtUrl;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			mPrgrsDlg.setMessage("Search Product ...");
//			// If activity is finishing , don't show the dialog
//		    // as it will cause bad window token exception
//		    if (!((Activity) mContext).isFinishing())
//			mPrgrsDlg.show();
		}
		
		@Override
		protected IgFeedModel doInBackground(String... params) {
			
			Log.d(TAG, "Fetching user photos");
			
			try {
				URL url = null;
				
				if (null == mNxtUrl) {
					
					if (hasAccessToken())
						url = new URL(API_URL + "/tags/" + params[0]
								+ "/media/recent" + "/?access_token="
								+ getAccessToken());
					else
						url = new URL(API_URL + "/tags/" + params[0]
								+ "/media/recent" + "/?client_id="
								+ InstagramConstants.CLIENT_ID);
				} else {
					url = new URL(mNxtUrl);
				}
				
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				urlConnection.connect();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "User photos response :" + response);	
				
				IgFeedModel feedModel = new IgFeedModel(response);					
				
				return feedModel;
			}catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		};
		
		@Override
		protected void onPostExecute(IgFeedModel feedModel) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
			
			if (null != feedModel) {
				mFeedListener.onIgFeedsFetched(feedModel.getUserIgFeeds(),
						feedModel.getIgNxtPageUrl());
			} else {
				mFeedListener.onIgFeedsFetched(null, null);
			}
		};
	}
	
	
	/**
     * Async Task to get the user info.  
     */ 
	class GetMediaInfoTask extends AsyncTask<String, IgProductInfoModel, IgProductInfoModel> {

		IProductInfoFetchedListener mProductInfoListener;		
		
		public GetMediaInfoTask(){			
		}
		
        public GetMediaInfoTask(IProductInfoFetchedListener listener){
        	mProductInfoListener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Getting product info ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}

		@Override
		protected IgProductInfoModel doInBackground(String... params) {

			Log.d(TAG, "Fetching product info");

			try {
				URL url = new URL(API_URL + "/media/" + params[0]
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Fetch product info response :" + response);

				IgProductInfoModel productInfoModel = new IgProductInfoModel(response);	

				return productInfoModel;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		}

		@Override
		protected void onPostExecute(IgProductInfoModel model) {

			if(null != mPrgrsDlg && mPrgrsDlg.isShowing())
    			mPrgrsDlg.dismiss();
			
			if (null != model) {
				mProductInfoListener.onIgProductInfoFetched(model.getDataHolder());				
			} else {
				mProductInfoListener.onIgProductInfoFetchingFailed();
			}		
		}
	}
	
	/**
	 * Asynctask to Follow User
	 * 
	 * @author falinch
	 *
	 */
	class FollowUserTask extends AsyncTask<String, Integer, Integer>{
		
		IFollowUserListener mFollowListener;
		int mReqType;
		
		public FollowUserTask(){}
		
		public FollowUserTask(IFollowUserListener listener) {
			mFollowListener = listener;
		}
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Following the Store ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
		
			Log.d(TAG, "Posting like on media");

			try {
				
				URL url = new URL(API_URL + "/users/" + params[0]
						+ "/follow?access_token=" + getAccessToken());
				
				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);				
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("access_token="+getAccessToken());				
				
			    writer.flush();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Follow response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
            mPrgrsDlg.dismiss();
			
			mFollowListener.onCopmplete(mReqType,responseCode);
		}
	}
	
	/**
     * Async Task to get the Images (Feeds).  
     */
    
	class GetWishlistTask extends AsyncTask<String, IgFeedModel, IgFeedModel>{
		
		IFetchIgFeedsListener mFeedListener;
		String mNxtUrl;
		String mCount = "100"; // Default feedcount.	
		
		public GetWishlistTask (){
			
		}
		
		public GetWishlistTask(IFetchIgFeedsListener listener){
			mFeedListener = listener;
		}
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			mPrgrsDlg.setMessage("Getting user photos ...");
//			// If activity is finishing , don't show the dialog
//		    // as it will cause bad window token exception
//		    if (!((Activity) mContext).isFinishing())
//			mPrgrsDlg.show();
		}
		
		@Override
		protected IgFeedModel doInBackground(String... params) {
			
			Log.d(TAG, "Fetching user photos");
			
			try {
				
				ArrayList<String> listWishlist = new ArrayList<String>();
				
				listWishlist = getWishlistFromParse(mSession.getId());
				
				ArrayList<JSONObject> wishlistArray = new ArrayList<JSONObject>();
				
				for(int i=0;i<listWishlist.size();i++)
				{
					URL url = new URL(API_URL + "/media/" + params[0]
							+ "/?access_token=" + getAccessToken());
					
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setRequestMethod(HTTP_GET);
					urlConnection.connect();
					
					String response = streamToString(urlConnection.getInputStream());

					JSONObject jsonObject = new JSONObject(response);	
					
					wishlistArray.add(jsonObject);
				}
				
				IgFeedModel feedModel = new IgFeedModel(wishlistArray);					
				
				return feedModel;
			}catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		};
		
		@Override
		protected void onPostExecute(IgFeedModel feedModel) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
			
			if (null != feedModel) {
				mFeedListener.onIgFeedsFetched(feedModel.getUserIgFeeds(),
						feedModel.getIgNxtPageUrl());
			} else {
				mFeedListener.onIgFeedsFetched(null, null);
			}
		};
	}
	
	
	public ArrayList<String> getWishlistFromParse(String userId)
	{
		final ArrayList<String> listWishlist = new ArrayList<String>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Wishlist");
		query.whereEqualTo("username", userId);
		
		query.findInBackground(new FindCallback<ParseObject>() {
		    
			public void done(List<ParseObject> wishlists, ParseException e) {
		        if (e == null) {
		            
		        	for (ParseObject wishlist : wishlists) {
						
		            	String imageId = wishlist.getString("productId");
		            	Log.d("imageId", imageId);
			            
		            	listWishlist.add(imageId);

					}
		            
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		            
		        }
		    }
		});
		
		return listWishlist;
	}
	
	
	
	
}
