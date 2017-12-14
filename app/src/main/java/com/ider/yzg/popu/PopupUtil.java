package com.ider.yzg.popu;


import android.content.Context;
import android.view.View;

import com.ider.yzg.view.ConfirmPopu;

/**
 * Created by Eric on 2017/12/14.
 */

public class PopupUtil {

    private static ConfirmPopup confirmPopup;
    public static ConfirmPopup buildConfirmPopup(Context context, String titleStr, String noticeStr, String okStr,
                                               String cancelStr, boolean outsideTouchable, ConfirmPopu.OnOkListener listener){
        confirmPopup = new ConfirmPopup(context,titleStr,noticeStr,okStr,cancelStr,outsideTouchable,listener);
        return confirmPopup;
    }

    public static boolean isPopupShow(){
        if (confirmPopup!=null) {
            return confirmPopup.isShowing();
        }else {
            return false;
        }
    }
    public static void dismissPopup() {
        if(confirmPopup!=null&& confirmPopup.isShowing()){
            if (confirmPopup.isCancelable()) {
                confirmPopup.dismiss();
                confirmPopup= null;
            }
        }
    }
    public static void forceDismissPopup() {
        if(confirmPopup!=null&& confirmPopup.isShowing()){
            confirmPopup.dismiss();
        }
        confirmPopup= null;
    }
}
