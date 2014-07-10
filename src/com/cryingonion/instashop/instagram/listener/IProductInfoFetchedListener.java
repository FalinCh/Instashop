package com.cryingonion.instashop.instagram.listener;

import com.cryingonion.instashop.instagram.holder.IgFeedHolder;

public interface IProductInfoFetchedListener {

	public void onIgProductInfoFetched(IgFeedHolder feedHolder);

	public void onIgProductInfoFetchingFailed();
	
}
