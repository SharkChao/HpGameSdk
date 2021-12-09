package com.hupu.gamesdk.base

import android.content.Context


/**
 *
 * 资源反射类
 *  * getViewId - 获取控件id
 *  * getLayoutId - 获取布局id
 *  * getStringId - 获取字符串id
 *  * getDrawableId - 获取图片资源id
 *  * getStyleId - 获取样式id
 *  * getDimenId - 获取尺寸id
 *  * getArrayId - 获取数组资源id
 *  * getColorId - 获取颜色id
 *  * getAnimId - 获取动画资源id
 *  * isClassFounded - 判断类是否存在
 *  * getObjectByClassName - 根据类名获取对象
 */
object ReflectUtil {
    private fun getResourceId(context: Context, name: String, type: String): Int {
        var id = 0
        id = context.resources.getIdentifier(name, type, context.packageName)
        return id
    }

    fun getMipmapId(context: Context, name: String): Int {
        return getResourceId(context, name, "mipmap")
    }

    fun getViewId(context: Context, name: String): Int {
        return getResourceId(context, name, "id")
    }

    fun getLayoutId(context: Context, name: String): Int {
        return getResourceId(context, name, "layout")
    }

    fun getStringId(context: Context, name: String): Int {
        return getResourceId(context, name, "string")
    }

    fun getDrawableId(context: Context, name: String): Int {
        return getResourceId(context, name, "drawable")
    }

    fun getStyleId(context: Context, name: String): Int {
        return getResourceId(context, name, "style")
    }

    fun getDimenId(context: Context, name: String): Int {
        return getResourceId(context, name, "dimen")
    }

    fun getArrayId(context: Context, name: String): Int {
        return getResourceId(context, name, "array")
    }

    fun getColorId(context: Context, name: String): Int {
        return getResourceId(context, name, "color")
    }

    fun getAnimId(context: Context, name: String): Int {
        return getResourceId(context, name, "anim")
    }

    fun isClassFounded(className: String?): Boolean {
        return try {
            val aClass = Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getObjectByClassName(className: String?): Any? {
        try {
            val aClass = Class.forName(className)
            return aClass.newInstance()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }
}