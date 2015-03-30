package org.zy.yuancheng.qq;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.content.Intent;
import android.text.TextUtils;


public class YCQQ extends CordovaPlugin {

	public String callbackId = "";
	public static Tencent mTencent;

	@Override
	public boolean execute(String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub
		boolean result = false;
		String APP_ID = webView.getProperty("qq_app_id", "");
		mTencent = Tencent.createInstance(APP_ID,this.cordova.getActivity().getApplicationContext());
		final PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
		pr.setKeepCallback(true);
		callbackId = callbackContext.getCallbackId();
		if (action.equals("ssoLogin")) {
			if (mTencent.isSessionValid()) {
				JSONObject jo = makeJson(mTencent.getAccessToken(),
						mTencent.getOpenId());
				this.webView.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, jo), this.callbackId);
				result = true;
			} else {
				Runnable runnable=new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mTencent.login(YCQQ.this.cordova.getActivity(), "all", loginListener);			
					}
				};
				this.cordova.getActivity().runOnUiThread(runnable);
				this.cordova.setActivityResultCallback(this);
				result = true;
			}

		}
		if (action.equals("logout")) {
			mTencent.logout(this.cordova.getActivity());
			callbackContext.success();
			result = true;
		}

		return result;
	}

	public static void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
					&& !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
		}
	}

	IUiListener loginListener = new BaseUiListener(this) {
		@Override
		protected void doComplete(JSONObject values) {
			initOpenidAndToken(values);
			JSONObject jo = makeJson(mTencent.getAccessToken(),
					mTencent.getOpenId());
			this.mQQ.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, jo), this.mQQ.callbackId);
		}
	};

	private class BaseUiListener implements IUiListener {
		YCQQ mQQ;
		public BaseUiListener(YCQQ mQQ) {
			// TODO Auto-generated constructor stub
			super();
			this.mQQ = mQQ;
		}
		@Override
		public void onComplete(Object response) {
			if (null == response) {
				this.mQQ.webView.sendPluginResult(new PluginResult(
						PluginResult.Status.ERROR), this.mQQ.callbackId);
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				this.mQQ.webView.sendPluginResult(new PluginResult(
						PluginResult.Status.ERROR), this.mQQ.callbackId);
				return;
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			this.mQQ.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR), this.mQQ.callbackId);
		}

		@Override
		public void onCancel() {
			this.mQQ.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR), this.mQQ.callbackId);
		}
	}

	/**
	 * 组装JSON
	 * 
	 * @param access_token
	 * @param uid
	 * @return
	 */
	private JSONObject makeJson(String access_token, String userid) {
		String json = "{\"access_token\": \"" + access_token
				+ "\",  \"userid\": \"" + userid + "\"}";
		JSONObject jo = null;
		try {
			jo = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(intent, loginListener);
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);

	}

}