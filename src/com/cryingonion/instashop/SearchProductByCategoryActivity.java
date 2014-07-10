package com.cryingonion.instashop;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgFeedAdapterGrid;
import com.cryingonion.instashop.instagram.holder.IgFeedHolder;
import com.cryingonion.instashop.instagram.listener.IFetchIgFeedsListener;
import com.cryingonion.instashop.model.CategoryDetail;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchProductByCategoryActivity extends Fragment {

	private ArrayList<String> listCategory1;
	
	private ArrayList<CategoryDetail> listCategory2;
	private ArrayList<String> listCategory2Title;
	
	private ListView mListCategory1;
	private ListView mListCategory2;
	
	private LinearLayout mBoxSearchByCategory;
	private Button mTextTempSearchTag;
	
	private LinearLayout mBoxSearchResult;
	private Button mTextSearchTag;
	private GridView mGridVwFeeds;
	private View mEmptyView;
	
	private String tempSelectedCategory1;
	private String tempSelectedCategory2Tag;
	
	private ArrayList<IgFeedHolder> mIgFeedList;
	private IgFeedAdapterGrid mFeedAdapter;	
	
	private InstagramWrapper mIgWrapper;
	private String mIgNxtPageUrl = null;
    private int mFeedCount = 100; // default feed count 
	
    protected Context context;
    View rootView;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
    	rootView = inflater.inflate(R.layout.activity_instagram_searchproduct_bycategory, container, false);
        
        context = rootView.getContext();
        
        mListCategory1 = (ListView) rootView.findViewById(R.id.lst_category1);
        mListCategory2 = (ListView) rootView.findViewById(R.id.lst_category2);
        
        mBoxSearchByCategory = (LinearLayout) rootView.findViewById(R.id.boxSearchByCategory);
        mBoxSearchByCategory.setVisibility(View.VISIBLE);
        mBoxSearchByCategory.setEnabled(true);
        
        mTextTempSearchTag = (Button) rootView.findViewById(R.id.textTempSearchTag);
        
        mBoxSearchResult = (LinearLayout) rootView.findViewById(R.id.boxSearchResult);
        mBoxSearchResult.setVisibility(View.INVISIBLE);
        mBoxSearchResult.setEnabled(false);
        
        mTextSearchTag = (Button)rootView.findViewById(R.id.textSearchTag);
        mGridVwFeeds = (GridView) rootView.findViewById(R.id.grid_ig_feeds);
        mEmptyView = rootView.findViewById(R.id.txt_empty_view);	

        generateCategory1();
        
        return rootView;
	}
	
    
	public void generateCategory1()
	{
		listCategory1 = new ArrayList<String>();
		listCategory1.add("Women");
		listCategory1.add("Men");
		listCategory1.add("Kids");
		listCategory1.add("Technology");
		listCategory1.add("Foods");
		listCategory2 = new ArrayList<CategoryDetail>();
		
		ArrayAdapter<String> arrayAdapter =      
                new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCategory1);
		
		mListCategory1.setAdapter(arrayAdapter);
		
		// register onClickListener to handle click events on each item
		mListCategory1.setOnItemClickListener(new OnItemClickListener()
		{
	       // argument position gives the index of item which is clicked
	       public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
	       {
	    	   String selectedCategory=listCategory1.get(position);
	    	   mTextTempSearchTag.setText(selectedCategory);
	    	   tempSelectedCategory1 = selectedCategory;
	    	   
	    	   if(position==0)
	    	   {
	    		   generateCategoryForWomen();
	    	   }
	    	   else if(position==1)
	    	   {
	    		   generateCategoryForMen();
	    	   }
	    	   else if(position==2)
	    	   {
	    		   generateCategoryForKids();
	    	   }
	    	   else if(position==3)
	    	   {
	    		   generateCategoryForTechnology();
	    	   }
	    	   else if(position==4)
	    	   {
	    		   generateCategoryForFood();
	    	   }
	       	}
		});
		
	}
	
	public void generateListCategory2(ArrayList<String> listCategory2Titles)
	{
		ArrayAdapter<String> arrayAdapter =      
                new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCategory2Titles);
		
		mListCategory2.setAdapter(arrayAdapter);
		
		// register onClickListener to handle click events on each item
		mListCategory2.setOnItemClickListener(new OnItemClickListener()
		{
	        // argument position gives the index of item which is clicked
			public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
			{
				String selectedCategory=listCategory2.get(position).getCategoryName();
				
				String selectedCategoryTag=listCategory2.get(position).getCategoryTag();
	    	    String selectedCategoryPath=listCategory2.get(position).getCategoryPath();
	    	    
	    	    tempSelectedCategory2Tag = selectedCategoryTag;
				
	    	    
	    	    mTextTempSearchTag.setText(tempSelectedCategory1 + " > " + selectedCategory);
	    	    mTextSearchTag.setText(tempSelectedCategory1 + " > " + selectedCategory);
	    	    
	    	    mBoxSearchByCategory.setVisibility(View.INVISIBLE);
	    	    mBoxSearchByCategory.setEnabled(false);
	            mBoxSearchResult.setVisibility(View.VISIBLE);
	            mBoxSearchResult.setEnabled(true);
	            
	            mIgWrapper = new InstagramWrapper(context);
	            
	            mIgWrapper.searchTag(selectedCategoryTag, mFeedCount, mIgNxtPageUrl, mUserFeedsListener);
	       	}
		});
	}
	
	private void getInstaFeeds(){
		
		InstagramWrapper wrapper = new InstagramWrapper(context);
		wrapper.searchTag(tempSelectedCategory2Tag, mFeedCount, mIgNxtPageUrl,
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
	
	public void generateCategoryForWomen()
	{
		listCategory2Title = new ArrayList<String>();
		listCategory2Title.add("All");
		listCategory2Title.add("Dress");
		listCategory2Title.add("Blazer");
		listCategory2Title.add("Shoes");
		listCategory2Title.add("Skirts");
		listCategory2Title.add("Handbags");
		listCategory2Title.add("Accessories");
		listCategory2Title.add("Beauty");
		
		listCategory2 = new ArrayList<CategoryDetail>();
		listCategory2.add(new CategoryDetail ("All", "womencloth", "Women > All"));
		listCategory2.add(new CategoryDetail ("Dress", "dress", "Women > Dress"));
		listCategory2.add(new CategoryDetail ("Blazer","blazer","Women > Blazer"));
		listCategory2.add(new CategoryDetail ("Shoes","heels","Women > Shoes"));
		listCategory2.add(new CategoryDetail ("Skirts","skirt","Women > Skirts"));
		listCategory2.add(new CategoryDetail ("Handbags","clutch","Women > Handbags"));
		listCategory2.add(new CategoryDetail ("Accessories","jewelry","Women > Accessories"));
		listCategory2.add(new CategoryDetail ("Beauty","cosmetic","Women > Beauty"));
		
		generateListCategory2(listCategory2Title);
	}
	
	public void generateCategoryForMen()
	{
		listCategory2Title = new ArrayList<String>();
		listCategory2Title.add("All");
		listCategory2Title.add("Shirts");
		listCategory2Title.add("T-Shirt");
		listCategory2Title.add("Shoes");
		listCategory2Title.add("Tie");
		listCategory2Title.add("Accessories");	
		
		listCategory2 = new ArrayList<CategoryDetail>();
		listCategory2.add(new CategoryDetail ("All", "mencloth", "Men > All"));
		listCategory2.add(new CategoryDetail ("Shirts", "shirt", "Men > Shirts"));
		listCategory2.add(new CategoryDetail ("T-Shirt","tees","Men > T-Shirt"));
		listCategory2.add(new CategoryDetail ("Shoes","pantovel","Men > Shoes"));
		listCategory2.add(new CategoryDetail ("Tie","tie","Men > Tie"));
		listCategory2.add(new CategoryDetail ("Accessories","watch","Men > Accessories"));
		
		generateListCategory2(listCategory2Title);
	}
	
	public void generateCategoryForKids()
	{
		listCategory2Title = new ArrayList<String>();
		listCategory2Title.add("All");
		listCategory2Title.add("Shirts");
		listCategory2Title.add("T-Shirt");
		listCategory2Title.add("Shoes");
		listCategory2Title.add("Accessories");	
		
		listCategory2 = new ArrayList<CategoryDetail>();
		listCategory2.add(new CategoryDetail ("All", "kidswear", "Kids > All"));
		listCategory2.add(new CategoryDetail ("Shirts", "kidshirt", "Kids > Shirts"));
		listCategory2.add(new CategoryDetail ("T-Shirt","kidtshirt","Kids > T-Shirt"));
		listCategory2.add(new CategoryDetail ("Shoes","kidshoes","Kids > Shoes"));
		listCategory2.add(new CategoryDetail ("Accessories","kidhat","Kids > Accessories"));
		
		generateListCategory2(listCategory2Title);
	}
	
	public void generateCategoryForTechnology()
	{
		listCategory2Title = new ArrayList<String>();
		listCategory2Title.add("All");
		listCategory2Title.add("Phones");
		listCategory2Title.add("Laptops");
		listCategory2Title.add("Tablets");
		listCategory2Title.add("Consoles");	
		listCategory2Title.add("Case");
		listCategory2Title.add("Accessories");
		
		listCategory2 = new ArrayList<CategoryDetail>();
		listCategory2.add(new CategoryDetail ("All", "technology", "Technology > All"));
		listCategory2.add(new CategoryDetail ("Phones", "phonesale", "Technology > Phones"));
		listCategory2.add(new CategoryDetail ("Laptops","computer","Technology > Laptops"));
		listCategory2.add(new CategoryDetail ("Tablets","tablets","Technology > Tablets"));
		listCategory2.add(new CategoryDetail ("Consoles","console","Technology > Consoles"));	
		listCategory2.add(new CategoryDetail ("Case", "case", "Technology > Case"));
		listCategory2.add(new CategoryDetail ("Accessories","pluggy","Technology > Accessories"));
		
		generateListCategory2(listCategory2Title);
	}
	
	public void generateCategoryForFood()
	{
		listCategory2Title = new ArrayList<String>();
		listCategory2Title.add("All");
		listCategory2Title.add("Snack");
		listCategory2Title.add("Japanese Snack");
		listCategory2Title.add("Dessert");
		listCategory2Title.add("Delivery");	
		listCategory2Title.add("Fast Food");
		
		listCategory2 = new ArrayList<CategoryDetail>();
		listCategory2.add(new CategoryDetail ("All", "restaurant", "Food > All"));
		listCategory2.add(new CategoryDetail ("Snack", "snack", "Food > Snack"));
		listCategory2.add(new CategoryDetail ("Japanese Snack","japanesesnack","Food > Japanese Snack"));
		listCategory2.add(new CategoryDetail ("Dessert","dessert","Food > Dessert"));
		listCategory2.add(new CategoryDetail ("Delivery","delivery","Food > Delivery"));	
		listCategory2.add(new CategoryDetail ("Fast Food", "fast food", "Food > Fast Foods"));
		
		generateListCategory2(listCategory2Title);
	}

}
