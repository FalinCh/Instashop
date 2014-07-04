package com.cryingonion.instashop.instagram.listener;

/**
 * Interface for status of the request.
 * 
 * @author falinch
 *
 */
public interface IReqStatusListener {

	public abstract void onSuccess(int reqType);
	public abstract void onFail(int reqType,String error);
}
