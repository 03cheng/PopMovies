package com.cheng.popmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieFragment extends Fragment {

    private MoviesAdapter moviesAdapter;

    public MovieFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Movie> movies = new ArrayList<>();
        Movie movie = new Movie();
        movie.setPoster_path("/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg");
        movies.add(movie);
        //moviesAdapter = new MoviesAdapter(getActivity(), movies);
        moviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gv_movies);
        gridView.setAdapter(moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = moviesAdapter.getItem(i).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable("movieDetail", moviesAdapter.getItem(i));
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtras(bundle);
                startActivity(intent);
                Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void updateMovie() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
        fetchMovieTask.execute(sort);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private ArrayList<Movie> getMovieListFromJson(String movieJsonStr) throws JSONException {
            ArrayList<Movie> movieList = new ArrayList<>();
            final String MOVIE_LIST = "results";
            final String MOVIE_ID = "id";
            final String MOVIE_POSTER = "poster_path";
            final String MOVIE_TITLE = "title";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RELEASEDATE = "release_date";
            final String MOVIE_VOTEAVERAGE = "vote_average";

            JSONObject movies = new JSONObject(movieJsonStr);
            JSONArray movieArray = movies.getJSONArray(MOVIE_LIST);

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieItem = movieArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(movieItem.getString(MOVIE_ID));
                movie.setPoster_path(movieItem.getString(MOVIE_POSTER));
                movie.setTitle(movieItem.getString(MOVIE_TITLE));
                movie.setOverview(movieItem.getString(MOVIE_OVERVIEW));
                movie.setRelease_date(movieItem.getString(MOVIE_RELEASEDATE));
                movie.setVote_average(movieItem.getDouble(MOVIE_VOTEAVERAGE));
                movieList.add(movie);
            }

            return movieList;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params == null) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3";
                final String POP_URL = "/movie/popular";
                final String RATED_URL = "/movie/top_rated";
                final String API_KEY = "api_key";
                Uri builtUri;
                if (params[0].equals(getString(R.string.pref_sort_popular))) {
                    builtUri = Uri.parse(MOVIE_BASE_URL + POP_URL).buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                } else if (params[0].equals(getString(R.string.pref_sort_rated))) {
                    builtUri = Uri.parse(MOVIE_BASE_URL + RATED_URL).buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                } else {
                    return null;
                }

                URL url = new URL(builtUri.toString());
                Log.v(FetchMovieTask.class.getSimpleName(), "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
                Log.v(FetchMovieTask.class.getSimpleName(), "Movies string:" + movieJsonStr);
            } catch (IOException e) {
                e.printStackTrace();
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MovierFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieListFromJson(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                moviesAdapter.clear();
                for (Movie movie : movies) {
                    Log.v("Picasso", "加载图片 ");
                    moviesAdapter.add(movie);
                }
            }
        }
    }
}
