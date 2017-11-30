package com.ider.yzg.util;


import com.ider.yzg.db.BoxFile;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eric on 2017/9/6.
 */

public class ListSort implements Comparator<BoxFile> {
    private Collator collator = Collator.getInstance(Locale.CHINA);

    public static void sort(List<BoxFile> list){
        Collections.sort(list,new ListSort());
    }

    @Override
    public int compare(BoxFile boxFile1 , BoxFile boxFile2){
        int value = collator.compare(String.valueOf(boxFile1.getFileType()),String.valueOf(boxFile2.getFileType()));
        if (value>0){
            return 1;
        }else if (value<0){
            return -1;
        }else {
            int value2 = collator.compare(boxFile1.getFileName(),boxFile2.getFileName());
            return value2;
        }
    }
}
