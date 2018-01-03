package com.ider.yzg.util;

import com.ider.yzg.db.MyData;
import com.ider.yzg.db.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Eric on 2018/1/3.
 */

public class PicUtil {

    public static List<Picture> getScreenShotPic(){
        List<Picture> list = new ArrayList<>();
        File file = new File(MyData.screenshotSavePath);
        if (file.exists()){
            File[] files = file.listFiles();
            if (files!=null){
                for (File file1:files){
                    if (FileUtil.isImage(file1)){
                        Picture picture = new Picture(file1.getName(),file1.getPath(),FileUtil.getLTime(file1));
                        list.add(picture);
                    }
                }
            }
        }
        PicSort.sort(list);
        return list;
    }
}
