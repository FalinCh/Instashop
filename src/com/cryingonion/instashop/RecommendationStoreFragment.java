package com.cryingonion.instashop;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RecommendationStoreFragment extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_recommendation_store);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recommendation_store, menu);
		return true;
	}

}
