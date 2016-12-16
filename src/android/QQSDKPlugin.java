package me.vanpan.qqsdk;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.apache.cordova.CordovaActivity.TAG;

/**
 * Project: QQSDKPlugin
 * Created by Van on 2016/12/16.
 */

class ShareScene {
    public static final int QQ = 0;
    public static final int QQZone = 1;
    public static final int Favorite = 2;
}
class ImageType {
    public static final int Local = 0;
    public static final int Base64 = 1;
    public static final int Network = 2;
}

public class QQSDKPlugin extends CordovaPlugin  {
    public static final String TAG = "cordova-plugin-qqsdk";
    private static Tencent mTencent;
    private CallbackContext currentCallbackContext;
    private String APP_ID;
    private static final String QQ_APP_ID = "qq_app_id";
    private static final String QQ_CANCEL_BY_USER = "cancelled by user";
    private static final String QQ_RESPONSE_ERROR = "QQ response is error";
    private static final String QZONE_SHARE_CANCEL = "QZone share is cancelled";
    private static final String QQFAVORITES_CANCEL = "QQ Favorites is cancelled";
    private static final String QQ_Client_NOT_INSYALLED_ERROR = "QQ client is not installed";

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
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));
        if (action.equalsIgnoreCase("checkClientInstalled")) {
            return checkClientInstalled(callbackContext);
        }
        if (action.equals("ssoLogin")) {
            return ssoLogin(callbackContext);
        }
        if (action.equals("logout")) {
            return logout(callbackContext);
        }
        return super.execute(action, args, callbackContext);
    }


    /**
     * 检查手机QQ客户端是否安装
     *
     * @param callbackContext
     * @return
     */
    private boolean checkClientInstalled(CallbackContext callbackContext) {
        Boolean installed = mTencent.isSupportSSOLogin(QQSDKPlugin.this.cordova.getActivity());
        if (installed) {
            callbackContext.success();
        } else {
            callbackContext.error(QQ_Client_NOT_INSYALLED_ERROR);
        }
        return true;
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
                    mTencent.getOpenId(),mTencent.getExpiresIn());
            this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK, jo), callbackContext.getCallbackId());
            return true;
        } else {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    mTencent.login(QQSDKPlugin.this.cordova.getActivity(), "all",
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
    public void shareText(String text,int shareScene,CallbackContext callbackContext) {
        final Bundle params = new Bundle();
        currentCallbackContext = callbackContext;
        switch (shareScene) {
            case ShareScene.QQ:
                callbackContext.error("Android不支持分享文字到QQ");
                break;
            case ShareScene.Favorite:
                params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE, GameAppOperation.QQFAV_DATALINE_TYPE_TEXT);
                params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, getAppName());
                params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, text);
                params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, getAppName());
                Runnable favoritesRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTencent.addToQQFavorites(QQSDKPlugin.this.cordova.getActivity(), params, addToQQFavoritesListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(favoritesRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.QQZone:
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, text);
                Runnable zoneRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.publishToQzone(QQSDKPlugin.this.cordova.getActivity(),params,qZoneShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(zoneRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            default:
                break;

        }

    }

    public void shareImage(String image,int imageType,String title, String description,int shareScene,CallbackContext callbackContext) {
        currentCallbackContext = callbackContext;
        if (imageType == ImageType.Network) {
            image = saveBitmapToFile(getBitmapFromURL(image));
        } else if (imageType == ImageType.Local){
            File file = new File(image);
            if(!file.exists()) {
                //image = dealLocalTypeFile(image);
                Log.d("转化后图片地址",image);
            }
        } else if(imageType == ImageType.Base64) {
            image = saveBitmapToFile(decodeBase64ToBitmap(image));
        }
        final Bundle params = new Bundle();
        switch (shareScene) {
            case ShareScene.QQ:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,image);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
                Runnable qqRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.shareToQQ(QQSDKPlugin.this.cordova.getActivity(),params,qqShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(qqRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.Favorite:
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add(image);
                params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE, GameAppOperation.QQFAV_DATALINE_TYPE_IMAGE_TEXT);
                params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, title);
                params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, description);
                params.putString(GameAppOperation.QQFAV_DATALINE_IMAGEURL,image);
                params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, getAppName());
                params.putStringArrayList(GameAppOperation.QQFAV_DATALINE_FILEDATA,imageUrls);
                Runnable favoritesRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTencent.addToQQFavorites(QQSDKPlugin.this.cordova.getActivity(), params, addToQQFavoritesListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(favoritesRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.QQZone:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,image);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                Runnable zoneRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.shareToQQ(QQSDKPlugin.this.cordova.getActivity(),params,qqShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(zoneRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            default:
                break;

        }
    }

    public void shareNews(String url,String image,int imageType,String title, String description,int shareScene,CallbackContext callbackContext) {
        currentCallbackContext = callbackContext;
        if(imageType == ImageType.Base64) {
            image = saveBitmapToFile(decodeBase64ToBitmap(image));
        } else if (imageType == ImageType.Local){
            File file = new File(image);
            if(!file.exists()) {
                //image = dealLocalTypeFile(image);
            }
        }
        final Bundle params = new Bundle();
        switch (shareScene) {
            case ShareScene.QQ:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                if(imageType == ImageType.Network) {
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,image);
                } else  {
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,image);
                }
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
                Runnable qqRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.shareToQQ(QQSDKPlugin.this.cordova.getActivity(),params,qqShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(qqRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.Favorite:
                params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE, GameAppOperation.QQFAV_DATALINE_TYPE_DEFAULT);
                params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, title);
                params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION,description);
                params.putString(GameAppOperation.QQFAV_DATALINE_IMAGEURL,image);
                params.putString(GameAppOperation.QQFAV_DATALINE_URL,url);
                params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, getAppName());
                Runnable favoritesRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTencent.addToQQFavorites(QQSDKPlugin.this.cordova.getActivity(), params, addToQQFavoritesListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(favoritesRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.QQZone:
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add(image);
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,description);
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,url);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imageUrls);
                Runnable zoneRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.shareToQzone(QQSDKPlugin.this.cordova.getActivity(),params,qZoneShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(zoneRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            default:
                break;

        }

    }

    public void shareAudio(String url,String flashUrl,String image,int imageType,String title, String description,int shareScene,CallbackContext callbackContext) {
        currentCallbackContext = callbackContext;
        if(imageType == ImageType.Base64) {
            image = saveBitmapToFile(decodeBase64ToBitmap(image));
        }else if (imageType == ImageType.Local){
            File file = new File(image);
            if(!file.exists()) {
                //image = dealLocalTypeFile(image);
            }
        }
        final Bundle params = new Bundle();
        switch (shareScene) {
            case ShareScene.QQ:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                if(imageType == ImageType.Network) {
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,image);
                } else  {
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,image);
                }
                params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, flashUrl);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
                Runnable qqRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.shareToQQ(QQSDKPlugin.this.cordova.getActivity(),params,qqShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(qqRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.Favorite:
                params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE, GameAppOperation.QQFAV_DATALINE_TYPE_DEFAULT);
                params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, title);
                params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION,description);
                params.putString(GameAppOperation.QQFAV_DATALINE_IMAGEURL,image);
                params.putString(GameAppOperation.QQFAV_DATALINE_URL,url);
                params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, getAppName());
                params.putString(GameAppOperation.QQFAV_DATALINE_AUDIOURL,flashUrl);
                Runnable favoritesRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTencent.addToQQFavorites(QQSDKPlugin.this.cordova.getActivity(), params, addToQQFavoritesListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(favoritesRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            case ShareScene.QQZone:
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add(image);
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,description);
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,url);
                params.putString(QzoneShare.SHARE_TO_QQ_AUDIO_URL,flashUrl);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imageUrls);
                Runnable zoneRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mTencent.shareToQzone(QQSDKPlugin.this.cordova.getActivity(),params,qZoneShareListener);
                    }
                };
                this.cordova.getActivity().runOnUiThread(zoneRunnable);
                this.cordova.setActivityResultCallback(this);
                break;
            default:
                break;

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
     * 获取应用的名称
     * @return
     */
    private String getAppName() {
        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(this.cordova.getActivity().getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {}
        final String AppName = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "AppName");
        return AppName;
    }

    /** 获取Drawble资源的文件ID
     *
     * @param imageName
     * @return
     */
