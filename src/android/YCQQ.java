package org.zy.yuancheng.qq;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.GameAppOperation;
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

    private static Tencent mTencent;
    private CallbackContext currentCallbackContext;
    private String APP_ID;
    private static final String QQ_APP_ID = "qq_app_id";
    private static final String QQ_CANCEL_BY_USER = "cancelled by user";
    private static final String QQ_SHARE_ERROR = "error happened when sharing";
    private static final String QQ_LOGIN_ERROR = "error happened when logging";
    private static final String QQ_PARAM_ERROR = "param incorrect";
    private static final String QQ_RESPONSE_ERROR = "QQ response is error";
    private static final String QZONE_SHARE_ERROR = "QZone share is error";
    private static final String QZONE_SHARE_CANCEL = "QZone share is cancelled";
    private static final String QQFAVORITES_ERROR = "QQ Favorites is error";
    private static final String QQFAVORITES_CANCEL = "QQ Favorites is cancelled";
    private static final String QQ_Client_NOT_INSYALLED_ERROR = "QQ client is not installed";
    private static final String TITLIE_IS_EMPTY = "share title is empty";
    private static final String URL_IS_EMPTY = "share url is empty";

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        APP_ID = webView.getPreferences().getString(QQ_APP_ID, "");
        mTencent = Tencent.createInstance(APP_ID, this.cordova.getActivity()
                .getApplicationContext());
    }

    @Override
    public boolean execute(String action, final JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {
        if (action.equals("ssoLogin")) {
            return ssoLogin(callbackContext);
        }
        if (action.equals("logout")) {
            return logout(callbackContext);
        }
        if (action.equalsIgnoreCase("shareToQQ")) {
            return qqshare(args, callbackContext);
        }
        if (action.equalsIgnoreCase("checkClientInstalled")) {
            return checkClientInstalled(callbackContext);
        }
        if (action.equalsIgnoreCase("shareToQzone")) {
            return shareToQzone(args, callbackContext);
        }
        if (action.equalsIgnoreCase("addToQQFavorites")) {
            return addToQQFavorites(args, callbackContext);
        }
        return super.execute(action, args, callbackContext);
    }

    /**
     * QQ 单点登录
     *
     * @param callbackContext
     * @return
     */
    private boolean ssoLogin(CallbackContext callbackContext) {
        currentCallbackContext = callbackContext;
        if (mTencent.isSessionValid()) {
            JSONObject jo = makeJson(mTencent.getAccessToken(),
                    mTencent.getOpenId());
            this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK, jo), callbackContext.getCallbackId());
            return true;
        } else {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
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
     *
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
     *
     * @param args
     * @param callbackContext
     * @return
     * @throws JSONException
     */
    private boolean qqshare(JSONArray args, CallbackContext callbackContext) throws JSONException {
        currentCallbackContext = callbackContext;
        JSONObject json = args.getJSONObject(0);
        checkNecessaryArgs(json, callbackContext);
        String imgUrl = json.getString("imageUrl");
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, json.getString("title"));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, json.getString("description"));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, json.getString("url"));
        if (imgUrl != null && !imgUrl.equalsIgnoreCase("")) {
            if (imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
            }
        }
        if (json.has("appName") && !json.getString("appName").equalsIgnoreCase("")) {
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
     *
     * @param callbackContext
     * @return
     */
    private boolean checkClientInstalled(CallbackContext callbackContext) {
        Boolean installed = mTencent.isSupportSSOLogin(YCQQ.this.cordova.getActivity());
        if (installed) {
            callbackContext.success();
        } else {
            callbackContext.error(QQ_Client_NOT_INSYALLED_ERROR);
        }
        return true;
    }

    /**
     * 分享到QQ空间
     */
    private boolean shareToQzone(JSONArray args, CallbackContext callbackContext) throws JSONException {
        currentCallbackContext = callbackContext;
        JSONObject json = args.getJSONObject(0);
        checkNecessaryArgs(json, callbackContext);
        ArrayList<String> imageUrls = new ArrayList<String>();
        JSONArray imgUrl = json.getJSONArray("imageUrl");
        for (int i = 0; i < imgUrl.length(); i++) {
            if (imgUrl.get(i) != null && !imgUrl.get(i).toString().equalsIgnoreCase("")) {
                if (imgUrl.get(i).toString().startsWith("http://") || imgUrl.get(i).toString().startsWith("https://")) {
                    imageUrls.add(imgUrl.get(i).toString());
                }
            }
        }
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, json.getString("title"));
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, json.getString("description"));
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, json.getString("url"));
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        this.cordova.setActivityResultCallback(this);
        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTencent.shareToQzone(YCQQ.this.cordova.getActivity(), params,
                        qZoneShareListener);
            }
        });
        return true;
    }

    /**
     * 添加到QQ收藏
     *
     * @param args
     * @param callbackContext
     * @return
     * @throws JSONException
     */
    private boolean addToQQFavorites(JSONArray args, CallbackContext callbackContext) throws JSONException {
        currentCallbackContext = callbackContext;
        JSONObject json = args.getJSONObject(0);
        if (json.getString("appName").equalsIgnoreCase("") || json.getString("imageUrl").equalsIgnoreCase("") || json.getString("title").equalsIgnoreCase("")) {
            callbackContext.error(QQ_PARAM_ERROR);
        }
        final Bundle params = new Bundle();
        params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, json.getString("appName"));
        params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, json.getString("title"));
        params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE, GameAppOperation.QQFAV_DATALINE_TYPE_DEFAULT);
        params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, json.getString("description"));
        params.putString(GameAppOperation.QQFAV_DATALINE_IMAGEURL, json.getString("imageUrl"));
        params.putString(GameAppOperation.QQFAV_DATALINE_URL, json.getString("url"));
        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTencent.addToQQFavorites(YCQQ.this.cordova.getActivity(), params,
                        addToQQFavoritesListener);
            }
        });
        return true;
    }

    /**
     * 检查参数
     *
     * @param json
     * @param callbackContext
     */
    private void checkNecessaryArgs(JSONObject json, CallbackContext callbackContext) throws JSONException {
        if (json == null || json.length() == 0) {
            callbackContext.error(QQ_PARAM_ERROR);
        } else {
            if (json.getString("title").equalsIgnoreCase("")) {
                callbackContext.error(TITLIE_IS_EMPTY);
            }
            if (json.getString("url").equalsIgnoreCase("")) {
                callbackContext.error(URL_IS_EMPTY);
            }
        }
    }

    /**
     * 保存token 和 openid
     *
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
    IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                YCQQ.this.webView.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR, QQ_RESPONSE_ERROR), currentCallbackContext.getCallbackId());
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                YCQQ.this.webView.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR, QQ_RESPONSE_ERROR), currentCallbackContext.getCallbackId());
                return;
            }
            initOpenidAndToken(jsonResponse);
            JSONObject jo = makeJson(mTencent.getAccessToken(),
                    mTencent.getOpenId());
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK, jo), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError uiError) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQ_LOGIN_ERROR), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onCancel() {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQ_CANCEL_BY_USER), currentCallbackContext.getCallbackId());
        }
    };
    /**
     * QQ分享监听
     */
    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQ_CANCEL_BY_USER), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onComplete(Object response) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQ_SHARE_ERROR), currentCallbackContext.getCallbackId());
        }

    };
    /**
     * QQZONE 分享监听
     */
    IUiListener qZoneShareListener = new IUiListener() {

        @Override
        public void onCancel() {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QZONE_SHARE_CANCEL), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QZONE_SHARE_ERROR), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onComplete(Object response) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK), currentCallbackContext.getCallbackId());
        }

    };
    /**
     * 添加到QQ收藏监听
     */
    IUiListener addToQQFavoritesListener = new IUiListener() {
        @Override
        public void onCancel() {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQFAVORITES_CANCEL), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onComplete(Object response) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            YCQQ.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQFAVORITES_ERROR), currentCallbackContext.getCallbackId());
        }
    };

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
        mTencent.onActivityResultData(requestCode,resultCode,intent,loginListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTencent != null) {
            mTencent.releaseResource();
        }
    }
}