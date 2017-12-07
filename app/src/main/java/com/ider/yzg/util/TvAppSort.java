package com.ider.yzg.util;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.TvApp;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eric on 2017/12/7.
 */

public class TvAppSort implements Comparator<TvApp> {
    private Collator collator = Collator.getInstance(Locale.CHINA);

    public static void sort(List<TvApp> list){
        Collections.sort(list,new TvAppSort());
    }

    @Override
    public int compare(TvApp tvApp1 , TvApp tvApp2){
        int value = collator.compare(String.valueOf(tvApp1.getType()),String.valueOf(tvApp2.getType()));
        if (value<0){
            return 1;
        }else if (value>0){
            return -1;
        }else {
            int value2 = collator.compare(tvApp1.getLabel(),tvApp2.getType());
            if (value>0){
                return 1;
            }else if (value<0){
                return -1;
            }
            return value2;
        }
    }
}
