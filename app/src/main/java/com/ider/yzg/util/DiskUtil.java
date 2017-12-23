package com.ider.yzg.util;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2017/12/23.
 */

public class DiskUtil {
    private static String TAG = "DiskUtil";
    public static List<StorageInfo> listAllStorage() {
        ArrayList<StorageInfo> storages = new ArrayList<StorageInfo>();
        StorageManager storageManager = (StorageManager) MyApplication.getContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path);

                    Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                    String state = (String) getVolumeState.invoke(storageManager, info.path);
                    info.state = state;

                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();

                    Method getUserLabel = obj.getClass().getMethod("getUserLabel", new Class[0]);
                    String label = (String) getUserLabel.invoke(obj, new Object[0]);
                    info.label = label;

//                    Method getMaxFileSize = obj.getClass().getMethod("getMaxFileSize", new Class[0]);
//                    long size = (long) getMaxFileSize.invoke(obj, new Object[0]);
//                    info.size = size;
                    storages.add(info);
                    Log.i(TAG, info.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    public static List<StorageInfo> getAvaliableStorage(){
        List<StorageInfo> infos = listAllStorage();
        List<StorageInfo> storages = new ArrayList<StorageInfo>();
        for(StorageInfo info : infos){
            File file = new File(info.path);
            if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                if (info.isMounted()) {
                    info.size = file.getTotalSpace();
                    info.avaSize = file.getFreeSpace();
                    storages.add(info);
                    Log.i(TAG, "getAvaliableStorage="+info.toString());
                }
            }
        }
//        checkAmlogic6Usb();
        return storages;
    }
    public static class StorageInfo {
        public String path;
        public String state;
        public String label;
        public long size;
        public long avaSize;
        public boolean isRemoveable;
        public StorageInfo(String path) {
            this.path = path;
        }
        public boolean isMounted() {
            return "mounted".equals(state);
        }
        @Override
        public String toString() {
            return "StorageInfo [path=" + path + ", state=" + state
                    + ", isRemoveable=" + isRemoveable +", label="+label +", size="+FileUtil.getSize(size) +"]";
        }
    }
}
