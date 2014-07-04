package com.cryingonion.instashop.instagram.listener;

import com.cryingonion.instashop.instagram.holder.IgUserInfoHolder;

public interface IUserInfoFetchedListener {
	public void onIgUsrInfoFetched(IgUserInfoHolder usrInfoHolder);

	public void onIgUsrInfoFetchingFailed();
}
