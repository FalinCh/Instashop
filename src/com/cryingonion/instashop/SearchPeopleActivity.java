package com.cryingonion.instashop;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.cryingonion.instashop.instagram.listener.IFetchIgFollowsListener;
import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgFollowsAdapter;
import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;

public class SearchPeopleActivity extends Fragment{
	
	private ListView mLstVwFollows;
	private View mEmptyView;
	private EditText mEdtTxtKeyword;
	private Button mBtnSearch;
	
	
	private ArrayList<IgFollowsHolder> mIgFollowsList;
	private IgFollowsAdapter mFollowsAdapter;	
	
	private String textKeyword = "";
	private String mIgUserId; 
    private String mIgNxtPageUrl = null;
    private int mFollowsCount = 20; // default Follows count 
	
    private InstagramWrapper mIgWrapper;
    
    protected Context context;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.activity_instagram_searchpeople, container, false);
        
        context = rootView.getContext();
        
        mLstVwFollows = (ListView) rootView.findViewById(R.id.lst_ig_follows);
        
        mIgWrapper = new InstagramWrapper(context);
		
		mBtnSearch = (Button) rootView.findViewById(R.id.btnSearch);
		mBtnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mLstVwFollows.invalidateViews();
				mIgNxtPageUrl = null;
				
				textKeyword = mEdtTxtKeyword.getText().toString().trim();
				
				if (textKeyword.length() != 0) {

					// search keyword.
					mIgWrapper.searchPeople(textKeyword, mFollowsCount, mIgNxtPageUrl,
							mUserFollowsListener);

				}
			}
		});
		
		
		
		mEdtTxtKeyword = (EditText) rootView.findViewById(R.id.edtKeyword);
		mEdtTxtKeyword.addTextChangedListener(textWatcherComment);
		
		mLstVwFollows = (ListView) rootView.findViewById(R.id.lst_ig_follows);
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
				
				mIgFollowsList.clear();
				
				mIgWrapper = new InstagramWrapper(context);
				
				
			} else {
				mBtnSearch.setEnabled(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};
	
	
	
	private void getInstaFollows(){
		
		textKeyword = mEdtTxtKeyword.getText().toString();
		InstagramWrapper wrapper = new InstagramWrapper(context);
		wrapper.searchPeople(mIgUserId, mFollowsCount, mIgNxtPageUrl,
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

			mFollowsAdapter = new IgFollowsAdapter(context, FollowsList);
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
