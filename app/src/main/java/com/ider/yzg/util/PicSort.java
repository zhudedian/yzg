package com.ider.yzg.util;

import com.ider.yzg.db.Picture;
import com.ider.yzg.db.TvApp;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eric on 2018/1/3.
 */

public class PicSort implements Comparator<Picture> {
    private Collator collator = Collator.getInstance(Locale.CHINA);

    public static void sort(List<Picture> list){
        Collections.sort(list,new PicSort());
    }

    @Override
    public int compare(Picture picture1 , Picture picture2){
        int value = collator.compare(String.valueOf(picture1.getPicTime()),String.valueOf(picture2.getPicTime()));
        if (value<0){
            return 1;
        }else if (value>0){
            return -1;
        }else {
            return 1;
        }
    }
}
