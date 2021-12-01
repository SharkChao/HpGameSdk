package com.hupu.gamesdk.base;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 加密参数拦截器
 * zhaowb
 */
public class SignInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        Request.Builder newRequestBuild=null;
        Request request = null ;
        ConcurrentHashMap<String, String> stringParams = new ConcurrentHashMap<>();
        if("POST".equals(original.method())) {
            RequestBody body = original.body();
            //表单
            if (body instanceof FormBody) {
                FormBody.Builder newFormBody = new FormBody.Builder();
                FormBody oidFormBody = (FormBody) original.body();
                for (int i = 0; i < oidFormBody.size(); i++) {
                    newFormBody.addEncoded(oidFormBody.encodedName(i), oidFormBody.encodedValue(i));
                    stringParams.put(oidFormBody.name(i), oidFormBody.value(i));
                    //Log.v("zwb","key:"+oidFormBody.encodedName(i)+"  value:"+oidFormBody.encodedValue(i));
                }
                newFormBody.add("sign", RequestParams.getSign(stringParams));
                requestBuilder.method(original.method(), newFormBody.build());
                request = requestBuilder.build();
                //非文本
            }else if(body instanceof MultipartBody){

                //文本
            }else {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(charset);
                    if (charset != null) {
                        //读取原请求参数内容
                        String requestParams = buffer.readString(charset);
                        try {
                            //重新拼凑请求体
                            JSONObject jsonObject;
                            if (TextUtils.isEmpty(requestParams)) {
                                jsonObject = new JSONObject();
                            }else {
                                jsonObject = new JSONObject(requestParams);
                            }
                            stringParams = RequestParams.jsonToParams(jsonObject);
                            jsonObject.put("sign", RequestParams.getSign(stringParams));
                            RequestBody newBody = RequestBody.create(body.contentType(), jsonObject.toString());
                            requestBuilder.post(newBody);
                            request = requestBuilder.build();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
        }else if("GET".equals(original.method())){
            HttpUrl.Builder commonParamsUrlBuilder = original.url().newBuilder();
            HttpUrl httpUrl = commonParamsUrlBuilder.build();
            Set<String> paramKeys = httpUrl.queryParameterNames();
            for (String key : paramKeys) {
                String value = httpUrl.queryParameter(key);
                stringParams.put(key, value);
            }
            commonParamsUrlBuilder.addEncodedQueryParameter("sign", RequestParams.getSign(stringParams));
            newRequestBuild = original.newBuilder()
                    .method(original.method(), original.body())
                    .url(commonParamsUrlBuilder.build());
            request = newRequestBuild.build();
        }
        return chain.proceed(request);
    }

}
