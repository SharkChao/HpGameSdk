package com.hupu.gamesdk.pay.entity

import java.io.Serializable

internal class HpPayEntity: Serializable {
    //商品名称
    var productName: String? = null
    //商品描述信息
    var productDesc: String? = null
    //游戏方订单号
    var gameTradeNo: String? = null
    //本次交易金额，单位：分（注意，total_fee的值必须为整数，并且在1~100000之间)
    var totalFee: Int = 0
    //游戏角色id
    var roleId: String? = null
    //区服id
    var serverId: String? = null

    //签名，服务端需要校验
    var sign: String? = null

    //puid
    var puid: String? = null
    //appid
    var appid: String? = null
}