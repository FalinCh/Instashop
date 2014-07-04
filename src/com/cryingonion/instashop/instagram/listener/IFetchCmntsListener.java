package com.cryingonion.instashop.instagram.listener;

import java.util.ArrayList;

import com.cryingonion.instashop.instagram.holder.IgCommentHolder;

/**
 * Listener for comments on a Media.
 * 
 * @author falinch
 *
 */
public interface IFetchCmntsListener {

	public void onIgCmntsFetched(ArrayList<IgCommentHolder> cmntList);
	public void onIgCmntsFailed();
}
