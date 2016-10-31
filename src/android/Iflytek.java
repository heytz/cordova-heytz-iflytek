package com.heytz.iflytek;

import android.content.Context;
import android.util.Log;
import com.heytz.iflytek.listener.HeytzRecognizerListener;
import com.heytz.iflytek.listener.HeytzWakeuperListener;
import com.iflytek.cloud.*;
import com.iflytek.cloud.util.ResourceUtil;
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
    private static final String VOICE_WAKEUP_START = "startWakeuper";
    private static final String VOICE_WAKEUP_STOP = "stopWakeuper";

    private SpeechUtility speechUtility;


    // 语音听写
    private SpeechRecognizer speechRecognizer;
    private HeytzRecognizerListener heytzRecognizerListener;

    // 唤醒
    private VoiceWakeuper voiceWakeuper;
    private HeytzWakeuperListener heytzWakeuperListener;


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


    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean exists = false;
        Log.i(TAG, "action=" + action);
        if (action.equals(VOICE_RECOGNITION_START)) {
            exists = true;

            // 设置参数
            setParameters();


            // 设置事件中的回调事件
            heytzRecognizerListener.setCallback(callbackContext);


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

        if (action.equals(VOICE_WAKEUP_START)) {
            exists = true;
            if (voiceWakeuper == null || !voiceWakeuper.isListening()) {

                // 设置参数
                setWakeupParameters();

                // 设置回调事件
                heytzWakeuperListener.setCallback(callbackContext);

                // 启动
                voiceWakeuper.startListening(heytzWakeuperListener);
            }
        }

        // 停止唤醒
        if (action.equals(VOICE_WAKEUP_STOP)) {
            exists = true;
            if (voiceWakeuper != null && voiceWakeuper.isListening()) {
                voiceWakeuper.stopListening();
            }
            callbackContext.success();
        }
        return exists;
    }

    private void setParameters() {

        // 听写
        // 1.创建SpeechRecognizer对象
        speechRecognizer = SpeechRecognizer.getRecognizer();
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createRecognizer(cordova.getActivity().getApplicationContext(), null);
        }


        // 2.创建监听对象
        heytzRecognizerListener = new HeytzRecognizerListener();

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

        //开始录入音频后，音频前面部分最长静音时长。
        //speechRecognizer.setParameter(SpeechConstant.VAD_BOS, 1000);

        //音频后面部分最长静音时长
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS, 1000);


    }


    // 唤醒
    private void setWakeupParameters() {
        voiceWakeuper = VoiceWakeuper.getWakeuper();
        if (voiceWakeuper == null) {
            //1.创建唤醒对象
            voiceWakeuper = VoiceWakeuper.createWakeuper(cordova.getActivity().getApplicationContext(), null);
        }

        heytzWakeuperListener = new HeytzWakeuperListener();


        // 清空参数
        voiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        voiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:10,1:10");
        // 设置唤醒模式
        voiceWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        //voiceWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
        // 设置闭环优化网络模式
//        wakeuper.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒资源路径
        String resource = getResource();
        Log.i(TAG, "resource:" + resource);
        voiceWakeuper.setParameter(SpeechConstant.IVW_RES_PATH, resource);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        // 退出时释放连接
        if (speechRecognizer != null) {
            if (speechRecognizer.isListening()) {
                speechRecognizer.cancel();
            }
            speechRecognizer.destroy();
        }
//      SpeechUtility.getUtility().destroy();
        super.onDestroy();
    }


    private String getResource() {
        return ResourceUtil.generateResourcePath(cordova.getActivity().getApplicationContext(),
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + IFLYTEK_APP_ID + ".jet");
    }

}
