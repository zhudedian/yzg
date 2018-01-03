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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.util.FileUtil;
import com.ider.yzg.view.ItemOpMenu;

import java.util.List;

/**
 * Created by Eric on 2017/8/29.
 */

public class FileAdapter extends ArrayAdapter<BoxFile> {

    private Context context;

    private OnMenuOpClickListener listener;
    private int resourceId;
    private List<BoxFile> selectFiles;
    public FileAdapter(Context context, int textViewResourceId, List<BoxFile> objects, List<BoxFile> selects){
        super(context,textViewResourceId,objects);
        this.context = context;
        resourceId = textViewResourceId;
        selectFiles = selects;
    }
    @Override
    public View getView(int posetion, View convertView, ViewGroup parent){
        View view;
        try {


        final BoxFile boxFile = getItem(posetion);
        FileAdapter.ViewHolder viewHolder;
        if (convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder =new FileAdapter.ViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.file_name);
            viewHolder.path = (TextView)view.findViewById(R.id.file_path);
            viewHolder.size = (TextView)view.findViewById(R.id.file_size);
            viewHolder.checkBox = (CheckBox)view.findViewById(R.id.checkbox);
            viewHolder.draw = (ImageView)view.findViewById(R.id.file_image);
            viewHolder.more_op = (ImageView)view.findViewById(R.id.item_menu_op) ;
            viewHolder.linearOp = (LinearLayout)view.findViewById(R.id.linear_menu);
            viewHolder.trans = (ItemOpMenu)view.findViewById(R.id.item_trans);
            viewHolder.move = (ItemOpMenu)view.findViewById(R.id.item_move);
            viewHolder.rename = (ItemOpMenu)view.findViewById(R.id.item_rename);
            viewHolder.remove = (ItemOpMenu)view.findViewById(R.id.item_remove);
            viewHolder.copy = (ItemOpMenu)view.findViewById(R.id.item_copy);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (FileAdapter.ViewHolder) view.getTag();
        }
        viewHolder.name.setText(boxFile.getFileName());
//        viewHolder.name.setMovementMethod(ScrollingMovementMethod.getInstance());
        viewHolder.path.setText(FileUtil.getSTime(boxFile.getCreateTime()));
        viewHolder.size.setText(FileUtil.getSize(boxFile.getFileSize()));
            if (MyData.disPlayMode.equals(MyData.NORMAL)&&!MyData.isShowCheck) {
                viewHolder.more_op.setVisibility(View.VISIBLE);
            }else {
                viewHolder.more_op.setVisibility(View.GONE);
            }
            if (boxFile.getFileType()==0){
                viewHolder.path.setText("可用："+FileUtil.getSize(boxFile.getCreateTime()));
                viewHolder.size.setText("共："+FileUtil.getSize(boxFile.getFileSize()));
                viewHolder.draw.setImageResource(R.drawable.item_usb);
                viewHolder.more_op.setVisibility(View.GONE);
            }
        else if (boxFile.getFileType()==1){
            if (boxFile.getFileCount()==0){
                viewHolder.size.setText(context.getResources().getString(R.string.empty_dir));
            }else {
                viewHolder.size.setText(boxFile.getFileCount() + context.getResources().getString(R.string.file_count_end));
            }
            if (resourceId==R.layout.file_list_item){
                viewHolder.draw.setImageResource(R.drawable.item_dir);
                viewHolder.move.setVisibility(View.GONE);
            }else {
                viewHolder.move.setVisibility(View.VISIBLE);
                viewHolder.draw.setImageResource(R.drawable.item_tvdir);
            }
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
        if (boxFile.isOpenOp()){
            viewHolder.linearOp.setVisibility(View.VISIBLE);
        }else {
            viewHolder.linearOp.setVisibility(View.GONE);
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
        setListener(viewHolder,boxFile);
        }catch (Exception e){
            e.printStackTrace();
            view = convertView;
        }
        return view;
    }
    private void setListener(FileAdapter.ViewHolder viewHolder,final BoxFile boxFile){
        viewHolder.more_op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuClick(boxFile,true,false,false,false,false,false);
            }
        });
        viewHolder.move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuClick(boxFile,false,false,false,false,true,false);
            }
        });
        viewHolder.trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuClick(boxFile,false,false,false,false,false,true);
            }
        });
        viewHolder.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuClick(boxFile,false,false,true,false,false,false);
            }
        });
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuClick(boxFile,false,false,false,true,false,false);
            }
        });
        viewHolder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuClick(boxFile,false,true,false,false,false,false);
            }
        });

    }

    class ViewHolder{
        ImageView draw,more_op;
        TextView name,size,path;
        LinearLayout linearOp;
        ItemOpMenu trans,move,rename,remove,copy;
        CheckBox checkBox;
    }
    public interface OnMenuOpClickListener{
        void menuClick(BoxFile boxFile,boolean isOpOpen,boolean isCopy, boolean isRename, boolean isRemove,
                       boolean isMove, boolean isTrans);
    }
    public void setOnMenuOpClickListener(FileAdapter.OnMenuOpClickListener clickListener){
        this.listener = clickListener;
    }
}
