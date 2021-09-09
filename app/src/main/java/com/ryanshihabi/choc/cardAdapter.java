package com.ryanshihabi.choc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.ViewHolder>{
    private final List<cardItem> cardItems;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final ImageView imageView;
        //private final VideoView videoView

        public ViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.cardTitle);
            imageView = (ImageView) view.findViewById(R.id.cardImage);
            //videoView = (VideoView) view.findViewById(R.id.cardVideo);
        }
    }

    public cardAdapter(List<cardItem> items){
        cardItems = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        viewHolder.textView.setText(cardItems.get(position).title);
        viewHolder.imageView.setImageResource(cardItems.get(position).image);
        //viewHolder.videoView.setResourceUri(cardItems.get(position).video);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public int getItemCount(){
        return cardItems.size();
    }
}
