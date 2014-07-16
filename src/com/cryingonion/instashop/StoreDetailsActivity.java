package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgCmntAdapter;
import com.cryingonion.instashop.instagram.adapter.IgFeedAdapter;
import com.cryingonion.instashop.instagram.adapter.IgFeedAdapterGrid;
import com.cryingonion.instashop.instagram.holder.IgCommentHolder;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;
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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class StoreDetailsActivity extends Activity {

		// profile image.
		private ImageView mImgVwProfile;
		// User Name
		private TextView mTxtVwName;
		private TextView mTxtVwFullName;
		private TextView mTxtVwFollowerCount;
		private TextView mTxtVwFollowingCount;
		private Button mButtonFollow;
		private Button mButtonRecommend;
		private Button mButtonContact;
		//private Button mButtonContentByProduct;
		//private Button mButtonContentByInfo;
		private ScrollView mLayoutstoreContentByProduct;
		private ScrollView mLayoutstoreContentByInfo;
		private GridView mGridVwFeeds;
		private View mEmptyView;
		//private TextView mTxtVwBio;
		//private TextView mTxtVwWebsite;
		
		private ArrayList<IgFeedHolder> mIgFeedList;
		private IgFeedAdapterGrid mFeedAdapter;	
		
		
		private InstagramWrapper mIgWrapper;
		
		private String mIgNxtPageUrl = null;
	    private int mFeedCount = 100; // default feed count 
		
		private String storeId;
		private int followerCnt;
		private int followingCnt;
		
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
			mTxtVwFollowingCount = (TextView)
					findViewById(R.id.txt_ig_following_count);
			mButtonFollow = (Button)
					findViewById(R.id.btnFollow);
			mButtonFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
			
			mButtonRecommend = (Button)
					findViewById(R.id.btnRecommend);
			
			mButtonContact = (Button)
					findViewById(R.id.btnContact);
			
//			mButtonContentByProduct = (Button)
//					findViewById(R.id.btnProductByStore);
			
			mLayoutstoreContentByProduct = (ScrollView)
					findViewById(R.id.layoutstoreContentByProduct);
			mLayoutstoreContentByProduct.setVisibility(View.VISIBLE);
			mLayoutstoreContentByProduct.setEnabled(true);
			
			mGridVwFeeds = (GridView) findViewById(R.id.grid_ig_feeds);
			mEmptyView = findViewById(R.id.txt_empty_view);	
			
//			mButtonContentByInfo = (Button)
//					findViewById(R.id.btnInfo);
//			
//			mLayoutstoreContentByInfo = (ScrollView)
//					findViewById(R.id.layoutstoreContentByInfo);
//			mLayoutstoreContentByInfo.setVisibility(View.INVISIBLE);
//			mLayoutstoreContentByInfo.setEnabled(false);
//			
//			mTxtVwBio = (TextView)
//					findViewById(R.id.txt_ig_bio);
//			
//			mTxtVwWebsite = (TextView)
//					findViewById(R.id.txt_ig_website);
			
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
			{
				userBio = userHolder.getmUserBio();
				//mTxtVwBio.setText(userHolder.getmUserBio());
			}	
			
			// Website
			if(!userHolder.getmUserWebSite().equals(null))
			{
				userWebsite = userHolder.getmUserWebSite();	
				//mTxtVwWebsite.setText(userHolder.getmUserWebSite());
			}

			// Follower count
			followerCnt = Integer.parseInt(userHolder.getmUserFollowedByCount());
			mTxtVwFollowerCount.setText(followerCnt + "");
			
			// Follower count
			followingCnt = Integer.parseInt(userHolder.getmUserFollowsCount());
			mTxtVwFollowingCount.setText(followingCnt + "");
			
			mButtonFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mIgWrapper.followUser(storeId, mFollowUserListener);
					followerCnt += 1;
					mTxtVwFollowerCount.setText(followerCnt+"");
				}
			});
			
			mButtonRecommend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
			
			mButtonContact.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
			
//			mButtonContentByProduct.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					getInstaFeeds();
//				}
//			});
			
//			mButtonContentByInfo.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					getStoreInfo();
//				}
//			});

			getInstaFeeds();
			
		}
		
		
		private void getInstaFeeds(){
			
//			mLayoutstoreContentByProduct.setVisibility(View.VISIBLE);
//			mLayoutstoreContentByProduct.setEnabled(true);
//			
//			mLayoutstoreContentByInfo.setVisibility(View.INVISIBLE);
//			mLayoutstoreContentByInfo.setEnabled(false);
			
			mIgWrapper.getUserFeeds(storeId, mFeedCount, mIgNxtPageUrl,
					mUserFeedsListener);
		}
		
		private void getStoreInfo(){
			
			mLayoutstoreContentByProduct.setVisibility(View.INVISIBLE);
			mLayoutstoreContentByProduct.setEnabled(false);
			
			mLayoutstoreContentByInfo.setVisibility(View.VISIBLE);
			mLayoutstoreContentByInfo.setEnabled(true);
		}
		
		/**
		 * User Feeds success or failure listener.
		 */
		private final IFetchIgFeedsListener mUserFeedsListener = new IFetchIgFeedsListener() {
					
			@Override
			public void onIgFeedsFetched(ArrayList<IgFeedHolder> feedList,
					String nxtPgUrl) {
				
				if (null != feedList) {
					Log.d(InstagramConstants.TAG,
							"Feeds fetched, size :" + feedList.size());
					Log.d(InstagramConstants.TAG, "Nxt Pg url :" + nxtPgUrl);

					mIgNxtPageUrl = nxtPgUrl;
					setIgFeedsAdapter(feedList);
				}
			}		
		};
		
		private void setIgFeedsAdapter(ArrayList<IgFeedHolder> feedList) {

			// List of all feeds to use in this activity.
			if (null == mIgFeedList){
				mIgFeedList = feedList;
			}else{
				mIgFeedList.addAll(feedList);
			}

			if (null == mFeedAdapter) {

				mFeedAdapter = new IgFeedAdapterGrid(this, feedList);
				mGridVwFeeds.setAdapter(mFeedAdapter);
				mGridVwFeeds.setOnScrollListener(new EndlessScrollListener());
				mGridVwFeeds.setEmptyView(mEmptyView);
			} else {
				// update lists(pagination)
				mFeedAdapter.updateFeedList(feedList);
				mFeedAdapter.notifyDataSetChanged();
			}
		}
		
		/**
	     * Endless scroll listener for Ig feeds.
	     * @author ritesh
	     *
	     */
		private class EndlessScrollListener implements OnScrollListener {

			// True if we are still waiting for the last set of data to load.
			private boolean loading = true;
			// The total number of items in the dataset after the last load
			private int previousTotal = 0;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				// first we check if we are waiting for the previous load to
				// finish
				if (loading) {
					// If it’s still loading, we check to see if the dataset
					// count has changed, if so we conclude it has finished
					// loading and update the and total item count.

					if (totalItemCount > previousTotal) {
						loading = false;
						previousTotal = totalItemCount;
					}
				}

				if (!loading
						&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + 12)) {
				
					if (null != mIgNxtPageUrl) {
						getInstaFeeds();
						loading = true;
					}
				}

			}

			public void resetLoading() {
				loading = false;
			}
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
