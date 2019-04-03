package com.example.cricket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.cricket.Model.VideoModel;
import com.example.cricket.PlayVideoActivity;
import com.example.cricket.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    ArrayList<VideoModel> arrayListVideos;
    Activity activity;
    String [] toDelete;
    public VideoAdapter(Context context, ArrayList<VideoModel> arrayListVideos, Activity activity) {

        this.context = context;
        this.arrayListVideos = arrayListVideos;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(context).load("file://" + arrayListVideos.get(position).getStr_thumb())
                .skipMemoryCache(false)
                .into(holder.imageView);
        holder.r1_select.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder.r1_select.setAlpha(0);

        holder.r1_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PlayVideoActivity.class);
                i.putExtra("video", arrayListVideos.get(position).getStr_path());
                activity.startActivity(i);
            }
        });
        holder.r1_select.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Default camera is USB, use front camera?");
                builder.setIcon(R.drawable.exo_icon_next);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        save_video(arrayListVideos.get(position).getStr_path());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RelativeLayout r1_select;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            r1_select = itemView.findViewById(R.id.r1_select);
        }
    }

    private void save_video(String fileName) {

        String outpath = Environment.getExternalStorageDirectory().toString()+"/Movies/Cricket/saved";
        String outfileName = fileName.replace("raw", "saved");

        //create output directory if it doesn't exist
        File dir = new File (outpath);
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(fileName);
            out = new FileOutputStream(outfileName);
            byte[] buffer = new byte[1024];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(fileName).delete();

            //
            MediaScannerConnection.scanFile(context, new String[] { fileName }, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
