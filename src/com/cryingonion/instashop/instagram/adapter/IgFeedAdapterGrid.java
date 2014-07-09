package com.cryingonion.instashop.instagram.adapter;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryingonion.instashop.instagram.listener.IAuthenticationListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramVideoDetailActivity;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.R;
import com.cryingonion.instashop.instagram.InstagramCmntDialogFrag;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;

public class IgFeedAdapterGrid extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<IgFeedHolder> mIgFeedList;

	public IgFeedAdapterGrid(Context context) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}
	
	public IgFeedAdapterGrid(Context context,ArrayList<IgFeedHolder> igFeedList) {
		this(context);
		mIgFeedList = igFeedList;
	}

	public void setDataSource(ArrayList<IgFeedHolder> igFeedList) {
		mIgFeedList = igFeedList;
	}
	
	public void updateFeedList(ArrayList<IgFeedHolder> igFeedList) {
		if (mIgFeedList != null) {
			mIgFeedList.addAll(igFeedList);
		} else {
			mIgFeedList = igFeedList;
		}
	}

	static class ViewHolder {
		
		ImageView mImgVwFeed;
		ImageView mImgVwPlay;
	}

	@Override
	public int getCount() {
		if (mIgFeedList == null)
			return 0;
		else
			return mIgFeedList.size();
	}

	@Override
	public Object getItem(int position) {
		return mIgFeedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.griditem_instagram_feed, null);

		
		holder.mImgVwFeed = (ImageView) convertView
				.findViewById(R.id.imgvw_ig_media);
		holder.mImgVwPlay = (ImageView) convertView
				.findViewById(R.id.imgvw_play);

		convertView.setTag(holder);
		holder = (ViewHolder) convertView.getTag();
		
		final IgFeedHolder feedHolder = mIgFeedList.get(position);
		
		
		// Feed image
		feedHolder.getmThmbnlUrl();
		// Feed image
		if (AppUtils.isUrlImage(feedHolder.getmStdResImgUrl())) {

		    // to display full in width.
		    ImageDownloader.setSquareImg(holder.mImgVwFeed,
			    feedHolder.getmStdResImgUrl(),
			    R.drawable.default_image_square, true, true);
		    
			if (feedHolder.getmFeedType() != null
					&& feedHolder.getmFeedType().equalsIgnoreCase(
							InstagramConstants.IG_MEDIA_VIDEO)) {
				holder.mImgVwPlay.setVisibility(View.VISIBLE);
			} else {
				holder.mImgVwPlay.setVisibility(View.GONE);
			}
		    
		    myMediaListener(holder, holder.mImgVwFeed, position);
		} else {
		    holder.mImgVwFeed.setImageResource(android.R.color.transparent);
		}
		
		
		
		holder.mImgVwFeed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				InstagramWrapper wrapper = new InstagramWrapper(mContext);

				if (wrapper.isLoggedIn()) {
//					InstagramCmntDialogFrag cmntDlgFrag = new InstagramCmntDialogFrag(
//							mContext, feedHolder.getmMediaId());
//					cmntDlgFrag.show(((FragmentActivity) mContext)
//							.getSupportFragmentManager(), "IgCmntDialog");
				} else {

					// To login instagram
					Intent intent = new Intent(mContext, InstagramActivity.class);
					intent.putExtra(InstagramConstants.IG_REQUEST,
							InstagramConstants.IG_REQ_LOGIN);

					InstagramActivity.setIgAuthListener(new IAuthenticationListener() {

						@Override
						public void onAuthSuccess() {
//							InstagramCmntDialogFrag cmntDlgFrag = new InstagramCmntDialogFrag(
//									mContext, feedHolder.getmMediaId());
//
//							FragmentTransaction transaction = ((FragmentActivity) mContext)
//									.getSupportFragmentManager()
//									.beginTransaction();
//							transaction.add(cmntDlgFrag, "IgCmntDialog");
//							transaction.commitAllowingStateLoss();
						}

						@Override
						public void onAuthFail(String error) {

						}
					});

					mContext.startActivity(intent);
				}				
			}
		});
		
		return convertView;
	}
	
	public void myMediaListener(final ViewHolder phHolder, ImageView imgView,
			final int position) {
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				IgFeedHolder feedHolder = mIgFeedList.get(position);

				Intent intent = null;
				Bundle args = new Bundle();

				if (feedHolder.getmFeedType() != null
						&& feedHolder.getmFeedType().equalsIgnoreCase(
								InstagramConstants.IG_MEDIA_VIDEO)) {

					args.putString("video_url", feedHolder.getmVidStdResUrl());

					intent = new Intent(mContext, InstagramVideoDetailActivity.class);

					intent.putExtras(args);
					mContext.startActivity(intent);
				}

			}
		});
	}
	
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
