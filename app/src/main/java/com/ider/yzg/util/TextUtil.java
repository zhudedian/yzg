package com.ider.yzg.util;

import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by Eric on 2018/1/2.
 */

public class TextUtil {
    public static String autoSplitText(final TextView tv, final String indent) {
        final String rawText = tv.getText().toString(); //原始文本
        final Paint tvPaint = tv.getPaint(); //paint，包含字体等信息
        final float tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); //控件可用宽度
        //将缩进处理成空格
        String indentSpace = "";
        float indentWidth = 0;
        if (!TextUtils.isEmpty(indent)) {
            float rawIndentWidth = tvPaint.measureText(indent);
            if (rawIndentWidth < tvWidth) {
                while ((indentWidth = tvPaint.measureText(indentSpace)) < rawIndentWidth) {
                    indentSpace += " ";
                }
            }
        }

        //将原始文本按行拆分
        String [] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine);
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    //从手动换行的第二行开始，加上悬挂缩进
                    if (lineWidth < 0.1f && cnt != 0) {
                        sbNewText.append(indentSpace);
                        lineWidth += indentWidth;
                    }
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }
        //把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }
        tv.setText(sbNewText.toString());
        return sbNewText.toString();
    }
}
