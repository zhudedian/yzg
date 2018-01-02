package com.ider.yzg.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.db.Picture;

import java.util.List;

/**
 * Created by Eric on 2018/1/2.
 */

public class PicAdapter extends RecyclerView.Adapter <PicAdapter.ViewHolder>{

    List<Picture> pictureList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            textView = (TextView)view.findViewById(R.id.text);
        }
    }
    public PicAdapter(List<Picture> list){
        pictureList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Picture picture = pictureList.get(position);
//        holder.imageView.setImageResource();
    }
    @Override
    public int getItemCount(){
        return pictureList.size();
    }
}
