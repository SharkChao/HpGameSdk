package com.hupu.gamesdk.report

import android.support.annotation.Keep

@Keep
class HpReportEntity {
    //设备平台(IOS/Android)
    var os: String? = null
    //系统版本
    var osv: String? = null
    //sdk版本
    var sv: String? = null
    //app版本
    var av: String? = null
    //appid
    var aid: String? = null
    //puid
    var puid: String? = null

    //唯一标示
    var clt: String? = null
    //设备制造商，手机品牌
    var mfrs: String? = null
    //手机型号
    var model: String? = null

    //时区；
    var tz: String? = null
    //时间戳(毫秒)
    var et: Long = 0

    //业务类型
    var type: String? = null
    //业务数据
    var pdata: HashMap<String,Any?>? = null
    override fun toString(): String {
        return "HpReportEntity(os=$os, osv=$osv, sv=$sv, av=$av, aid=$aid, puid=$puid, clt=$clt, mfrs=$mfrs, model=$model, tz=$tz, et=$et, type=$type, pdata=$pdata)"
    }


}