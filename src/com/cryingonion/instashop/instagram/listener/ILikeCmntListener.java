package com.cryingonion.instashop.instagram.listener;

/**
 * To listen to like and comment 
 * 
 * @author falinch
 *
 */
public interface ILikeCmntListener {

	public abstract void onCopmplete(int reqType,int responseCode);
}
