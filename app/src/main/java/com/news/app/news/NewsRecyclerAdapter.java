package com.news.app.news;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.app.news.entities.Article;
import com.squareup.picasso.Picasso;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.VH> {

    Article[] articles;

    public NewsRecyclerAdapter(Article[] articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        vh.bindData(articles[i]);
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.length : 0;
    }

    class VH extends RecyclerView.ViewHolder {

        TextView title, author, description;
        ImageView imageView;

        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.image);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);
        }

        public void bindData(Article article) {
            title.setText(article.title);
            author.setText(article.author);
            description.setText(article.description);

            if(TextUtils.isEmpty(article.urlToImage)){
                imageView.setVisibility(View.INVISIBLE);
            } else {
                Picasso.get().load(article.urlToImage).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

}
