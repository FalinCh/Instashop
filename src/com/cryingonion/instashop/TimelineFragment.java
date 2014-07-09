package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgFeedAdapter;
import com.cryingonion.instashop.instagram.adapter.IgFeedAdapterGrid;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class TimelineFragment extends Fragment {

	protected Context context;
	Bundle intentExtra;
	
	private GridView mGridVwFeeds;
	private View mEmptyView;
	
	private ArrayList<IgFeedHolder> mIgFeedList;
	private IgFeedAdapterGrid mFeedAdapter;	
	
	private InstagramManager mIgManager;
	private String mIgUserId; 
    private String mIgNxtPageUrl = null;
    private int mFeedCount = 100; // default feed count 
	
	public TimelineFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        
        context = rootView.getContext();
        
        mGridVwFeeds = (GridView) rootView.findViewById(R.id.grid_ig_feeds);
        
        return rootView;
    }
	
	@Override
    public void onResume() {
        super.onResume();
        
		if ((mFeedAdapter == null || mFeedAdapter.getCount() <= 0))
			getInstaFeeds();     
    }
	
	private void getInstaFeeds(){
		
		mIgManager = new InstagramManager(context);
		mIgUserId = mIgManager.getUserId();
		
		//mIgUserId = getIntent().getStringExtra(InstagramActivity.KEY_USER_ID);
//		InstagramWrapper wrapper = new InstagramWrapper(context);
//		wrapper.getUserFeeds(mIgUserId, mFeedCount, mIgNxtPageUrl,
//				mUserFeedsListener);
		
		String keyword = "shop";
		InstagramWrapper wrapper = new InstagramWrapper(context);
		wrapper.searchTag(keyword, mFeedCount, mIgNxtPageUrl,
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
