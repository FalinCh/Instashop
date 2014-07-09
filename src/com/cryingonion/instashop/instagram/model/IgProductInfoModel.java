package com.cryingonion.instashop.instagram.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;

/**
 * Basic info about the Instagram user.
 * 
 * @author falinch
 */
public class IgProductInfoModel {
	
	private static final String DATA = "data";
	
	private static final String KEY_ID = "id";	
	private static final String KEY_FEED_TYPE = "type";
	private static final String KEY_FEED_TAGS = "tags";
	private static final String KEY_CMNTS = "comments";
	private static final String KEY_LIKES = "likes";
	private static final String KEY_COUNT = "count";
	private static final String KEY_CAPTION = "caption";
	private static final String KEY_USER = "user";
	private static final String KEY_CREATED_TIME = "created_time";
	private static final String KEY_IMAGES = "images";
	private static final String KEY_VIDEOS = "videos";
	private static final String KEY_LOW_RESO = "low_resolution";
	private static final String KEY_THMBNL = "thumbnail";
	private static final String KEY_STD_RESO = "standard_resolution";
	private static final String KEY_URL = "url";
	private static final String KEY_TEXT = "text";
	private static final String KEY_USER_LIKED = "user_has_liked";
	
	private IgFeedHolder mMediaInfoHolder;
	
	public IgProductInfoModel(){
		
	}
	
	public IgProductInfoModel(String jsonResponse) {

		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONTokener(jsonResponse).nextValue();
			parseResponse(jsonObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseResponse(JSONObject jsonObj) {

		JSONObject internal_Obj;
		mMediaInfoHolder = new IgFeedHolder();
		
		if (jsonObj.has(DATA)) {
			try {
				internal_Obj = jsonObj.getJSONObject(DATA);

				if (internal_Obj.has(KEY_ID)) {
					mMediaInfoHolder.setmMediaId(internal_Obj.optString(KEY_ID));
				}

				if (internal_Obj.has(KEY_FEED_TYPE)) {
					mMediaInfoHolder.setmFeedType(internal_Obj
							.optString(KEY_FEED_TYPE));
				}
				
				if (internal_Obj.has(KEY_FEED_TAGS)) {
					mMediaInfoHolder.setmFeedTags(internal_Obj
							.optString(KEY_FEED_TAGS));

				}
				if (internal_Obj.has(KEY_CAPTION)) {
					mMediaInfoHolder.setmCaptionText(internal_Obj
							.optString(KEY_CAPTION));

				}
				if (internal_Obj.has(KEY_CREATED_TIME)) {
					mMediaInfoHolder.setmCreatedTime(internal_Obj
							.optString(KEY_CREATED_TIME));
				}
				
				// Get the like object.
				if (internal_Obj.has(KEY_LIKES)) {
					JSONObject jsonLike = internal_Obj
							.getJSONObject(KEY_LIKES);

					// Get the likes count
					if (jsonLike.has(KEY_COUNT)) {
						mMediaInfoHolder.setmLikesCount(jsonLike
								.optString(KEY_COUNT));
					}
				}
				
				// Get the Images object.
				if (internal_Obj.has(KEY_IMAGES)) {
					JSONObject jsonImage = internal_Obj
							.getJSONObject(KEY_IMAGES);

					// Get the low resolution image object
					if (jsonImage.has(KEY_LOW_RESO)) {

						JSONObject obj = jsonImage
								.getJSONObject(KEY_LOW_RESO);
						if (obj.has(KEY_URL)) {
							// Get the low resolution image url
							mMediaInfoHolder.setmLowResImgUrl(obj
									.optString(KEY_URL));
						}
					}

					// Get the thmbnl image object
					if (jsonImage.has(KEY_THMBNL)) {

						JSONObject obj = jsonImage
								.getJSONObject(KEY_THMBNL);
						if (obj.has(KEY_URL)) {
							// Get the thmbnl image url
							mMediaInfoHolder.setmThmbnlUrl(obj
									.optString(KEY_URL));
						}
					}

					// Get the std resolution image object
					if (jsonImage.has(KEY_STD_RESO)) {

						JSONObject obj = jsonImage
								.getJSONObject(KEY_STD_RESO);
						if (obj.has(KEY_URL)) {
							// Get the std resolution image url
							mMediaInfoHolder.setmStdResImgUrl(obj
									.optString(KEY_URL));
						}
					}
				}
				
				// Get the feed type (Image or Video.)
				if (internal_Obj.has(KEY_FEED_TYPE)) {
					mMediaInfoHolder.setmFeedType(internal_Obj
							.optString(KEY_FEED_TYPE));
				}

				// Get the Video object.
				if (internal_Obj.has(KEY_VIDEOS)) {
					JSONObject jsonVideo = internal_Obj
							.getJSONObject(KEY_VIDEOS);

					// Get the low resolution video object
					if (jsonVideo.has(KEY_LOW_RESO)) {

						JSONObject obj = jsonVideo
								.getJSONObject(KEY_LOW_RESO);
						if (obj.has(KEY_URL)) {
							// Get the low resolution video url
							mMediaInfoHolder.setmVidLowResUrl(obj
									.optString(KEY_URL));
						}
					}

					// Get the std resolution video object
					if (jsonVideo.has(KEY_STD_RESO)) {

						JSONObject obj = jsonVideo
								.getJSONObject(KEY_STD_RESO);
						if (obj.has(KEY_URL)) {
							// Get the std resolution video url
							mMediaInfoHolder.setmVidStdResUrl(obj
									.optString(KEY_URL));
						}
					}
				}

				// If user has liked the media or not
				if (internal_Obj.has(KEY_USER_LIKED)) {
					mMediaInfoHolder.setmLikeStatus(internal_Obj
							.getBoolean(KEY_USER_LIKED));
				}

				// Get the user object.
				if (internal_Obj.has(KEY_USER)) {

					JSONObject jsonUser = internal_Obj
							.getJSONObject(KEY_USER);
					IgUserInfoHolder infoHolder = new IgUserInfoHolder();
					if (jsonUser.has(IgUserInfoModel.USR_ID)) {
						infoHolder.setmUserId(jsonUser
								.optString(IgUserInfoModel.USR_ID));
					}

					if (jsonUser.has(IgUserInfoModel.USR_NAME)) {
						infoHolder.setmUserName(jsonUser
								.optString(IgUserInfoModel.USR_NAME));
					}

					if (jsonUser.has(IgUserInfoModel.USR_FULL_NAME)) {
						infoHolder.setmUserFullName(jsonUser
								.optString(IgUserInfoModel.USR_FULL_NAME));

					}
					if (jsonUser.has(IgUserInfoModel.USR_PROF_PIC_URL)) {
						infoHolder
								.setmUserProfPicUrl(jsonUser
										.optString(IgUserInfoModel.USR_PROF_PIC_URL));

					}
					if (jsonUser.has(IgUserInfoModel.USR_BIO)) {
						infoHolder.setmUserBio(jsonUser
								.optString(IgUserInfoModel.USR_BIO));

					}
					if (jsonUser.has(IgUserInfoModel.USR_WEBSITE)) {
						infoHolder.setmUserWebSite(jsonUser
								.optString(IgUserInfoModel.USR_WEBSITE));
					}

					mMediaInfoHolder.setmUserInfo(infoHolder);
				}
				
			} catch (JSONException e) {	
				Log.e(InstagramConstants.TAG, "Exception in UserInfoModel :"+e.getMessage());
				e.printStackTrace();				
			}

		}
	}
	
	public IgFeedHolder getDataHolder() {		
		return mMediaInfoHolder;
	}
}
