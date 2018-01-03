package com.ider.yzg.popu;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.ider.yzg.R;
import com.ider.yzg.view.ConfirmPopu;

/**
 * Created by Eric on 2017/12/14.
 */

public class PopupUtil {

    private static ConfirmPopup confirmPopup;
    private static ProgressPopup progressPopup;
    private static EditPopup editPopup;
    private static ScreenPicPopup screenPicPopup;
    public static ConfirmPopup buildConfirmPopup(Context context, String titleStr, String noticeStr, String okStr,
                                               String cancelStr, boolean outsideTouchable, ConfirmPopu.OnOkListener listener){
        confirmPopup = new ConfirmPopup(context,titleStr,noticeStr,okStr,cancelStr,outsideTouchable,listener);
        return confirmPopup;
    }
    public static ConfirmPopup getDeleteConfirmPopup(Context context,String noticeStr,ConfirmPopu.OnOkListener listener){
        confirmPopup = new ConfirmPopup(context, context.getResources().getString(R.string.popup_remove_title),
                noticeStr, context.getResources().getString(R.string.popup_delete_ok_button),
                context.getResources().getString(R.string.popup_delete_cancel_button), true,listener);
        return confirmPopup;
    }
    public static ConfirmPopup getOverConfirmPopup(Context context,String noticeStr,String checkStr,ConfirmPopu.OnOkListener listener){
        confirmPopup = new ConfirmPopup(context, context.getResources().getString(R.string.popup_over_title),
                noticeStr,checkStr, context.getResources().getString(R.string.popup_ok_button),
                context.getResources().getString(R.string.popup_cancel_button), true,listener);
        confirmPopup.setAllCheckLinearVisible(View.VISIBLE);
        return confirmPopup;
    }
    public static ProgressPopup getUploadPopup(Context context,ProgressPopup.OnCancelListener listener){
        progressPopup = new ProgressPopup(context,context.getString(R.string.popup_upload_title),false,listener);
        return progressPopup;
    }
    public static ProgressPopup getDownloadPopup(Context context,ProgressPopup.OnCancelListener listener){
        progressPopup = new ProgressPopup(context,context.getString(R.string.popup_download_title),false,listener);
        return progressPopup;
    }
    public static ProgressPopup getCopyPopup(Context context,ProgressPopup.OnCancelListener listener){
        progressPopup = new ProgressPopup(context,context.getString(R.string.popup_copy_title),false,listener);
        return progressPopup;
    }
    public static ProgressPopup getMovePopup(Context context,ProgressPopup.OnCancelListener listener){
        progressPopup = new ProgressPopup(context,context.getString(R.string.popup_move_title),false,listener);
        return progressPopup;
    }
    public static ScreenPicPopup getScreenPicPopup(Context context){
        screenPicPopup = new ScreenPicPopup(context,true);
        return screenPicPopup;
    }
    public static EditPopup getEditPopup(Context context,String editStr,EditPopup.OnOkListener listener){
        editPopup = new EditPopup(context,context.getResources().getString(R.string.popup_ok_button),editStr,
                context.getResources().getString(R.string.popup_ok_button),
                context.getResources().getString(R.string.popup_cancel_button),true,listener);
        return editPopup;
    }
    public static void setDownloadTitle(String title){
        if (progressPopup!=null){
            progressPopup.setTitle(title);
        }
    }
    public static boolean isPopupShow(){
        if (confirmPopup!=null) {
            return confirmPopup.isShowing();
        }else if (progressPopup!=null) {
            return progressPopup.isShowing();
        }else if (screenPicPopup!=null) {

            return screenPicPopup.isShowing();
        }else {
            return false;
        }
    }
    public static void setFileName(String fileName){
        if (progressPopup!=null){
            progressPopup.setFileName(fileName);
        }
    }
    public static void setText(String noticeStr,String checkStr){
        if (confirmPopup!=null){
            confirmPopup.setText(noticeStr,checkStr);
        }
    }
    public static void setAllCheckVisible(int visible){
        if (confirmPopup!= null){
            confirmPopup.setAllCheckLinearVisible(visible);
        }
    }
    public static boolean isAllCheck(){
        if (confirmPopup!=null){
            return confirmPopup.isAllCheckSelect();
        }
        return false;
    }
    public static void update(long numBytes, long totalBytes,float parents,float speed){
        if (progressPopup!=null&&progressPopup.isShowing()){
            progressPopup.update(numBytes,totalBytes,parents,speed);
        }
    }
    public static void notifyDataChange(){
        if (screenPicPopup!=null){
            screenPicPopup.notifyDataChange();
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
        }else if(editPopup!=null&& editPopup.isShowing()){
            if (editPopup.isCancelable()) {
                editPopup.dismiss();
                editPopup= null;
            }
        }else if (screenPicPopup!=null&&screenPicPopup.isShowing()){
            if (screenPicPopup.isCancelable()){
                screenPicPopup.dismiss();
                screenPicPopup = null;
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

        }else if(editPopup!=null&& editPopup.isShowing()){
            editPopup.dismiss();
            editPopup= null;

        }

    }
}
