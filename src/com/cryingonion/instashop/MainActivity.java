package com.cryingonion.instashop;

import com.cryingonion.instashop.adapter.NavDrawerListAdapter;
import com.cryingonion.instashop.model.NavDrawerItem;
import com.cryingonion.instashop.instagram.InstagramActivity;
import com.cryingonion.instashop.instagram.InstagramManager;
import com.cryingonion.instashop.instagram.InstagramWrapper;

import java.util.ArrayList;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
	public static final String TAG = MainActivity.class.getSimpleName();
	
	public String PARSE_APPLICATION_ID = "y6RzPzQ7v2Pz9HimuPoZA3jNyc3N4zQHapASaIf7";
	public String PARSE_CLIENT_KEY = "bTpn9m8Ggu7z21dHaU6CRbmhF3oLxB0y0OvcmEIS";
	
	public SharedPreferences pref;
	protected Context context;
	public Editor editor;
	
	// Navigation Drawer
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	// Instagram
	private String userId;
	private InstagramWrapper mIgWrapper;
	private InstagramManager mIgManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pref = getSharedPreferences(getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		ParseAnalytics.trackAppOpened(getIntent());
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		mIgWrapper = new InstagramWrapper(this);
		
		if (!mIgWrapper.isLoggedIn() && currentUser == null) {
			navigateToLogin();
		}
		else
		{
			//navigateToMainPage();
			Log.i(TAG, "Success Login");
		}
		
		navigationDrawerHandler(savedInstanceState);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void navigationDrawerHandler(Bundle savedInstanceState) {
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array

		// Timeline
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Discover
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Recommnedation
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Wishlist
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Profile
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// Setting
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadPref() {
		SharedPreferences mySharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 **/
	private void displayView(int position) {
		// update the main content by replacing fragments
		
		Fragment fragment = null;
		
		switch (position) {
		case 0:
			fragment = new TimelineFragment();
			break;
		case 1:
			fragment = new DiscoverFragment();
			break;
		case 2:
			fragment = new RecommendationFragment();
			break;
		case 3:
			fragment = new WishlistFragment();
			break;
		case 4:
			fragment = new ProfileFragment();
			break;
		case 5:
			fragment = new SettingFragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	public void signUpToParse ()
	{
		ParseUser user = new ParseUser();
		user.setUsername("my name");
		user.setPassword("my pass");
		user.setEmail("email@example.com");
		  
		// other fields can be set just like with ParseObject
		user.put("phone", "650-555-0000");
		  
		user.signUpInBackground(new SignUpCallback() {
		  public void done(ParseException e) {
		    if (e == null) {
		      // Hooray! Let them use the app now.
		    } else {
		      // Sign up didn't succeed. Look at the ParseException
		      // to figure out what went wrong
		    }
		  }
		});
	}
	
	public void navigateToLogin ()
	{
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
	
	public void navigateToMainPage()
	{
		mIgManager = new InstagramManager(this);
		userId = mIgManager.getUserId();
		
		//Intent intent = new Intent(this, TimelineActivity.class);
		//Intent intent = new Intent(this, ListFollowsActivity.class);
		//Intent intent = new Intent(this, SearchPeopleActivity.class);
		//Intent intent = new Intent(this, ListProductActivity.class);
		
		Intent intent = new Intent(MainActivity.this, TimelineFragment.class);
		intent.putExtra(InstagramActivity.KEY_USER_ID, userId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

}
