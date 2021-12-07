package com.hupu.gamesdk.base.activitycallback;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

public class OnActResultEventDispatcherFragment extends Fragment {

    public static final String TAG = "on_act_result_event_dispatcher";
    public  int mRequestCode = 0x11;
    private SparseArray<ActivityCallback> mCallbacks = new SparseArray<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startForResult(Intent intent, ActivityCallback activityCallback) {
        mCallbacks.put(mRequestCode, activityCallback);
        startActivityForResult(intent, mRequestCode);
        mRequestCode++;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ActivityCallback activityCallback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);

        if (activityCallback != null) {
            activityCallback.onActivityResult(resultCode, data);
        }
    }
}