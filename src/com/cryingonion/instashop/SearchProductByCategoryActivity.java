package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgFeedAdapterGrid;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AbsListView.OnScrollListener;

public class SearchProductByCategoryActivity extends Activity {

	private GridView mGridVwFeeds;
	private View mEmptyView;
	private EditText mEdtTxtKeyword;
	private Button mBtnSearch;
	
	private String textKeyword = "";
	
	private ArrayList<IgFeedHolder> mIgFeedList;
	private IgFeedAdapterGrid mFeedAdapter;	
	
	private InstagramManager mIgManager;
	private String mIgUserId; 
    private String mIgNxtPageUrl = null;
    private int mFeedCount = 100; // default feed count 
	
    private InstagramWrapper mIgWrapper;
    
    public int searchCount = 1;
    
    protected Context context;
    View rootView;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
    	rootView = inflater.inflate(R.layout.activity_instagram_searchproduct_bycategory, container, false);
        
        context = rootView.getContext();
        
        mGridVwFeeds = (GridView) rootView.findViewById(R.id.grid_ig_feeds);
        
        mIgWrapper = new InstagramWrapper(context);
        
        mBtnSearch = (Button) rootView.findViewById(R.id.btnSearch);
		mBtnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(searchCount > 1)
				{
					mIgFeedList.clear();
					mIgWrapper = new InstagramWrapper(context);
					
					mGridVwFeeds.invalidateViews();
					mIgNxtPageUrl = null;
				}
				
				searchCount++;
				
				textKeyword = mEdtTxtKeyword.getText().toString().trim();
				
				if (textKeyword.length() != 0) {

					// search keyword.
					mIgWrapper.searchTag(textKeyword, mFeedCount, mIgNxtPageUrl,
							mUserFeedsListener);

				}
			}
		});
		
		mEdtTxtKeyword = (EditText) rootView.findViewById(R.id.edtKeyword);
		//mEdtTxtKeyword.addTextChangedListener(textWatcherComment);
		
		mGridVwFeeds = (GridView) rootView.findViewById(R.id.grid_ig_feeds);
		mEmptyView = rootView.findViewById(R.id.txt_empty_view);	
		
		return rootView;
	}
	
	// To enable the Post button only when edit-text is non-empty
	TextWatcher textWatcherComment = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (TextUtils.isEmpty(s)) {
				mBtnSearch.setEnabled(false);
			} else {
				mBtnSearch.setEnabled(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
			if (!TextUtils.isEmpty(arg0)) {
				mBtnSearch.setEnabled(true);
			}
			else
			{
				mBtnSearch.setEnabled(false);
			}
			
		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};
	
	
	
	private void getInstaFeeds(){
		
		textKeyword = mEdtTxtKeyword.getText().toString();
		InstagramWrapper wrapper = new InstagramWrapper(context);
		wrapper.searchTag(textKeyword, mFeedCount, mIgNxtPageUrl,
				mUserFeedsListener);
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

			mFeedAdapter = new IgFeedAdapterGrid(context, feedList);
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

}
