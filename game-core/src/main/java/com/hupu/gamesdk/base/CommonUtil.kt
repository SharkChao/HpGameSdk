package com.hupu.gamesdk.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.annotation.Dimension
import android.support.annotation.Px
import android.text.TextUtils
import android.util.Base64
import android.util.TypedValue
import com.alipay.sdk.app.PayTask
import com.hupu.gamesdk.pay.alipay.AliPayResult
import java.nio.charset.Charset
import java.text.DecimalFormat
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


internal class CommonUtil {

    companion object {

        /** 加密key  */
        private var PASSWORD_ENC_SECRET = "hupu-aes-authkey"
        /** 加密算法 */
        private const val KEY_ALGORITHM = "AES"
        /** 字符编码 */
        private val CHARSET = Charset.forName("UTF-8")
        /** 加解密算法/工作模式/填充方式 */
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding"

        /**
         * 对字符串加密
         * @param data  源字符串
         * @return  加密后的字符串
         */
        fun String.encrypt(): String {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val byteArray = PASSWORD_ENC_SECRET.toByteArray(CHARSET)
            val keySpec = SecretKeySpec(byteArray, KEY_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(byteArray))
            val encrypted = cipher.doFinal(this.toByteArray(CHARSET))
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        }

        /**
         * 对字符串解密
         * @param data  已被加密的字符串
         * @return  解密得到的字符串
         */
        fun String.decrypt(): String {
            val encrypted = Base64.decode(this.toByteArray(CHARSET), Base64.NO_WRAP)
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val byteArray = PASSWORD_ENC_SECRET.toByteArray(CHARSET)
            val keySpec = SecretKeySpec(byteArray, KEY_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(byteArray))
            val original = cipher.doFinal(encrypted)
            return String(original, CHARSET)
        }

        fun isAppInstalled(context: Context, packageName: String): Boolean {
            var isInstalled = false
            try {
                val packageManager = context.packageManager
                val pInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_GIDS)
                isInstalled = pInfo != null
            }catch (e: Exception){
                e.printStackTrace()
            }
            return isInstalled
        }

        /**
         * 校验身份证
         * @param v [String]
         * @return [Boolean]
         * */
        fun checkIsIDCard (v: String) : Boolean {
            return Regex("""^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}${'$'})|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])${'$'})${'$'}""").matches(v)
        }

        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            val display = context.resources.displayMetrics
            return display.widthPixels
        }
        @JvmStatic
        fun getScreenHeight(context: Context): Int {
            val display = context.resources.displayMetrics
            return display.heightPixels
        }

        @SuppressLint("QueryPermissionsNeeded")
        fun isAppInstalled2(context: Context, packageName: String): Boolean {
            val packageManager = context.packageManager
            // 获取所有已安装程序的包信息
            val pInfo = packageManager.getInstalledPackages(0)
            for (i in pInfo.indices) {
                // 循环判断是否存在指定包名
                if (pInfo[i].packageName.equals(packageName, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }

        /**
         * 去市场下载页面
         */
        fun goToMarket(context: Context, packageName: String) {
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                context.startActivity(goToMarket)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @Px
        fun Context.dp2px(@Dimension(unit = Dimension.DP) dpVal: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal, this.resources.displayMetrics
            )
        }

        private fun format(number: Double): String {
            return DecimalFormat("#.##").format(number) ;

        }

        fun fenToYuan(fen: Int): String {
            return format(fen/100.0)
        }

        /**
         * 获取应用程序名称
         */
        fun getAppName(context: Context): String? {
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                context.resources.getString(packageInfo.applicationInfo.labelRes)
            }catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }


        fun alipay(activity: Activity, payUrl: String?, callback: (success: Boolean) -> Unit) {
            if (payUrl == null) {
                callback.invoke(false)
                return
            }

            ExecutorManager.instance.ioExecutor.execute {
                val alipay = PayTask(activity)
                val result = alipay.payV2(payUrl, true)
                val payResult = AliPayResult(result)
                ExecutorManager.instance.mainExecutor.execute {
                    if (TextUtils.equals(payResult.resultStatus, "9000")) {
                        callback.invoke(true)
                    } else {
                        callback.invoke(false)
                    }
                }
            }
        }
    }
}