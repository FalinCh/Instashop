package com.cryingonion.instashop;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.app.Fragment;

public class SearchActivity extends Activity {
	
	ActionBar.Tab searchProductCategory, searchProductTag, searchStore;
	
	Fragment fragmentSearchProductByCategory = new SearchProductByCategoryActivity();
	Fragment fragmentSearchProductByTag = new SearchProductByTagActivity();
	Fragment fragmentSearchStore = new SearchPeopleActivity();
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        searchProductCategory = actionBar.newTab().setText("Category");
        searchProductTag = actionBar.newTab().setText("Tag");
        searchStore = actionBar.newTab().setText("Store");
        
        searchProductCategory.setTabListener(new MyTabListener(fragmentSearchProductByCategory));
        searchProductTag.setTabListener(new MyTabListener(fragmentSearchProductByTag));
        searchStore.setTabListener(new MyTabListener(fragmentSearchStore));
        
        actionBar.addTab(searchProductCategory);
        actionBar.addTab(searchProductTag);
        actionBar.addTab(searchStore);
    }
    
    public class MyTabListener implements ActionBar.TabListener {
    	Fragment fragment;
    	
    	public MyTabListener(Fragment fragment) {
    		this.fragment = fragment;
    	}
    	
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
    		ft.replace(R.id.fragment_container, fragment);
    	}
    	
    	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    		ft.remove(fragment);
    	}
    	
    	public void onTabReselected(Tab tab, FragmentTransaction ft) {
    		
    	}
    }
}
