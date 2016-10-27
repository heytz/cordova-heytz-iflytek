package com.heytz.iflytek;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.iflytek.cloud.*;
import com.iflytek.sunflower.FlowerCollector;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * This class echoes a string called from JavaScript.
 */
public class Iflytek extends CordovaPlugin {

    private static String TAG = Iflytek.class.getSimpleName();

    // appid 使用的是Charlie账号下,应用黑子智能
    private static final String IFLYTEK_APP_ID = "5538a7f6";
    private static final String VOICE_RECOGNITION_START = "VoiceRecognitionStart";
    private static final String VOICE_RECOGNITION_STOP = "VoiceRecognitionStop";

    private SpeechUtility speechUtility;

    private SpeechRecognizer speechRecognizer;

    private HeytzRecognizerListener heytzRecognizerListener;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Context context = cordova.getActivity().getApplicationContext();

        speechUtility = SpeechUtility.getUtility();
        if (speechUtility == null) {
            speechUtility = SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + IFLYTEK_APP_ID);
        }

//        FlowerCollector.setDebugMode(true);//开启调试模式
        FlowerCollector.setAutoLocation(true);//开启自动获取位置信息
        FlowerCollector.setCaptureUncaughtException(true);//开启自动捕获异常信息

        // 1.创建SpeechRecognizer对象
        speechRecognizer = SpeechRecognizer.createRecognizer(context, null);

        // 2.创建监听对象
        heytzRecognizerListener = new HeytzRecognizerListener();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean exists = false;
        Log.i(TAG, "action=" + action);
        if (action.equals(VOICE_RECOGNITION_START)) {
            exists = true;

            // 设置事件中的回调事件
            heytzRecognizerListener.setCallback(callbackContext);

            // 2.设置参数
            setParameters();

            // 开始
            int ret = speechRecognizer.startListening(heytzRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.i(TAG, "启动失败,错误码: " + ret);
                callbackContext.error("{\"code\":" + ret + "}");
            }
        }

        if (action.equals(VOICE_RECOGNITION_STOP)) {
            exists = true;
            if (speechRecognizer.isListening()) {
                Log.i(TAG, "正在监听");
                speechRecognizer.stopListening();
            }
            Log.i(TAG, "停止成功");
            callbackContext.success();
        }
        return exists;
    }

    private void setParameters() {
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 不使用标点
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置为识别引擎
        // speechRecognizer.setParameter(SpeechConstant.SUBJECT, SpeechConstant.ENG_ASR);


        // 设置此参数为真后，网络状态将通过onEvent函数的SpeechEvent.EVENT_NETPREF 事件返回给应用层。
        speechRecognizer.setParameter(SpeechConstant.ASR_NET_PERF, "true");

        // 静音抑制
        // speechRecognizer.setParameter(SpeechConstant.VAD_ENABLE, null);

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        // 退出时释放连接
        if (speechRecognizer.isListening()) {
            speechRecognizer.cancel();
        }
        speechRecognizer.destroy();
        SpeechUtility.getUtility().destroy();
        super.onDestroy();
    }

}
