package com.cheng.popmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by asus on 2016-10-04.
 * 依旧没有解决第二次才能加在出图片的问题
 */

public class MoviesAdapter extends ArrayAdapter<Movie> {

    public MoviesAdapter(Activity context, List<Movie> movieList) {
        super(context, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_poster);
        String url = "http://image.tmdb.org/t/p/" + "w185/" + movie.getPoster_path();
        Picasso.with(getContext()).load(url).into(imageView);

        return convertView;
    }
}
