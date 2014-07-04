package com.cryingonion.instashop.instagram.listener;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.holder.IgFeedHolder;

/**
 * Listener for Instagram feeds.
 * 
 * @author falinch
 *
 */
public interface IFetchIgFeedsListener {
	public void onIgFeedsFetched(ArrayList<IgFeedHolder> feedList,
		    String nxtPgUrl);
}