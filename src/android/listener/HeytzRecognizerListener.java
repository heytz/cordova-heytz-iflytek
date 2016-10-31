/**
 * Created by chendongdong on 2016/9/26.
 */

package com.heytz.iflytek.listener;

import android.os.Bundle;
import android.util.Log;
import com.iflytek.cloud.*;
import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.heytz.iflytek.util.JsonParser;

/**
 * 扩展识别监听器
 */
public class HeytzRecognizerListener extends HeytzListener implements RecognizerListener {

    private static String TAG = HeytzRecognizerListener.class.getSimpleName();

    @Override
    public void onVolumeChanged(int i, byte[] bytes) {
        Log.i(TAG, "音量变化 音量:" + i);
    }

    @Override
    public void onBeginOfSpeech() {
        results.clear(); // 清空上一次听写的结果
        Log.i(TAG, "说话开始");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "说话结束");
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isLast) {

        String text = JsonParser.parseIatResult(recognizerResult.getResultString());
        String sn = null;

        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        results.put(sn, text);
        Log.i(TAG, "返回结果:" + text);

        // 最后一次,返回听写出的文本
        if (isLast && hasCallbackContext()) {
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : results.keySet()) {
                resultBuffer.append(results.get(key));
            }
            callback.success(resultBuffer.toString());
        }
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
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
        Log.i(TAG, "事件回调 消息类型:" + i + ",网络连接值:" + i1 + "," + i2 + ",消息内容:" + bundle);
    }


}
