package com.example.movies;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.example.movies.FetchMovieAsync;
import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.database.DataSource;
import com.example.movies.database.MovieRoom;
import com.example.movies.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieModel[]> {
        private final static int LOADERID=1;
    RecyclerView recyclerView;
        MovieModel[] movieModel;
        MyAdapter myAdapter;
        String sort_order = "popular";
        GridLayout g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportLoaderManager().getLoader(LOADERID)!=null) {
            getSupportLoaderManager().initLoader(LOADERID, null, this);
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putString("SORT_ORDER",sort_order);
            getSupportLoaderManager().restartLoader(LOADERID,bundle,this);
        }
        recyclerView=findViewById(R.id.rcv);
        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        }
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.top_rated)
        {
            sort_order = "top_rated";
            Bundle bundle = new Bundle();
            bundle.putString("SORT_ORDER",sort_order);
            getSupportLoaderManager().restartLoader(LOADERID,bundle,this);

        }
        if(item.getItemId()==R.id.populer)
        {
            sort_order = "popular";
            Bundle bundle = new Bundle();
            bundle.putString("SORT_ORDER",sort_order);
            getSupportLoaderManager().restartLoader(LOADERID,bundle,this);
        }
        if(item.getItemId()==R.id.favourites)
        {
            MovieRoom r= Room.databaseBuilder(this, MovieRoom.class,"table").allowMainThreadQueries().build();
            List<DataSource> dataSources=new ArrayList<>();
            dataSources=r.dao().getFavouriteMovies();
                MovieModel[] movies = new MovieModel[dataSources.size()];
                for (int i = 0; i < dataSources.size(); i++) {
                    movies[i] = new MovieModel();
                    movies[i].setOriginalTitle(dataSources.get(i).getMname());
                    movies[i].setPosterPath(dataSources.get(i).getPoster());
                    movies[i].setOverview(dataSources.get(i).getOverview());
                    movies[i].setVoteAverage(dataSources.get(i).getVote());
                    movies[i].setReleaseDate(dataSources.get(i).getRelease_date());

                }
                this.movieModel=movies;
                setupRecyclerAdapter(this.movieModel);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerAdapter(MovieModel[] movie) {
        myAdapter = new MyAdapter(this,movie);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Loader<MovieModel[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        if(bundle!=null)
        {
            return new FetchMovieAsync(this,bundle.getString("SORT_ORDER"));
        }
        return new FetchMovieAsync(this,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<MovieModel[]> loader, MovieModel[] strings) {
        this.movieModel = strings;
        setupRecyclerAdapter(this.movieModel);

    }
    @Override
    public void onLoaderReset(@NonNull Loader<MovieModel[]> loader) {
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        MovieModel[] movieModel;
        final Context context;
        public MyAdapter(Context context,MovieModel[] movieModel) {
            this.movieModel = movieModel;
            this.context=context;
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v= LayoutInflater.from(context).inflate(R.layout.movie_item,viewGroup,false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,MovieDetails.class);
                    int position=(int)v.getTag();
                    intent.putExtra("poster",movieModel[position].getDetailPosterPath());
                    intent.putExtra("release",movieModel[position].getReleaseDate());
                    intent.putExtra("vote",movieModel[position].getVoteAverage());
                    intent.putExtra("overview",movieModel[position].getOverview());
                    intent.putExtra("original_title",movieModel[position].getOriginalTitle());
                    intent.putExtra("movie_id",movieModel[position].getmId());
                    startActivity(intent);

                }
            });
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            /*Picasso
                    .with(context)
                    .load(movieModel[i].getPosterPath())
                    .into(myViewHolder.mView);*/
            if(movieModel[i].getDetailPosterPath().contains("http")) {
                Glide.with(context)
                        .load(movieModel[i].getDetailPosterPath())
                        .into(myViewHolder.mView);
                myViewHolder.itemView.setTag(i);
            }
            else{
                Glide.with(context)
                        .load(movieModel[i].getPosterPath())
                        .into(myViewHolder.mView);
                myViewHolder.itemView.setTag(i);
            }
        }
        @Override
        public int getItemCount() {
            if(movieModel!=null)
            return movieModel.length;
            return 0;
        }

        public class MyViewHolder extends  RecyclerView.ViewHolder{
            ImageView mView;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                mView=itemView.findViewById(R.id.imageView2);
            }
        }
    }
}
