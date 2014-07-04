package com.cryingonion.instashop.instagram.listener;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.holder.IgFollowsHolder;

/**
 * Listener for Instagram Followss.
 * 
 * @author falinch
 *
 */
public interface IFetchIgFollowsListener {
	public void onIgFollowsFetched(ArrayList<IgFollowsHolder> FollowsList,
		    String nxtPgUrl);
}