package com.cryingonion.instashop;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.cryingonion.instashop.instagram.listener.IFetchIgFollowsListener;
import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgFollowsAdapter;
import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;

public class ListFollowsActivity extends FragmentActivity{
	
	private ListView mLstVwFollows;
	private View mEmptyView;
	
	private ArrayList<IgFollowsHolder> mIgFollowsList;
	private IgFollowsAdapter mFollowsAdapter;	
	
	private String mIgUserId; 
    private String mIgNxtPageUrl = null;
    private int mFollowsCount = 20; // default Follows count 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instagram_follows);
		init();
	}
	
	private void init(){
		
		mLstVwFollows = (ListView) findViewById(R.id.lst_ig_follows);
		mEmptyView = findViewById(R.id.txt_empty_view);			
	}
	
	@Override
    public void onResume() {
        super.onResume();
        
		if ((mFollowsAdapter == null || mFollowsAdapter.getCount() <= 0))
			getInstaFollows();     
    }
	
	private void getInstaFollows(){
		
		mIgUserId = getIntent().getStringExtra(InstagramActivity.KEY_USER_ID);
		InstagramWrapper wrapper = new InstagramWrapper(this);
		wrapper.getUserFollows(mIgUserId, mFollowsCount, mIgNxtPageUrl,
				mUserFollowsListener);
	}
	
	/**
	 * User follows success or failure listener.
	 */
	private final IFetchIgFollowsListener mUserFollowsListener = new IFetchIgFollowsListener() {
				
		@Override
		public void onIgFollowsFetched(ArrayList<IgFollowsHolder> FollowsList,
				String nxtPgUrl) {
			
			if (null != FollowsList) {
				Log.d(InstagramConstants.TAG,
						"Follows fetched, size :" + FollowsList.size());
				Log.d(InstagramConstants.TAG, "Nxt Pg url :" + nxtPgUrl);

				mIgNxtPageUrl = nxtPgUrl;
				setIgFollowsAdapter(FollowsList);
			}
		}		
	};
	
	private void setIgFollowsAdapter(ArrayList<IgFollowsHolder> FollowsList) {

		// List of all Follows to use in this activity.
		if (null == mIgFollowsList){
			mIgFollowsList = FollowsList;
		}else{
			mIgFollowsList.addAll(FollowsList);
		}

		if (null == mFollowsAdapter) {

			mFollowsAdapter = new IgFollowsAdapter(this, FollowsList);
			mLstVwFollows.setAdapter(mFollowsAdapter);
			mLstVwFollows.setOnScrollListener(new EndlessScrollListener());
			mLstVwFollows.setEmptyView(mEmptyView);
		} else {
			// update lists(pagination)
			mFollowsAdapter.updateFollowsList(FollowsList);
			mFollowsAdapter.notifyDataSetChanged();
		}
	}
	
	/**
     * Endless scroll listener for Ig follows.
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
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2)) {

				if (null != mIgNxtPageUrl) {
					getInstaFollows();
					loading = true;
				}
			}

		}

		public void resetLoading() {
			loading = false;
		}
	}
	
}
