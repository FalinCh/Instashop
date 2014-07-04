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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cryingonion.instashop.instagram.listener.IAuthenticationListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramVideoDetailActivity;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.R;
import com.cryingonion.instashop.instagram.InstagramCmntDialogFrag;
import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;
import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;

public class IgFollowsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<IgFollowsHolder> mIgFollowsList;

	public IgFollowsAdapter(Context context) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}
	
	public IgFollowsAdapter(Context context,ArrayList<IgFollowsHolder> igFollowsList) {
		this(context);
		mIgFollowsList = igFollowsList;
	}

	public void setDataSource(ArrayList<IgFollowsHolder> igFollowsList) {
		mIgFollowsList = igFollowsList;
	}
	
	public void updateFollowsList(ArrayList<IgFollowsHolder> igFollowsList) {
		if (mIgFollowsList != null) {
			mIgFollowsList.addAll(igFollowsList);
		} else {
			mIgFollowsList = igFollowsList;
		}
	}

	public void resetFollowsList() {
		mIgFollowsList.clear();
	}
	
	static class ViewHolder {
		// profile image.
		ImageView mImgVwProfile;
		// Full Name
		TextView mTxtVwName;
		// User Name
		TextView mTxtVwUsername;
		// Box
		LinearLayout mLinearLayoutVwBoxFollowInfo;

	}

	@Override
	public int getCount() {
		if (mIgFollowsList == null)
			return 0;
		else
			return mIgFollowsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mIgFollowsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.listitem_instagram_follows, null);

		holder.mImgVwProfile = (ImageView) convertView
				.findViewById(R.id.imgvw_ig_profpic);
		holder.mTxtVwName = (TextView) convertView
				.findViewById(R.id.txt_ig_full_name);
		holder.mTxtVwUsername = (TextView) convertView
				.findViewById(R.id.txt_ig_username);
		holder.mLinearLayoutVwBoxFollowInfo = (LinearLayout) convertView
				.findViewById(R.id.box_followsinfo);


		convertView.setTag(holder);
		holder = (ViewHolder) convertView.getTag();
		
		final IgFollowsHolder FollowsHolder = mIgFollowsList.get(position);
		
		
		// Set the profile image
		if (AppUtils.isUrlImage(FollowsHolder.getmProfilePictureUrl())) {

			ImageDownloader.setSquareImg(mContext,holder.mImgVwProfile,
					FollowsHolder.getmProfilePictureUrl(),
					R.drawable.icon,false,true);	    
		}
		
		
		// Full Name
		holder.mTxtVwName.setText(FollowsHolder.getmFullName());
		
		// User Name
		holder.mTxtVwUsername.setText(FollowsHolder.getmUsername());
		
//		holder.mLinearLayoutVwBoxFollowInfo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				InstagramWrapper wrapper = new InstagramWrapper(mContext);
//
//				if (wrapper.isLoggedIn()) {
//					InstagramCmntDialogFrag cmntDlgFrag = new InstagramCmntDialogFrag(
//							mContext, FollowsHolder.getmMediaId());
//					cmntDlgFrag.show(((FragmentActivity) mContext)
//							.getSupportFragmentManager(), "IgCmntDialog");
//				} else {
//
//					// To login instagram
//					Intent intent = new Intent(mContext, InstagramActivity.class);
//					intent.putExtra(InstagramConstants.IG_REQUEST,
//							InstagramConstants.IG_REQ_LOGIN);
//
//					InstagramActivity.setIgAuthListener(new IAuthenticationListener() {
//
//						@Override
//						public void onAuthSuccess() {
//							InstagramCmntDialogFrag cmntDlgFrag = new InstagramCmntDialogFrag(
//									mContext, FollowsHolder.getmMediaId());
//
//							FragmentTransaction transaction = ((FragmentActivity) mContext)
//									.getSupportFragmentManager()
//									.beginTransaction();
//							transaction.add(cmntDlgFrag, "IgCmntDialog");
//							transaction.commitAllowingStateLoss();
//						}
//
//						@Override
//						public void onAuthFail(String error) {
//
//						}
//					});
//
//					mContext.startActivity(intent);
//				}				
//			}
//		});
//		
		return convertView;
	}
}
