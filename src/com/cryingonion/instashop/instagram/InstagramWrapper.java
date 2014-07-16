package com.cryingonion.instashop.instagram;

import android.content.Context;

import com.cryingonion.instashop.instagram.listener.IAuthenticationListener;
import com.cryingonion.instashop.instagram.listener.IFetchCmntsListener;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;
import com.cryingonion.instashop.instagram.listener.IFetchIgFollowsListener;
import com.cryingonion.instashop.instagram.listener.IFollowUserListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.listener.IProductInfoFetchedListener;
import com.cryingonion.instashop.instagram.listener.IReqStatusListener;
import com.cryingonion.instashop.instagram.listener.IUserInfoFetchedListener;

public class InstagramWrapper {

	private Context mContext;
	private InstagramManager mIgManager;
	private IReqStatusListener mReqListener;
	
	/**
	 * initializes Instagram wrapper
	 * 
	 * @param context
	 */
	public InstagramWrapper(Context context) {
		
		mContext = context;		
		mIgManager = new InstagramManager(context);					
	}
	
    public InstagramWrapper(Context context,IAuthenticationListener listener) {
		
		this(context);		
		mIgManager.setAuthListener(listener);			
	}
	
	/**
     * checks if the user is logged in with Instagram
     * 
     * @return
     */
	public boolean isLoggedIn() {
		return (mIgManager.hasAccessToken() ? true : false);
	}	
	
	/**
     * login through Instagram
     */
    public void loginInstagram() {
    	
    	if (!isLoggedIn())
			mIgManager.showLoginDialog();
		else {
			return;
		}
    }
    
    /**
     * logs out the user from Instagram
     */
	public void logoutInstagram() {
		mIgManager.resetAccessToken();
	}
	
	public void setReqStatusListener(IReqStatusListener listener) {
		mReqListener = listener;
		mIgManager.setReqStatusListener(listener);
	}
	
	/**
	 * Get the user id.
	 * @return
	 */
	public String getUserId(){
		return mIgManager.getUserId();
	}
	
	/**
	 * Get all the Information about the authenticated User.
	 *   
	 * @param userId
	 * @param listener
	 */
	public void getUserInfo(String userId, IUserInfoFetchedListener listener){
		mIgManager.getUserinfo(userId, listener);
	}
	
	/**
	 * Get all the follows of a particular user.
	 * 
	 * @param userId
	 * @param listener
	 */
	public void getUserFollows(String userId,IFetchIgFollowsListener listener){
		mIgManager.getUserFollows(userId,listener);
	}
	
	/**
     * Get all the follows of a particular user.
     * 
     * @param userId
     * @param nxtPgUrl
     * @param listener
     */
	public void getUserFollows(String userId, int followCount, String nxtPgUrl,
			IFetchIgFollowsListener listener) {
		mIgManager.getUserFollows(userId, followCount, nxtPgUrl, listener);
	}
	
	/**
	 * Get all the feeds(posts) of a particular user.
	 * 
	 * @param userId
	 * @param listener
	 */
	public void getUserFeeds(String userId,IFetchIgFeedsListener listener){
		mIgManager.getUserFeeds(userId,listener);
	}
	
	/**
     * Get all the feeds(posts) of a particular user.
     * 
     * @param userId
     * @param nxtPgUrl
     * @param listener
     */
	public void getUserFeeds(String userId, int feedCount, String nxtPgUrl,
			IFetchIgFeedsListener listener) {
		mIgManager.getUserFeeds(userId, feedCount, nxtPgUrl, listener);
	}
	
	/**
	 * Get all the wishlist of a particular user.
	 * 
	 * @param userId
	 * @param listener
	 */
	public void getWishlist(String userId,IFetchIgFeedsListener listener){
		mIgManager.getWishlist(userId,listener);
	}
	
	
	
	/**
	 * Get all the Information about the authenticated Product.
	 *   
	 * @param mediaId
	 * @param listener
	 */
	public void getProductInfo(String mediaId, IProductInfoFetchedListener listener){
		mIgManager.getProductinfo(mediaId, listener);
	}
	
	/**
     * Get all the comments on a particular media.
     * 
     * @param mediaId
     * @param listener
     */
	public void getCommentsOnMedia(String mediaId, IFetchCmntsListener listener) {
		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.getCommentsOnMedia(mediaId, listener);
		}
	}
	
	/**
     * To Post a like on a media.
     * 
     * @param mediaId
     * @param listener
     */
//    public void postLikeOnMedia(String mediaId,ILikeCmntListener listener){
//    	mIgManager.postLikeOnMedia(mediaId, listener);    	
//    }
    
    /**
     * To Post a like on a media.
     * 
     * @param reqType - type of request.
     * @param mediaId
     * @param listener
     */
	public void postLikeOnMedia(int reqType, String mediaId,
			ILikeCmntListener listener) {

		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.postLikeOnMedia(reqType, mediaId, listener);
		}
	}
	
	/**
     * To Post a like on a media.
     * 
     * @param reqType - type of request.
     * @param mediaId
     * @param listener
     */
	public void followUser(String userId,
			IFollowUserListener listener) {

		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.followUser(userId, listener);
		}
	}
    
    /**
     * To Post a Comment on a media.
     * 
     * @param mediaId
     * @param listener
     */
    
	public void postCmntOnMedia(int reqType, String cmntTxt, String mediaId,
			ILikeCmntListener listener) {
		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.postCmntOnMedia(reqType, cmntTxt, mediaId, listener);
		}
	}

	/**
	 * Search people by name.
	 * 
	 * @param userId
	 * @param listener
	 */
	public void searchPeople(String keyword,IFetchIgFollowsListener listener){
		mIgManager.searchPeople(keyword,listener);
	}
	
	/**
     * Search people by name.
     * 
     * @param userId
     * @param nxtPgUrl
     * @param listener
     */
	public void searchPeople(String keyword, int followCount, String nxtPgUrl,
			IFetchIgFollowsListener listener) {
		mIgManager.searchPeople(keyword, followCount, nxtPgUrl, listener);
	}
	
	/**
	 * Search product by tag.
	 * 
	 * @param userId
	 * @param listener
	 */
	public void searchTag(String keyword,IFetchIgFeedsListener listener){
		mIgManager.searchTag(keyword,listener);
	}
	
	/**
     * Search product by tag.
     * 
     * @param userId
     * @param nxtPgUrl
     * @param listener
     */
	public void searchTag(String keyword, int followCount, String nxtPgUrl,
			IFetchIgFeedsListener listener) {
		mIgManager.searchTag(keyword, followCount, nxtPgUrl, listener);
	}
	
}
