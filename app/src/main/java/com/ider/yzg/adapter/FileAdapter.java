package com.ider.yzg.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import java.util.List;

/**
 * Created by Eric on 2017/8/29.
 */

public class FileAdapter extends ArrayAdapter<BoxFile> {

    private int resourceId;
    private List<BoxFile> selectFiles;
    public FileAdapter(Context context, int textViewResourceId, List<BoxFile> objects, List<BoxFile> selects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
        selectFiles = selects;
    }
    @Override
    public View getView(int posetion, View convertView, ViewGroup parent){
        final BoxFile boxFile = getItem(posetion);
        View view;
        FileAdapter.ViewHolder viewHolder;
        if (convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder =new FileAdapter.ViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.file_name);
            viewHolder.size = (TextView)view.findViewById(R.id.file_size);
            viewHolder.checkBox = (CheckBox)view.findViewById(R.id.checkbox);
            viewHolder.draw = (ImageView)view.findViewById(R.id.file_image);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (FileAdapter.ViewHolder) view.getTag();
        }
        viewHolder.name.setText(boxFile.getFileName());
//        viewHolder.name.setMovementMethod(ScrollingMovementMethod.getInstance());
        viewHolder.size.setText(boxFile.getFileSize());
        if (boxFile.getFileType()==1){
            viewHolder.size.setVisibility(View.GONE);
            viewHolder.draw.setImageResource(R.drawable.item_dir);
        }else if (boxFile.getFileType()==2){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_video);
        }else if (boxFile.getFileType()==3){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_music);
        }else if (boxFile.getFileType()==4){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_photo);
        }else if (boxFile.getFileType()==5){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_apk);
        }else if (boxFile.getFileType()==6){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_zip);
        }else if (boxFile.getFileType()==7){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_pdf);
        }else if (boxFile.getFileType()==8){
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_file);
        }else {
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.draw.setImageResource(R.drawable.item_default);
        }
        if (MyData.isShowCheck){
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            if (selectFiles.contains(boxFile)){
                viewHolder.checkBox.setChecked(true);
            }else {
                viewHolder.checkBox.setChecked(false);
            }
        }else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("ischecked",isChecked+"");
//                if(isChecked){
//                    selectFiles.add(boxFile);
//                }
            }
        });
        return view;
    }
    class ViewHolder{
        ImageView draw;
        TextView name,size;
        CheckBox checkBox;
    }
}