//    private int getDrawableFileID(String imageName) {
//        ResourceDrawableIdHelper sResourceDrawableIdHelper = ResourceDrawableIdHelper.getInstance();
//        int id = sResourceDrawableIdHelper.getResourceDrawableId(getReactApplicationContext(),imageName);
//        return id;
//    }
//    private String dealLocalTypeFile(String image) {
//        Bitmap bitmap = BitmapFactory.decodeResource(getReactApplicationContext().getResources(),getDrawableFileID(image));
//        return  saveBitmapToFile(bitmap);
//    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将Base64解码成Bitmap
     * @param Base64String
     * @return
     */

    private Bitmap decodeBase64ToBitmap(String Base64String) {
        byte[] decode = Base64.decode(Base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return  bitmap;
    }

    /**
     * 将bitmap 保存成文件
     * @param bitmap
     * @return
     */
    private String saveBitmapToFile(Bitmap bitmap) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return pictureFile.getAbsolutePath();
    }

    /**
     * 生成文件用来存储图片
     * @return
     */
    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + this.cordova.getActivity().getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="RN_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    /**
     * 登录监听
     */
    IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR, QQ_RESPONSE_ERROR), currentCallbackContext.getCallbackId());
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR, QQ_RESPONSE_ERROR), currentCallbackContext.getCallbackId());
                return;
            }
            initOpenidAndToken(jsonResponse);
            JSONObject jo = makeJson(mTencent.getAccessToken(),
                    mTencent.getOpenId(),mTencent.getExpiresIn());
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK, jo), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, e.errorMessage), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onCancel() {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQ_CANCEL_BY_USER), currentCallbackContext.getCallbackId());
        }
    };
    /**
     * QQ分享监听
     */
    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQ_CANCEL_BY_USER), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onComplete(Object response) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, e.errorMessage), currentCallbackContext.getCallbackId());
        }

    };
    /**
     * QQZONE 分享监听
     */
    IUiListener qZoneShareListener = new IUiListener() {

        @Override
        public void onCancel() {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QZONE_SHARE_CANCEL), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, e.errorMessage), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onComplete(Object response) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK), currentCallbackContext.getCallbackId());
        }

    };
    /**
     * 添加到QQ收藏监听
     */
    IUiListener addToQQFavoritesListener = new IUiListener() {
        @Override
        public void onCancel() {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, QQFAVORITES_CANCEL), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onComplete(Object response) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.OK), currentCallbackContext.getCallbackId());
        }

        @Override
        public void onError(UiError e) {
            QQSDKPlugin.this.webView.sendPluginResult(new PluginResult(
                    PluginResult.Status.ERROR, e.errorMessage), currentCallbackContext.getCallbackId());
        }
    };

    /**
     * 组装JSON
     *
     * @param access_token
     * @param userid
     * @param expires_time
     * @return
     */
    private JSONObject makeJson(String access_token, String userid, long expires_time) {
        String json = "{\"access_token\": \"" + access_token + "\", " +
                " \"userid\": \"" + userid + "\", " +
                " \"expires_time\": \"" + String.valueOf(expires_time) + "\"" +
                "}";
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
