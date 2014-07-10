package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgCmntAdapter;
import com.cryingonion.instashop.instagram.holder.IgCommentHolder;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;
import com.cryingonion.instashop.instagram.listener.IFollowUserListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.listener.IProductInfoFetchedListener;
import com.cryingonion.instashop.instagram.listener.IUserInfoFetchedListener;
import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;
import com.parse.ParseObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StoreDetailsActivity extends Activity {

		// profile image.
		private ImageView mImgVwProfile;
		// User Name
		private TextView mTxtVwName;
		private TextView mTxtVwFullName;
		private TextView mTxtVwFollowerCount;
		private Button mButtonFollow;
		
		private InstagramWrapper mIgWrapper;
		
		private String storeId;
		private int followerCnt;
		
		private String userBio;
		private String userWebsite;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_store_details);
			
			mIgWrapper = new InstagramWrapper(this);
			
			// Get the video info from Extras
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				if (bundle.containsKey("selectedStoreId")) {
					storeId = bundle.getString("selectedStoreId");
					
					// search keyword.
					mIgWrapper.getUserInfo(storeId, mUserListener);
					
				}
			}
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.product_details, menu);
			
			mImgVwProfile = (ImageView)
					findViewById(R.id.imgvw_ig_prof_pic);
			mTxtVwName = (TextView)
					findViewById(R.id.txt_ig_usr_name);
			mTxtVwFullName = (TextView)
					findViewById(R.id.txt_ig_full_name);
			mTxtVwFollowerCount = (TextView)
					findViewById(R.id.txt_ig_follower_count);
			mButtonFollow = (Button)
					findViewById(R.id.btnFollow);
			mButtonFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
			
			return true;
		}
		
		public void setUserDetails(IgUserInfoHolder userHolder)
		{
			
			// Set the profile image
			if (AppUtils.isUrlImage(userHolder.getmUserProfPicUrl())) {

				ImageDownloader.setSquareImg(this, mImgVwProfile,
						userHolder.getmUserProfPicUrl(),
						R.drawable.icon,false,true);	    
			}

			// User Name
			mTxtVwName.setText(userHolder.getmUserName());
	
			// Full Name
			if(!userHolder.getmUserFullName().equals(null))
				mTxtVwFullName.setText(userHolder.getmUserFullName());	
			
			// Bio
			if(!userHolder.getmUserBio().equals(null))
				userBio = userHolder.getmUserBio();	
			
			// Website
			if(!userHolder.getmUserWebSite().equals(null))
				userWebsite = userHolder.getmUserWebSite();	

			// Follower count
			followerCnt = Integer.parseInt(userHolder.getmUserFollowedByCount());
			mTxtVwFollowerCount.setText(followerCnt + " Likes");
			
			mButtonFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mIgWrapper.followUser(storeId, mFollowUserListener);
					followerCnt += 1;
					mTxtVwFollowerCount.setText(followerCnt+"");
				}
			});

		}
		
		/**
		 * User follows success or failure listener.
		 */
		private final IUserInfoFetchedListener mUserListener = new IUserInfoFetchedListener() {
					
			@Override
			public void onIgUsrInfoFetched(IgUserInfoHolder userHolder) {
				if (null != userHolder) {
					
					setUserDetails(userHolder);
					
				}
				
			}

			@Override
			public void onIgUsrInfoFetchingFailed() {
				// TODO Auto-generated method stub
				
			}
		};
		
		private IFollowUserListener mFollowUserListener = new IFollowUserListener() {

			@Override
			public void onCopmplete(int reqType, int responseCode) {

				

			}
		};

}
