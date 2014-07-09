package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.adapter.IgCmntAdapter;
import com.cryingonion.instashop.instagram.holder.IgCommentHolder;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
	private TextView mTxtVwCmntCount;
	private ImageView mImgVwLike;
	private ImageView mImgVwCmnt;
	private ImageView mImgVwFeed;
	private ImageView mImgVwPlay;
	
	private ArrayList<IgCommentHolder> mCmntList;
	@SuppressLint("ValidFragment")
	private IgCmntAdapter mCmntAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_details);
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
		mTxtVwCmntCount = (TextView)
				findViewById(R.id.txt_ig_cmnt_count);
		mImgVwLike = (ImageView)
				findViewById(R.id.imgvw_ig_like);
		mImgVwCmnt = (ImageView)
				findViewById(R.id.imgvw_ig_cmnt);
		mImgVwFeed = (ImageView)
				findViewById(R.id.imgvw_ig_media);
		mImgVwPlay = (ImageView)
				findViewById(R.id.imgvw_play);
//
//		final IgFeedHolder feedHolder = mIgFeedList.get(position);
//		
//		// Set the profile image
//		if (AppUtils.isUrlImage(feedHolder.getmUserInfo().getmUserProfPicUrl())) {
//
//			ImageDownloader.setSquareImg(this, mImgVwProfile,
//					feedHolder.getmUserInfo().getmUserProfPicUrl(),
//					R.drawable.icon,false,true);	    
//		}
//
//		final IgFeedHolder feedHolder = mIgFeedList.get(position);
//		
//		// User Name
//		holder.mTxtVwName.setText(feedHolder.getmUserInfo().getmUserName());
//
//		// Created time
//		long longTime = Long.parseLong(feedHolder.getmCreatedTime());
//		/**
//		 * Instagram returns unix time stamp for the feed's created time, which
//		 * is in seconds. Hence multiplying by 1000 to convert it to
//		 * milliseconds.
//		 */
//		longTime = longTime * 1000;
//		holder.mTxtVwLog.setText(DateUtils.getRelativeTimeSpanString(longTime,
//			System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));		
//
//		// Feed image
//		feedHolder.getmThmbnlUrl();
//		// Feed image
//		if (AppUtils.isUrlImage(feedHolder.getmStdResImgUrl())) {
//
//		    // to display full in width.
//		    ImageDownloader.setSquareImg(holder.mImgVwFeed,
//			    feedHolder.getmStdResImgUrl(),
//			    R.drawable.default_image_square, true, true);
//
//			if (feedHolder.getmFeedType() != null
//					&& feedHolder.getmFeedType().equalsIgnoreCase(
//							InstagramConstants.IG_MEDIA_VIDEO)) {
//				holder.mImgVwPlay.setVisibility(View.VISIBLE);
//			} else {
//				holder.mImgVwPlay.setVisibility(View.GONE);
//			}
//
//		    myMediaListener(holder, holder.mImgVwFeed, position);
//		} else {
//		    holder.mImgVwFeed.setImageResource(android.R.color.transparent);
//		}
//
//		// Caption
//		holder.mTxtVwFeedTxt.setText(feedHolder.getmCaptionText());		
//
////		// Like count
////		int likeCnt = Integer.parseInt(feedHolder.getmLikesCount());
////		holder.mTxtVwLikeCount.setText(AppUtils
////				.getStringFormattedNumber(likeCnt)
////				+ AppUtils.getStringLikes(likeCnt) + ", ");
////
////		// Comment Count
////		int cmntCnt = Integer.parseInt(feedHolder.getmCmntCount());
////		holder.mTxtVwCmntCount.setText(AppUtils.getStringFormattedNumber(
////				cmntCnt).trim()
////				+ AppUtils.getStringComments(cmntCnt));
//
//		
		return true;
	}

}
