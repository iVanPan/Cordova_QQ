package org.zy.yuancheng.qq;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class YCQQ extends CordovaPlugin {

	public String callbackId = "";
	public static Tencent mTencent;
	public static String APP_ID;
	public static CallbackContext currentCallbackContext;
	public static final String QQ_APP_ID ="qq_app_id";
	public static final String QQ_CANCEL_BY_USER ="cancelled by user";
	public static final String QQ_SHARE_ERROR ="error happend when sharing";
	public static final String QQ_LOGIN_ERROR ="error happend when loging";
	public static final String QQ_PARAM_ERROR ="paran incorrect";
	public static final String QQ_RESONPSE_ERROR ="QQ resopnse is error";
	public static final String QQZONE_SHARE_ERROR ="QQZONE share is error";
	public static final String QQZONE_SHARE_CANCEL ="QQZONE share is cancelled";
	public static final String QQ_Client_NOT_INSYALLED_ERROR ="QQ client is not installed";
	public static final String TITLIE_IS_EMPTY ="share title is empty";
	public static final String URL_IS_EMPTY ="share url is empty";

	@Override
	protected void pluginInitialize() {
		// TODO Auto-generated method stub
		super.pluginInitialize();
		APP_ID = webView.getPreferences().getString(QQ_APP_ID,"");
		mTencent = Tencent.createInstance(APP_ID, this.cordova.getActivity()
				.getApplicationContext());
	}
	@Override
	public boolean execute(String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub
		if (action.equals("ssoLogin")) {
			return ssoLogin(callbackContext);
		}
		if (action.equals("logout")) {			
			return logout(callbackContext);
		}
		if (action.equalsIgnoreCase("shareToQQ")) {
			return qqshare(args, callbackContext);
		}
		if(action.equalsIgnoreCase("checkClientInstalled")){
			return checkClientInstalled(callbackContext);
		}
		if (action.equalsIgnoreCase("shareToQzone")){
			return shareToQzone(args,callbackContext);
		}
		return super.execute(action, args, callbackContext);
	}
	/**
	 * QQ 单点登录
	 * @param callbackContext
	 * @return
	 */
	private boolean ssoLogin(CallbackContext callbackContext) {
		callbackId = callbackContext.getCallbackId();
		if (mTencent.isSessionValid()) {
			JSONObject jo = makeJson(mTencent.getAccessToken(),
					mTencent.getOpenId());
			this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, jo), this.callbackId);
			return true;
		} else {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTencent.login(YCQQ.this.cordova.getActivity(), "all",
							loginListener);
//					mTencent.loginServerSide(YCQQ.this.cordova.getActivity(), "all",
//							loginListener);
				}
			};
			this.cordova.getActivity().runOnUiThread(runnable);
			this.cordova.setActivityResultCallback(this);
			return true;
		}
		
	}
	/**
	 * QQ 登出
	 * @param callbackContext
	 * @return
	 */
	private boolean logout(CallbackContext callbackContext) {
		mTencent.logout(this.cordova.getActivity());
		callbackContext.success();
		return true;
	}

	/**
	 * QQ 分享
	 * @param args
	 * @param callbackContext
	 * @return
	 * @throws JSONException
	 */
	private boolean qqshare(JSONArray args,CallbackContext callbackContext) throws JSONException {
		currentCallbackContext =callbackContext;
		JSONObject json = args.getJSONObject(0);
		callbackId =callbackContext.getCallbackId();
		checkNecessaryArgs(json,callbackContext);
		String imgUrl = json.getString("imageUrl");
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, json.getString("title"));
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, json.getString("description"));
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, json.getString("url"));
		if (imgUrl!=null && !imgUrl.equalsIgnoreCase("")) {
			if(imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
			}
		}
		if(json.has("appName") && !json.getString("appName").equalsIgnoreCase("")){
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, json.getString("appName"));
		}
		this.cordova.setActivityResultCallback(this);
		this.cordova.getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTencent.shareToQQ(YCQQ.this.cordova.getActivity(), params,
						qqShareListener);
			}
		});

		return true;
	}
	/**
	 * 检查手机QQ客户端是否安装
	 * @param callbackContext
	 * @return
	 */
	private boolean checkClientInstalled(CallbackContext callbackContext){
		Boolean installed = mTencent.isSupportSSOLogin(YCQQ.this.cordova.getActivity());
		if(installed){
			callbackContext.success();
		}else{
			callbackContext.error(QQ_Client_NOT_INSYALLED_ERROR);
		}
		return true;
	}
	/**
	 * 分享到QQ空间
	 */
	private boolean shareToQzone (JSONArray args,CallbackContext callbackContext) throws JSONException{
		currentCallbackContext =callbackContext;
		JSONObject json = args.getJSONObject(0);
		callbackId =callbackContext.getCallbackId();
		checkNecessaryArgs(json,callbackContext);
		ArrayList<String> imageUrls = new ArrayList<String>();
		JSONArray imgUrl = json.getJSONArray("imageUrl");
		for(int i=0;i<imgUrl.length();i++){
			if (imgUrl.get(i)!=null && !imgUrl.get(i).toString().equalsIgnoreCase("")) {
				if(imgUrl.get(i).toString().startsWith("http://") || imgUrl.get(i).toString().startsWith("https://")){
					imageUrls.add(imgUrl.get(i).toString());
				}
			}
		}
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, json.getString("title"));//必填
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, json.getString("description"));//选填
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, json.getString("url"));//必填
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		this.cordova.setActivityResultCallback(this);
		this.cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTencent.shareToQzone(YCQQ.this.cordova.getActivity(), params,
						qZoneShareListener);
			}
		});
		return true;
	}

	/**
	 * 检查参数
	 * @param json
	 * @param callbackContext
	 */
	private void checkNecessaryArgs(JSONObject json,CallbackContext callbackContext) throws JSONException{
		if(json ==null || json.length() == 0 ){
			callbackContext.error(QQ_PARAM_ERROR);
		}else{
			if(json.getString("title").equalsIgnoreCase("")){
				callbackContext.error(TITLIE_IS_EMPTY);
			}
			if(json.getString("url").equalsIgnoreCase("")){
				callbackContext.error(URL_IS_EMPTY);
			}
		}
	}
	/**
	 * 保存token 和 openid
	 * @param jsonObject
	 */
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
	/**
	 * 登录监听
	 */
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
	 /**
	  * QQ分享监听
	  */
	IUiListener qqShareListener = new IUiListener() {
		@Override
		public void onCancel() {
			YCQQ.this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR,QQ_CANCEL_BY_USER), YCQQ.this.callbackId);
		}

		@Override
		public void onComplete(Object response) {
			YCQQ.this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.OK), YCQQ.this.callbackId);
		}

		@Override
		public void onError(UiError e) {
			YCQQ.this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR,QQ_SHARE_ERROR), YCQQ.this.callbackId);
		}
		
	};
	/**
	 * QQZONE 分享监听
	 */
	IUiListener qZoneShareListener = new IUiListener() {

		@Override
		public void onCancel() {
			YCQQ.this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR,QQZONE_SHARE_CANCEL), YCQQ.this.callbackId);
		}

		@Override
		public void onError(UiError e) {
			YCQQ.this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR,QQZONE_SHARE_ERROR), YCQQ.this.callbackId);
		}

		@Override
		public void onComplete(Object response) {
			YCQQ.this.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.OK), YCQQ.this.callbackId);
		}

	};
	private class BaseUiListener implements IUiListener {
		YCQQ mQQ;

		public BaseUiListener(YCQQ mQQ) {
			super();
			this.mQQ = mQQ;
		}

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				this.mQQ.webView.sendPluginResult(new PluginResult(
						PluginResult.Status.ERROR,QQ_RESONPSE_ERROR), this.mQQ.callbackId);
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				this.mQQ.webView.sendPluginResult(new PluginResult(
						PluginResult.Status.ERROR,QQ_RESONPSE_ERROR), this.mQQ.callbackId);
				return;
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			this.mQQ.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR,QQ_LOGIN_ERROR), this.mQQ.callbackId);
		}

		@Override
		public void onCancel() {
			this.mQQ.webView.sendPluginResult(new PluginResult(
					PluginResult.Status.ERROR,QQ_CANCEL_BY_USER), this.mQQ.callbackId);
		}
	}

	/**
	 * 组装JSON
	 * 
	 * @param access_token
	 * @param userid
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
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(intent, loginListener);
			}
		}
		if (requestCode == Constants.REQUEST_QQ_SHARE) {
			if (resultCode == Constants.ACTIVITY_OK) {
				Tencent.handleResultData(intent, qqShareListener);
			}
		}
		if (requestCode == Constants.REQUEST_QZONE_SHARE) {
			if (resultCode == Constants.ACTIVITY_OK) {
				Tencent.handleResultData(intent, qZoneShareListener);
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

}