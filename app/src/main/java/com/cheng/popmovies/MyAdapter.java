package com.cheng.popmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016-10-02.
 * 先写的这种适配器，但结果在第一次无法加载出来，只有在第二次打开界面是才会出现图片，但最终结果应该不是适配器的问题
 */
public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Movie> movies;
    private Context context;

    public MyAdapter(){

    }
    public MyAdapter(Context context, ArrayList<Movie> movies) {
        super();
        inflater = LayoutInflater.from(context);
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != movies) {
            return movies.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       /* ViewHolder viewHolder;*/
        convertView = inflater.inflate(R.layout.grid_item, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_poster);
        if (imageView == null) {
            imageView = new ImageView(context);
        }
        String url = "http://image.tmdb.org/t/p/" + "w185/" + movies.get(position).getPoster_path();

        Picasso.with(context).load(url).into(imageView);
        Log.v("Picasso", "加载图片 " + url);
        //view.setImageResource(R.drawable.pic);
        /*if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.iv_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }*/

        /*String url = new String(("http://image.tmdb.org/t/p/" + "w185/" + movies.get(position).getPoster_path()).toString());
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185//9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg").into(viewHolder.image);*/
        //viewHolder.image.setImageResource(R.drawable.pic);
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
    }
}
