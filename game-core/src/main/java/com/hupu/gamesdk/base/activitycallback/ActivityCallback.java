package com.hupu.gamesdk.base.activitycallback;

import android.content.Intent;

public interface ActivityCallback {

    void onActivityResult(int resultCode, Intent data);
}