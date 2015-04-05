package us.pinguo.album;

import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by zhouwei on 14-10-9.
 */
public class PopWindowUtils {
    /**
     *

     * @param w  pop的宽
     * @param h pop的高
     * @param view
     * @return
     */
    public static PopupWindow getPopWindowInstance( int w, int h, View view){
        View popView =view;
        PopupWindow pop = new PopupWindow(popView);
        pop.setHeight(h);
        pop.setWidth(w);
       // pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        return pop;
    }
}
