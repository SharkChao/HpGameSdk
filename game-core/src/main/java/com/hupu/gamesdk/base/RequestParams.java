package com.hupu.gamesdk.base;

import android.text.TextUtils;

import com.hupu.gamesdk.init.HpGameAppInfo;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;

public class RequestParams implements Serializable {

    public static void putParams(FormBody.Builder fromBody,String key,String value){
        if(!TextUtils.isEmpty(value)) {
            fromBody.add(key, value);
        }
    }

    public static void putParams(HttpUrl.Builder httpUrl,String key, String value){
        httpUrl.addQueryParameter(key,value);
    }

    /**
     * 获取参数签名字符串
     * @param
     * @return
     */
    public  static String getSign(ConcurrentHashMap<String, String> stringParams){
        List<Map.Entry<String, String> > params =new ArrayList<Map.Entry<String, String> >(stringParams.entrySet());
        // 对HashMap中的key 进行排序
        Collections.sort(params, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey().toString());
            }
        });
        StringBuilder result = new StringBuilder();
        for(ConcurrentHashMap.Entry<String, String> entry : params) {
            if(result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        String  sign = new HPMd5().md5(HpGameAppInfo.INSTANCE.getAppId() + result.toString() +HpGameAppInfo.INSTANCE.getAppKey() );
        return sign;
    }


    public static ConcurrentHashMap jsonToParams(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keys();
        ConcurrentHashMap<String, String> stringParams = new ConcurrentHashMap<>();
        while(iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.opt(key);
            if(value != null &&
                    (value instanceof Integer || value instanceof String || value instanceof Long)) {
                stringParams.put(key, value.toString());
            } else if(value != null){
                stringParams.put(key, value.toString());
            }
        }
        return stringParams;
    }
}
