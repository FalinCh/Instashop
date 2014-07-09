package com.cryingonion.instashop;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RecommendationProductFragment extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_recommendation_product);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recommendation_product, menu);
		return true;
	}

}
