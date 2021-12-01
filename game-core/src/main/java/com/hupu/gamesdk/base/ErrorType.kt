package com.hupu.gamesdk.base

enum class ErrorType(val code: Int,val msg: String) {
    AppNotLegal(-1,"App认证失败，appId或appKey不正确"),
    PayFail(-2,"充值失败"),
    LoginNotInstallHp(-3,"手机没有安装虎扑app"),
    LoginHpNotSupportSchema(-4,"虎扑版本过低，不支持授权登陆"),
    LoginResultError(-5,"虎扑授权页面返回信息错误"),
    LoginCancel(-6,"用户取消授权"),
    LoginNetError(-7,"授权登陆接口异常"),

    AppAuthError(-8,"App认证接口调用失败"),
    CertificationFail(-9,"实名认证失败"),
    Certificationing(-10,"实名认证中"),
    Immaturity(-11,"触发未成年保护"),
    SecretReject(-12,"同意隐私协议后才可进入游戏")
}