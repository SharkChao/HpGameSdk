package com.hupu.gamesdk.base

import android.content.Context
import java.lang.reflect.Field


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
    fun getStyleableIntArray(context: Context, name: String?): IntArray? {
        try {
            val fields: Array<Field> =
                Class.forName(context.packageName + ".R\$styleable").fields //.与$ difference,$表示R的子类
            for (field in fields) {
                if (field.name.equals(name)) {
                    return field.get(null) as IntArray?
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    /**
     * 遍历R类得到styleable数组资源下的子资源，1.先找到R类下的styleable子类，2.遍历styleable类获得字段值
     *
     * @param context
     * @param styleableName
     * @param styleableFieldName
     * @return
     */
    fun getStyleableFieldId(
        context: Context,
        styleableName: String,
        styleableFieldName: String
    ): Int {
        val className = context.packageName + ".R"
        val type = "styleable"
        val name = styleableName + "_" + styleableFieldName
        try {
            val cla = Class.forName(className)
            for (childClass in cla.classes) {
                val simpleName = childClass.simpleName
                if (simpleName == type) {
                    for (field in childClass.fields) {
                        val fieldName = field.name
                        if (fieldName == name) {
                            return field[null] as Int
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}