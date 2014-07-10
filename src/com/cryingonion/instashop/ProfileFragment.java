package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;
import com.cryingonion.instashop.instagram.listener.IFollowUserListener;
import com.cryingonion.instashop.instagram.listener.IUserInfoFetchedListener;
import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

	public ProfileFragment(){}
	
	// profile image.
	private ImageView mImgVwProfile;
	// User Name
	private TextView mTxtVwName;
	private TextView mTxtVwFullName;
	private TextView mTxtVwFollowerCount;
	private TextView mTxtVwFollowingCount;
	
	private InstagramWrapper mIgWrapper;
	private InstagramManager mIgManager;

	private String mIgUserId; 
	
	private String storeId;
	private int followerCnt;
	private int followingCnt;
	
	private String userBio;
	private String userWebsite;
	
	protected Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
         
        context = rootView.getContext();
        
        mIgWrapper = new InstagramWrapper(context);
		
        mImgVwProfile = (ImageView)
        		rootView.findViewById(R.id.imgvw_ig_prof_pic);
		mTxtVwName = (TextView)
				rootView.findViewById(R.id.txt_ig_usr_name);
		mTxtVwFullName = (TextView)
				rootView.findViewById(R.id.txt_ig_full_name);
		mTxtVwFollowerCount = (TextView)
				rootView.findViewById(R.id.txt_ig_follower_count);
		mTxtVwFollowingCount = (TextView)
				rootView.findViewById(R.id.txt_ig_following_count);
        
        return rootView;
    }

	@Override
    public void onResume() {
        super.onResume();
        
        getInstaFeeds();     
    }
	
	private void getInstaFeeds(){
		
		mIgManager = new InstagramManager(context);
		mIgUserId = mIgManager.getUserId();
		
		mIgWrapper.getUserInfo(mIgUserId, mUserListener);
	}
	
	public void setUserDetails(IgUserInfoHolder userHolder)
	{
		
		// Set the profile image
		if (AppUtils.isUrlImage(userHolder.getmUserProfPicUrl())) {

			ImageDownloader.setSquareImg(context, mImgVwProfile,
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
		mTxtVwFollowerCount.setText(followerCnt+"");
		
		// Following count
		followingCnt = Integer.parseInt(userHolder.getmUserFollowsCount());
		mTxtVwFollowingCount.setText(followingCnt+"");

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

}
