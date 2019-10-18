package org.leaffun.system.display;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕显示
 */
public class DisplayUtil {

    /**
     * 在Activity-onCreate-setContentView之后执行
     * @param activity
     */
    public void setDisplay(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(activity.isInMultiWindowMode()) {
                DisplayManager mDisplayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays = mDisplayManager.getDisplays();
                int screenNum = displays.length;



                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
                WindowManager.LayoutParams p = activity.getWindow().getAttributes();
                float widthScale = (float) 9 / 16;


                p.width = (int) (dm.heightPixels * widthScale);
                activity.getWindow().setAttributes(p);

//                RuntimeConfig.WIDTH_PIXELS = p.width;
//                RuntimeConfig.HEIGHT_PIXELS = dm.heightPixels;

            }
        }
    }

}
