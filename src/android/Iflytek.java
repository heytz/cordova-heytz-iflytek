package com.heytz.iflytek;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.iflytek.cloud.*;
import com.iflytek.sunflower.FlowerCollector;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * This class echoes a string called from JavaScript.
 */
public class Iflytek extends CordovaPlugin {
    private static SpeechRecognizer mAsr;
    private static Context context;

    private static final String TAG = "Iflytek Tag";
    private static final String IFLYTEK_APP_KEY = "iflytekappkey";
    private static final String VOICE_RECOGNITION_START = "VoiceRecognitionStart";
    private static final String VOICE_RECOGNITION_STOP = "VoiceRecognitionStop";
    private static HeytzRecognizerListener heytzRecognizerListener = new HeytzRecognizerListener();
    private static RecognizerListener recognizerListener = new RecognizerListener() {
        /**
         *  音量变化
         * @param i
         * @param bytes
         */
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            String str = null;
            try {
                str = new String(bytes, "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "音量变化:" + i + str);
        }

        /**
         *开始说话
         */
        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "开始说话");
        }

        /**
         *结束说话
         */
        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "结束说话");
        }

        /**
         * 返回结果
         * @param recognizerResult
         * @param b
         */
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.d(TAG, "返回结果");
        }

        /**
         * 错误回调
         * @param speechError
         */
        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "错误回调" + speechError.getErrorDescription());
        }

        /**
         * 事件回调
         * @param i
         * @param i1
         * @param i2
         * @param bundle
         */
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            Log.d(TAG, "事件回调" + i + "" + i1 + "" + i2);
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        String appid = webView.getPreferences().getString(IFLYTEK_APP_KEY, "57e4a307");
        context = cordova.getActivity().getApplicationContext();
        // 将“12345678”替换成您申请的 APPID，申请地址:http://www.xfyun.cn
        // 请勿在“=”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + appid);

//        FlowerCollector.setDebugMode(true);//开启调试模式
        FlowerCollector.setAutoLocation(true);//开启自动获取位置信息
        FlowerCollector.setCaptureUncaughtException(true);//开启自动捕获异常信息

        //在线命令词识别，不启用终端级语法
        // 1.创建SpeechRecognizer对象
        mAsr = SpeechRecognizer.createRecognizer(cordova.getActivity().getApplicationContext(), null);
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        // 2.设置参数
        mAsr.setParameter(SpeechConstant.SUBJECT, "asr");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(VOICE_RECOGNITION_START)) {

            int ret = mAsr.startListening(recognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.d(TAG, "识别失败,错误码: " + ret);
                callbackContext.error(ret);
            } else {

            }
            return true;
        }
        if (action.equals(VOICE_RECOGNITION_STOP)) {
            mAsr.startListening(null);

            return true;
        }
        return false;
    }
}
