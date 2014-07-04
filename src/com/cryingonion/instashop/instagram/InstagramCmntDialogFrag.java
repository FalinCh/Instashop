package com.cryingonion.instashop.instagram;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cryingonion.instashop.instagram.listener.IFetchCmntsListener;
import com.cryingonion.instashop.instagram.listener.ILikeCmntListener;
import com.cryingonion.instashop.instagram.InstagramConstants;
import com.cryingonion.instashop.instagram.InstagramWrapper;
import com.cryingonion.instashop.instagram.adapter.IgCmntAdapter;
import com.cryingonion.instashop.R;
import com.cryingonion.instashop.instagram.holder.IgCommentHolder;

@SuppressLint("ValidFragment")
public class InstagramCmntDialogFrag extends DialogFragment implements OnClickListener {

	private Context mContext;

	private View mVwParent;
	private EditText mEdtTxtCmnt;
	private ListView mLstVwCmnts;
	private View mEmptyView;
	private Button mBtnPost;

	private Dialog mDialog;

	private ArrayList<IgCommentHolder> mCmntList;
	@SuppressLint("ValidFragment")
	private IgCmntAdapter mCmntAdapter;

	private String mMediaId;

	public InstagramCmntDialogFrag(Context context, String mediaId) {
		mContext = context;
		mMediaId = mediaId;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mDialog = new Dialog(getActivity(), android.R.style.Theme_Light);

		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.activity_comment);
		// To Hide keyboard popping on Activity launch.
		mDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// To dismiss the dialog on touching outside dialog.
		mDialog.setCanceledOnTouchOutside(true);

		init();

		mDialog.show();

		return mDialog;
	}
	
    private void init(){
		
		mLstVwCmnts = (ListView) mDialog.findViewById(R.id.lst_cmnts);
		mEmptyView = mDialog.findViewById(R.id.txt_empty_view);
		
		mBtnPost = (Button)mDialog.findViewById(R.id.btnComment);
		mEdtTxtCmnt = (EditText)mDialog.findViewById(R.id.edtComment);
		mEdtTxtCmnt.addTextChangedListener(textWatcherComment);
		
		mCmntAdapter = new IgCmntAdapter(mContext);
		mLstVwCmnts.setAdapter(mCmntAdapter);
		mLstVwCmnts.setEmptyView(mEmptyView);
		
		mBtnPost.setOnClickListener(this);
		
		getCmntsOnMedia();
	}
    
	// To enable the Post button only when edit-text is non-empty
	TextWatcher textWatcherComment = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (TextUtils.isEmpty(s)) {
				mBtnPost.setEnabled(false);
			} else {
				mBtnPost.setEnabled(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};

	private void getCmntsOnMedia() {
		InstagramWrapper wrapper = new InstagramWrapper(mContext);
		wrapper.getCommentsOnMedia(mMediaId, mCmntsListener);
	}
    
    /**
	 * Comments fetch success or failure listener.
	 */
	private final IFetchCmntsListener mCmntsListener = new IFetchCmntsListener() {
		
		@Override
		public void onIgCmntsFetched(ArrayList<IgCommentHolder> cmntList) {
			Log.d(InstagramConstants.TAG,"Cmnt fetched, size :"+cmntList.size());
			setCmntAdapter(cmntList);
		}
		
		@Override
		public void onIgCmntsFailed() {			
			Log.d(InstagramConstants.TAG,"Failed to get the cmnts");
		}
	};
	
    private void setCmntAdapter(ArrayList<IgCommentHolder> cmntList){
		
		if(null == mCmntList)
			mCmntList = cmntList;
		
		mCmntAdapter.setDataSource(mCmntList);
		mCmntAdapter.notifyDataSetChanged();		
	}
    
    private ILikeCmntListener mPostLikeCmntListener = new ILikeCmntListener() {

		@Override
		public void onCopmplete(int reqType, int responseCode) {

			if (responseCode == 200) {
				Log.d(InstagramConstants.TAG, "Post like/cmnt Success.");
				if (reqType == InstagramConstants.IG_REQ_POST_LIKE) {
					Log.d(InstagramConstants.TAG, " Like done succesfully.");
				} else {
					Log.d(InstagramConstants.TAG, " Comment posted succesfully.");
				}
			} else {
				Log.d(InstagramConstants.TAG, "Post like/cmnt failed.");
			}

		}
	};
	
	@Override
    public void onClick(View view) {
		
		if (mEdtTxtCmnt.getText().toString().trim().length() != 0) {

			// post comment.
			InstagramWrapper wrapper = new InstagramWrapper(mContext);
			wrapper.postCmntOnMedia(InstagramConstants.IG_REQ_POST_CMNT, mEdtTxtCmnt
					.getText().toString(), mMediaId, mPostLikeCmntListener);

		} else {
			Toast.makeText(getActivity(), "Message cannot be empty",
					Toast.LENGTH_SHORT).show();
		}
	}
}
