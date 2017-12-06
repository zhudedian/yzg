package com.ider.yzg.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.db.MyData;
import com.ider.yzg.db.TvApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Eric on 2017/12/5.
 */

public class AppsAdapter extends ArrayAdapter<TvApp> {
    private int resourceId;
    private Context context;
    private AppsAdapter.OnApkInstallClickListener listener;
    public AppsAdapter(Context context, int textViewResourceId, List<TvApp> objects){
        super(context,textViewResourceId,objects);
        this.context = context;
        resourceId = textViewResourceId;

    }
    @Override
    public View getView(int posetion, View convertView, ViewGroup parent){
        final TvApp tvApp = getItem(posetion);
        String label = tvApp.getLabel();
        String packageName = tvApp.getPackageName();
        View view;
        AppsAdapter.ViewHolder viewHolder;
        if (convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder =new AppsAdapter.ViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.title);
            viewHolder.path = (TextView)view.findViewById(R.id.text);
            viewHolder.vers = (TextView)view.findViewById(R.id.text2);
            viewHolder.state = (TextView)view.findViewById(R.id.apk_state);
            viewHolder.draw = (ImageView)view.findViewById(R.id.image);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (AppsAdapter.ViewHolder) view.getTag();
        }
        viewHolder.name.setText(tvApp.getLabel());
        viewHolder.path.setText(tvApp.getPackageName());
        viewHolder.vers.setText(context.getResources().getString(R.string.version)+tvApp.getVersionName());
        if (tvApp.isUninstalled()){
            viewHolder.state.setText(context.getString(R.string.uninstalled));
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.apk_state_grey));
            viewHolder.state.setBackgroundResource(R.drawable.apk_state_grey);
        }else {
            viewHolder.state.setText(context.getString(R.string.uninstall));
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.apk_state_yellow));
            viewHolder.state.setBackgroundResource(R.drawable.apk_state_yellow);
        }
        Bitmap bitmap = getLoacalBitmap(MyData.picIconSavePath+ File.separator+packageName+".jpg");
        viewHolder.draw.setImageBitmap(bitmap);

        viewHolder.state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                listener.installClick(tvApp);
            }
        });
        return view;
    }
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    class ViewHolder{
        ImageView draw;
        TextView name,path,vers,state;
    }
    public interface OnApkInstallClickListener{
        void installClick(TvApp tvApp);
    }
    public void setOnApkInstallClickListener(AppsAdapter.OnApkInstallClickListener onApkInstallClickListener){
        this.listener = onApkInstallClickListener;
    }
}
