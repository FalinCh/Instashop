package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgCmntAdapter;
import com.cryingonion.instashop.instagram.holder.IgCommentHolder;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;
import com.cryingonion.instashop.instagram.listener.IFetchIgFollowsListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.listener.IProductInfoFetchedListener;
import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;
import com.parse.ParseObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailsActivity extends Activity {

	// profile image.
	private ImageView mImgVwProfile;
	// User Name
	private TextView mTxtVwName;
	// Feed text
	private TextView mTxtVwFeedTxt;
	// Created at
	private TextView mTxtVwLog;
	private TextView mTxtVwLikeCount;
	private Button mButtonLike;
	private Button mButtonWishlist;
	private ImageView mImgVwFeed;
	private ImageView mImgVwPlay;
	private LinearLayout mImgVwStoreButton;
	
	private ArrayList<IgCommentHolder> mCmntList;
	@SuppressLint("ValidFragment")
	private IgCmntAdapter mCmntAdapter;
	
	private InstagramWrapper mIgWrapper;
	
	private String mediaId;
	private String ownerId;
	private int likeCnt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_details);
		
		mIgWrapper = new InstagramWrapper(this);
		
		// Get the video info from Extras
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("selectedMediaId")) {
				mediaId = bundle.getString("selectedMediaId");
				
				// search keyword.
				mIgWrapper.getProductInfo(mediaId, mProductFollowsListener);
				
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
		mTxtVwLog = (TextView)
				findViewById(R.id.txt_ig_created_at);
		mTxtVwFeedTxt = (TextView)
				findViewById(R.id.txt_ig_caption);
		mTxtVwLikeCount = (TextView)
				findViewById(R.id.txt_ig_like_count);
		mButtonLike = (Button)
				findViewById(R.id.buttonLike);
		mImgVwFeed = (ImageView)
				findViewById(R.id.imgvw_ig_media);
		mImgVwPlay = (ImageView)
				findViewById(R.id.imgvw_play);
		mImgVwStoreButton = (LinearLayout)
				findViewById(R.id.imgvw_storeButton);
		mImgVwStoreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				navigateToStore();
			}
		});
		mButtonWishlist = (Button)
				findViewById(R.id.buttonAddToWishlist);
		mButtonWishlist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addToWishList();
			}
		});
		
		return true;
	}
	
	public void setProductDetails(IgFeedHolder feedHolder)
	{
		
		// Set the profile image
		if (AppUtils.isUrlImage(feedHolder.getmUserInfo().getmUserProfPicUrl())) {

			ImageDownloader.setSquareImg(this, mImgVwProfile,
					feedHolder.getmUserInfo().getmUserProfPicUrl(),
					R.drawable.icon,false,true);	    
		}

		// User Name
		mTxtVwName.setText(feedHolder.getmUserInfo().getmUserName());

		ownerId = feedHolder.getmUserInfo().getmUserId();
		
//		// Created time
//		long longTime = Long.parseLong(feedHolder.getmCreatedTime());
//		/**
//		 * Instagram returns unix time stamp for the feed's created time, which
//		 * is in seconds. Hence multiplying by 1000 to convert it to
//		 * milliseconds.
//		 */
//		longTime = longTime * 1000;
//		mTxtVwLog.setText(DateUtils.getRelativeTimeSpanString(longTime,
//			System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));		

		// Feed image
		feedHolder.getmThmbnlUrl();
		// Feed image
		if (AppUtils.isUrlImage(feedHolder.getmStdResImgUrl())) {

		    // to display full in width.
		    ImageDownloader.setSquareImg(mImgVwFeed,
			    feedHolder.getmStdResImgUrl(),
			    R.drawable.default_image_square, true, true);

			if (feedHolder.getmFeedType() != null
					&& feedHolder.getmFeedType().equalsIgnoreCase(
							InstagramConstants.IG_MEDIA_VIDEO)) {
				mImgVwPlay.setVisibility(View.VISIBLE);
			} else {
				mImgVwPlay.setVisibility(View.GONE);
			}
			
		} else {
		    mImgVwFeed.setImageResource(android.R.color.transparent);
		}

		// Caption
		if(!feedHolder.getmCaptionText().equals(null))
			mTxtVwFeedTxt.setText(feedHolder.getmCaptionText());		

		// Like count
		likeCnt = Integer.parseInt(feedHolder.getmLikesCount());
		mTxtVwLikeCount.setText(likeCnt + " Likes");
		
		mButtonLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIgWrapper.postLikeOnMedia(InstagramConstants.IG_REQ_POST_LIKE, mediaId, mPostLikeCmntListener);
				likeCnt += 1;
				mTxtVwLikeCount.setText(likeCnt + " Likes");
			}
		});

	}
	
	private void navigateToStore()
	{
		Intent intent = null;
		Bundle args = new Bundle();

		args.putString("selectedStoreId", ownerId);

		intent = new Intent(this, StoreDetailsActivity.class);
		intent.putExtras(args);
		startActivity(intent);
	}
	
	private void addToWishList()
	{
		Toast toast = Toast.makeText(this, "Wishlist Added", Toast.LENGTH_SHORT);
		toast.show();
		
		ParseObject user = new ParseObject("Wishlist");
		user.put("username",mIgWrapper.getUserId());
		user.put("productId", mediaId);
		user.put("productOwnerId", ownerId);
		  
		user.saveInBackground();
	}
	
	/**
	 * User follows success or failure listener.
	 */
	private final IProductInfoFetchedListener mProductFollowsListener = new IProductInfoFetchedListener() {
				
		@Override
		public void onIgProductInfoFetched(IgFeedHolder feedHolder) {
			
			if (null != feedHolder) {
				
				setProductDetails(feedHolder);
				
			}
		}	
		
		public void onIgProductInfoFetchingFailed(){}
	};
	
	private ILikeCmntListener mPostLikeCmntListener = new ILikeCmntListener() {

		@Override
		public void onCopmplete(int reqType, int responseCode) {

			if (responseCode == 200) {
				Log.d(InstagramConstants.TAG, "Post like/cmnt Success.");
				if (reqType == InstagramConstants.IG_REQ_POST_LIKE) {
					Log.d(InstagramConstants.TAG, " Like done succesfully.");
					
				} else {
					Log.d(InstagramConstants.TAG, " Comment posted succesfully.");
				}
			} else {
				Log.d(InstagramConstants.TAG, "Post like/cmnt failed.");
			}

		}
	};
	


}
