package com.ider.yzg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.db.ApkFile;

import java.util.List;

/**
 * Created by Eric on 2017/8/26.
 */

public class ApkAdapter extends ArrayAdapter<ApkFile> {
    private String TAG = "ApkAdapter";
    private int resourceId;
    private Context context;
    private OnApkInstallClickListener listener;
    public ApkAdapter(Context context, int textViewResourceId, List<ApkFile> objects){
        super(context,textViewResourceId,objects);
        this.context = context;
        resourceId = textViewResourceId;

    }
    @Override
    public View getView(int posetion, View convertView, ViewGroup parent){
        final ApkFile apkFile = getItem(posetion);
        View view;
        ApkAdapter.ViewHolder viewHolder;
        if (convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder =new ApkAdapter.ViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.title);
            viewHolder.path = (TextView)view.findViewById(R.id.text);
            viewHolder.size = (TextView)view.findViewById(R.id.text2);
            viewHolder.state = (TextView)view.findViewById(R.id.apk_state);
            viewHolder.draw = (ImageView)view.findViewById(R.id.image);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ApkAdapter.ViewHolder) view.getTag();
        }
        viewHolder.name.setText(apkFile.getFileName());
        viewHolder.path.setText(apkFile.getFilePath());
        viewHolder.size.setText(apkFile.getLabel()+"("+context.getResources().getString(R.string.version)+apkFile.getVersionName()+")"+apkFile.getFileSize());
//        Log.i(TAG, apkFile.getFileName()+"apkFile.getInstallLevel()="+apkFile.getInstallLevel());
        if (apkFile.getInstallLevel()==0){
            viewHolder.state.setText(context.getString(R.string.old_version));
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.apk_state_grey));
            viewHolder.state.setBackgroundResource(R.drawable.apk_state_grey);

        }else if (apkFile.getInstallLevel()==1){
            viewHolder.state.setText(context.getString(R.string.installed));
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.apk_state_grey));
            viewHolder.state.setBackgroundResource(R.drawable.apk_state_grey);

        }else if (apkFile.getInstallLevel()==2){
            viewHolder.state.setText(context.getString(R.string.update));
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.apk_state_yellow));
            viewHolder.state.setBackgroundResource(R.drawable.apk_state_yellow);
        }else {
            viewHolder.state.setText(context.getString(R.string.installing));
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.apk_state_yellow));
            viewHolder.state.setBackgroundResource(R.drawable.apk_state_yellow);
        }
        if (apkFile.getApkDraw()!=null){
            viewHolder.draw.setImageDrawable(apkFile.getApkDraw());
        }
        viewHolder.state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                listener.installClick(apkFile);
            }
        });
        return view;
    }
    class ViewHolder{
        ImageView draw;
        TextView name,path,size,state;
    }
    public interface OnApkInstallClickListener{
        void installClick(ApkFile apkFile);
    }
    public void setOnApkInstallClickListener(OnApkInstallClickListener onApkInstallClickListener){
        this.listener = onApkInstallClickListener;
    }
}
