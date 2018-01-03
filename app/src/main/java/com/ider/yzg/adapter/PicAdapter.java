package com.ider.yzg.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ider.yzg.R;
import com.ider.yzg.db.MyData;
import com.ider.yzg.db.Picture;
import com.ider.yzg.popu.ScreenPicPopup;
import com.ider.yzg.util.PicUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Eric on 2018/1/2.
 */

public class PicAdapter extends RecyclerView.Adapter <PicAdapter.ViewHolder>{


    List<Picture> pictureList;
    Picture opPicture;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View picView;
        ImageView imageView;
        TextView textView;
        LinearLayout opLinear,share,delete;
        public ViewHolder(View view){
            super(view);
            picView = view;
            imageView = (ImageView)view.findViewById(R.id.image);
            textView = (TextView)view.findViewById(R.id.text);
            opLinear = (LinearLayout)view.findViewById(R.id.op_linear);
            share = (LinearLayout)view.findViewById(R.id.share);
            delete = (LinearLayout)view.findViewById(R.id.delete);
        }
    }
    public PicAdapter(List<Picture> list){
        pictureList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Picture picture = pictureList.get(position);
                if (opPicture != null && opPicture.equals(picture)) {
                    if (picture.isShowOp()) {
                        picture.setShowOp(false);
                    } else {
                        picture.setShowOp(true);
                    }
                    opPicture = picture;
                } else {
                    if (opPicture != null) {
                        opPicture.setShowOp(false);
                    }
                    opPicture = picture;
                    picture.setShowOp(true);
                }
                notifyDataSetChanged();
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Picture picture = pictureList.get(position);
                String path = picture.getPicPath();
                File file = new File(path);
                Intent imageIntent = new Intent(Intent.ACTION_SEND);
                imageIntent.setType("image/png");
//                imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                context.startActivity(Intent.createChooser(imageIntent, "分享"));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Picture picture = pictureList.get(position);
                String path = picture.getPicPath();
                File file = new File(path);
                if (file.exists()){
                    file.delete();
                }
                notifyDataChange();
            }
        });
        return holder;
    }

    public void notifyDataChange(){
        pictureList = PicUtil.getScreenShotPic();
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Picture picture = pictureList.get(position);
        if (picture.isShowOp()){
            holder.opLinear.setVisibility(View.VISIBLE);
        }else {
            holder.opLinear.setVisibility(View.GONE);
        }
        Glide.with(context).load(picture.getPicPath()).into(holder.imageView);
        holder.textView.setText(picture.getPicName());
    }
    @Override
    public int getItemCount(){
        return pictureList.size();
    }
}
