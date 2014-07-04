package com.cryingonion.instashop.instagram.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;
import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;

public class IgFollowsModel {

	private static final String DATA = "data";

	private static final String KEY_USER = "user";
	
	private static final String KEY_USERNAME = "username";
	private static final String KEY_ID = "id";
	private static final String KEY_BIO = "bio";
	private static final String KEY_WEBSITEURL = "website";
	private static final String KEY_FULLNAME = "full_name";
	private static final String KEY_PROFILE_PICTURE = "profile_picture";
	private static final String KEY_URL = "url";
	private static final String KEY_PAGINATION = "pagination";
	private static final String KEY_NXT_URL = "next_url";
	private static final String KEY_NXT_MAX_ID = "next_max_id";

	private ArrayList<IgFollowsHolder> mIgFollowsList;
	private String mNxtPageUrl;

	public IgFollowsModel() {

	}

	public IgFollowsModel(String jsonResponse) {

		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONTokener(jsonResponse).nextValue();
			parseResponse(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void parseResponse(JSONObject jsonObj) {

		JSONArray FollowssArray;

		// Pagination link
		if (jsonObj.has(KEY_PAGINATION)) {
			JSONObject jsonPage;
			try {
				jsonPage = jsonObj.getJSONObject(KEY_PAGINATION);
				// Get the next url
				if (jsonPage.has(KEY_NXT_URL)) {
					mNxtPageUrl = jsonPage.optString(KEY_NXT_URL);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (jsonObj.has(DATA)) {
			try {

				FollowssArray = jsonObj.getJSONArray(DATA);
				mIgFollowsList = new ArrayList<IgFollowsHolder>();
				// no. of Follows ie. posts
				int arrayLength = FollowssArray.length();
				Log.d(InstagramConstants.TAG, "Total number of items in Array:"
						+ arrayLength);

				for (int i = 0; i < arrayLength; i++) {
					Log.d(InstagramConstants.TAG, " IG post Num :" + i);

					JSONObject singleFollowsObject = FollowssArray.getJSONObject(i);
					IgFollowsHolder igFollowsHolder = new IgFollowsHolder();

					// Get the ID
					if (singleFollowsObject.has(KEY_ID)) {
						igFollowsHolder.setmUserId(singleFollowsObject
								.optString(KEY_ID));
					}
					
					// Get the Username
					if (singleFollowsObject.has(KEY_USERNAME)) {
						igFollowsHolder.setmUsername(singleFollowsObject
								.optString(KEY_USERNAME));
					}
					
					// Get the Bio
					if (singleFollowsObject.has(KEY_BIO)) {
						igFollowsHolder.setmBio(singleFollowsObject
								.optString(KEY_BIO));
					}

					// Get the Website
					if (singleFollowsObject.has(KEY_WEBSITEURL)) {
						igFollowsHolder.setmWebsiteUrl(singleFollowsObject
								.optString(KEY_WEBSITEURL));
					}
					
					// Get the Profile Picture
					if (singleFollowsObject.has(KEY_PROFILE_PICTURE)) {
						igFollowsHolder.setmProfilePictureUrl(singleFollowsObject
								.optString(KEY_PROFILE_PICTURE));
					}
				
					// Get the ID
					if (singleFollowsObject.has(KEY_ID)) {
						igFollowsHolder.setmUserId(singleFollowsObject
								.optString(KEY_ID));
					}
					
					// Get the Full Name
					if (singleFollowsObject.has(KEY_FULLNAME)) {
						igFollowsHolder.setmFullName(singleFollowsObject
								.optString(KEY_FULLNAME));
					}
					

					// Get the user object.
					if (singleFollowsObject.has(KEY_USER)) {

						JSONObject jsonUser = singleFollowsObject
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

						igFollowsHolder.setmUserInfo(infoHolder);
					}

					mIgFollowsList.add(igFollowsHolder);
				}

			} catch (JSONException e) {
				Log.e(InstagramConstants.TAG,
						"Exception in IgFollowsModel :" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public ArrayList<IgFollowsHolder> getUserIgFollows() {
		return mIgFollowsList;
	}

	public String getIgNxtPageUrl() {
		return mNxtPageUrl;
	}

}
