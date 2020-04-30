package com.example.android.rxvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoHolder> implements Filterable {
    @NonNull
    private Context context;
    private ArrayList<File> videoArrayList;
    public static ArrayList<File> videoArrayListFull;


    VideoAdapter(@NonNull Context context, ArrayList<File> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        videoArrayListFull = new ArrayList<>(videoArrayList);

    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder holder, int position) {

          holder.mFileName.setText(MainActivity.fileArrayList.get(position).getName());
        Glide.with(context) .asBitmap() .load( Uri.fromFile(new File(MainActivity.fileArrayList.get(position).getPath()))) . into(holder.mVideoThumbnail);
       holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExoPlayer.class);
                intent.putExtra("position", holder.getAdapterPosition());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(videoArrayList.size() > 0)
            return videoArrayList.size();
        else
            return 1;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<File> filteredList = new ArrayList();
            if(constraint == null || constraint.length() == 0)
                filteredList.addAll(videoArrayListFull);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (File item: videoArrayListFull)
                {
                    if(item.getName().toLowerCase().contains(filterPattern ))
                    {
                        filteredList.add(item);
                    }
                }
                if(filteredList.isEmpty())
                {
                    filteredList = new ArrayList<>(videoArrayListFull);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint , FilterResults results) {
            videoArrayList.clear();
            videoArrayList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
class VideoHolder extends RecyclerView.ViewHolder{
    TextView mFileName;
    ImageView mVideoThumbnail;
    RelativeLayout mItem;
    VideoHolder(View view)
    {
        super(view);
        mFileName = view.findViewById(R.id.video_file_name);
        mVideoThumbnail = view.findViewById(R.id.video_thumbnail);
        mItem = view.findViewById(R.id.video_cardView);
    }
}
