package com.cheng.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.cheng.popmovies.R.id.tv_overview;
import static com.cheng.popmovies.R.id.tv_release_date;
import static com.cheng.popmovies.R.id.tv_runtime;
import static com.cheng.popmovies.R.id.tv_vote_average;

public class DetailActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            movie = (Movie) intent.getSerializableExtra("movieDetail");
        }
        getSupportActionBar().setTitle(movie.getTitle());

        ImageView imageView = (ImageView) findViewById(R.id.iv_poster);
        String url = "http://image.tmdb.org/t/p/" + "w185/" + movie.getPoster_path();
        Picasso.with(this).load(url).into(imageView);

        ((TextView) findViewById(tv_release_date)).setText(movie.getRelease_date());
        //((TextView) findViewById(tv_runtime)).setText(movie.getRuntime());
        //((TextView) findViewById(tv_vote_average)).setText(movie.getVote_average() + "/10");
        ((TextView) findViewById(tv_overview)).setText(movie.getOverview());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getDetail() {
        Intent intent = getIntent();
        if (intent != null) {
            movie = (Movie) intent.getSerializableExtra("movieDetail");
        }
        FetchDetailTask fetchDetailTask = new FetchDetailTask();
        fetchDetailTask.execute(movie.getId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDetail();
    }

    public class FetchDetailTask extends AsyncTask<String, Void, Void> {

        private void getDetailFromJson(String detailJsonStr) throws JSONException {
            final String DETAIL_VOTEAVERAGE = "vote_average";
            final String DETAIL_RUNTIME = "runtime";


            JSONObject details = new JSONObject(detailJsonStr);

            movie.setVote_average(details.getDouble(DETAIL_VOTEAVERAGE));
            movie.setRuntime(details.getInt(DETAIL_RUNTIME));
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params == null) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String detailJsonStr = null;
            try {
                final String DETAIL_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";
                Uri builtUri = Uri.parse(DETAIL_BASE_URL + params[0]).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    detailJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    detailJsonStr = null;
                }
                detailJsonStr = buffer.toString();
                Log.v(DetailActivity.FetchDetailTask.class.getSimpleName(), "Detail string:" + detailJsonStr);
            } catch (IOException e) {
                e.printStackTrace();
                detailJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                getDetailFromJson(detailJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((TextView) findViewById(tv_runtime)).setText(movie.getRuntime() + "min");
            ((TextView) findViewById(tv_vote_average)).setText(movie.getVote_average() + "/10");
        }
    }
}
