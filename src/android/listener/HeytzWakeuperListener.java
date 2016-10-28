package com.heytz.iflytek.listener;

/**
 * Created by Alben on 16/10/28.
 */

import android.os.Bundle;
import android.util.Log;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechEvent;


import org.json.JSONException;
import org.json.JSONObject;

public class HeytzWakeuperListener extends HeytListener implements WakeuperListener {
    private static final String TAG = HeytzWakeuperListener.class.getSimpleName();

    @Override
    public void onResult(WakeuperResult result) {
        Log.d(TAG, "onResult");
        String resultString;
        try {
            String text = result.getResultString();
            JSONObject object;
            object = new JSONObject(text);
            StringBuffer buffer = new StringBuffer();
            buffer.append("【RAW】 " + text);
            buffer.append("\n");
            buffer.append("【操作类型】" + object.optString("sst"));
            buffer.append("\n");
            buffer.append("【唤醒词id】" + object.optString("id"));
            buffer.append("\n");
            buffer.append("【得分】" + object.optString("score"));
            buffer.append("\n");
            buffer.append("【前端点】" + object.optString("bos"));
            buffer.append("\n");
            buffer.append("【尾端点】" + object.optString("eos"));
            resultString = buffer.toString();
            if (hasCallbackContext()) {
                callback.success("唤醒成功");
            }
        } catch (JSONException e) {
            resultString = "结果解析出错";
            e.printStackTrace();
        }
        Log.i(TAG, resultString);
    }


    @Override
    public void onBeginOfSpeech() {
        results.clear(); // 清空上一次听写的结果
        Log.i(TAG, "说话开始");
    }

    @Override
    public void onError(SpeechError speechError) {
        Log.i(TAG, "ERROR " + speechError.getPlainDescription(true));
        if (hasCallbackContext()) {
            callback.error("{\"code\":\"" + speechError.getErrorCode()
                    + "\",\"message\":\"" + speechError.getErrorDescription() + "\"}");
        }
    }

    @Override
    public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
        Log.i(TAG, "事件回调 消息类型:" + eventType + ",网络连接值:" + i1 + "," + i2 + ",消息内容:" + bundle);
        if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
            // 当使用唤醒+识别功能时获取识别结果
            // i1:是否最后一个结果,1:是,0:否。
            RecognizerResult reslut = ((RecognizerResult) bundle.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
            Log.i(TAG, "result:" + reslut);
        }
    }

    @Override
    public void onVolumeChanged(int i) {
        Log.i(TAG, "音量变化 音量:" + i);
    }

}
