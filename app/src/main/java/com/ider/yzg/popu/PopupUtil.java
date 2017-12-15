package com.ider.yzg.popu;


import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.ider.yzg.R;
import com.ider.yzg.view.ConfirmPopu;

import static android.R.attr.id;

/**
 * Created by Eric on 2017/12/14.
 */

public class PopupUtil {

    private static ConfirmPopup confirmPopup;
    private static ProgressPopup progressPopup;
    public static ConfirmPopup buildConfirmPopup(Context context, String titleStr, String noticeStr, String okStr,
                                               String cancelStr, boolean outsideTouchable, ConfirmPopu.OnOkListener listener){
        confirmPopup = new ConfirmPopup(context,titleStr,noticeStr,okStr,cancelStr,outsideTouchable,listener);
        return confirmPopup;
    }
    public static ProgressPopup getUploadPopup(Context context,ProgressPopup.OnCancelListener listener){
        progressPopup = new ProgressPopup(context,context.getString(R.string.popup_upload_title),false,listener);
        return progressPopup;
    }
    public static boolean isPopupShow(){
        if (confirmPopup!=null) {
            return confirmPopup.isShowing();
        }else if (progressPopup!=null) {
            return progressPopup.isShowing();
        }else {
            return false;
        }
    }
    public static void update(long numBytes, long totalBytes,float parents,float speed){
        if (progressPopup!=null&&progressPopup.isShowing()){
            progressPopup.update(numBytes,totalBytes,parents,speed);
        }
    }
    public static void dismissPopup() {
        if(confirmPopup!=null&& confirmPopup.isShowing()){
            if (confirmPopup.isCancelable()) {
                confirmPopup.dismiss();
                confirmPopup= null;
            }
        }else if(progressPopup!=null&& progressPopup.isShowing()){
            if (progressPopup.isCancelable()) {
                progressPopup.dismiss();
                progressPopup= null;
            }
        }
    }
    public static void forceDismissPopup() {
        if(confirmPopup!=null&& confirmPopup.isShowing()){
            confirmPopup.dismiss();
            confirmPopup= null;
        }else if(progressPopup!=null&& progressPopup.isShowing()){
            progressPopup.dismiss();
            progressPopup= null;

        }

    }
}
