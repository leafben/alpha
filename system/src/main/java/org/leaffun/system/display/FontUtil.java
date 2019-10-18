package org.leaffun.system.display;

import android.app.Activity;
import android.content.res.Resources;

/**
 * 字体
 */
public class FontUtil {

    /**
     * 设置sp单位的字体缩放系数
     * 在Activity-getResource()之前使用
     * @param activity 要缩放的Activity
     * @param fontScale 1.0表示不缩放
     */
    public void setFontScale(Activity activity,float fontScale) {
        Resources resources = activity.getResources();
        if (resources != null && resources.getConfiguration().fontScale != fontScale) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = fontScale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
    }
}
