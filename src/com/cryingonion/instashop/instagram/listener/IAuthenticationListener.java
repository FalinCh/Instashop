package com.cryingonion.instashop.instagram.listener;

/**
 * To listen Authentication events.
 * 
 * @author falinch
 *
 */
public interface IAuthenticationListener {

	public abstract void onAuthSuccess();
	public abstract void onAuthFail(String error);
}
