package org.cz.media.player.sample.sample.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cz.media.player.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020/9/9 4:16 PM
 * @email binigo110@126.com
 */
public class PlaybackListAdapter extends RecyclerView.Adapter<PlaybackListAdapter.ViewHolder> {
    private static final String TAG="VideoListAdapter";
    private List<PlaybackItem> videoList=new ArrayList<>();
    private RecyclerView recyclerView;
    private OnItemClickListener listener;
    private int currentSelectPosition;

    public PlaybackListAdapter(List<PlaybackItem> videoList) {
        if(null!=videoList){
            this.videoList.addAll(videoList);
        }
    }

    public List<PlaybackItem> getVideoList() {
        return videoList;
    }

    public void setSelectPosition(int position){
        if(currentSelectPosition!=position){
            int oldPosition=currentSelectPosition;
            currentSelectPosition=position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView=recyclerView;
    }

    @NonNull
    @Override
    public PlaybackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View parentView = layoutInflater.inflate(R.layout.video_play_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaybackListAdapter.ViewHolder holder, int position) {
        holder.itemView.setSelected(currentSelectPosition==position);
        holder.itemView.setOnClickListener(v -> {
            if(null!=listener){
                RecyclerView.ViewHolder selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(currentSelectPosition);
                if(null!=selectedViewHolder){
                    selectedViewHolder.itemView.setSelected(false);
                }
                holder.itemView.setSelected(true);
                int layoutPosition = holder.getLayoutPosition();
                listener.onItemClick(v,layoutPosition);
                currentSelectPosition=layoutPosition;
            }
        });
        holder.textView.setText(String.valueOf(position+1));
    }

    public PlaybackItem getItem(int position){
        return videoList.get(position);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
}
