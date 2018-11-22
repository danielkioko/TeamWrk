package com.versusdeveloper.blogr.ImagePosts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.versusdeveloper.blogr.R;

public class BlogzoneViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public BlogzoneViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.tvTopic);
            post_title.setText(title);
        }

        public void setDesc(String desc){
            TextView post_desc = mView.findViewById(R.id.tvDescription);
            post_desc.setText(desc);
        }

        public void setImageUrl(Context ctx, String imageUrl){
            ImageView post_image = mView.findViewById(R.id.ivPicture);
            Picasso.with(ctx).load(imageUrl).into(post_image);
        }
    }