package com.example.movies;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies.Adapters.VideoAdapter;
import com.example.movies.R;
import com.example.movies.database.DataSource;
import com.example.movies.database.MovieRoom;
import com.example.movies.model.MovieModel;
import com.example.movies.model.VideoModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks <VideoModel[]> {
    ImageView poster;
    TextView release_date,vote,overview;
    Button add_to_favor;
    String votes="";
    private String url;
   private String path="https://image.tmdb.org/t/p/w185";
   RecyclerView recycleTrailers;
   String movie_id;
    private final static int LOADER=2;
    VideoModel[] vm;
    VideoAdapter videoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if(getSupportLoaderManager().getLoader(LOADER)!=null) {
            getSupportLoaderManager().initLoader(LOADER, null, this);
        }
        else
        {
            getSupportLoaderManager().restartLoader(LOADER,null,this);
        }


        poster=findViewById(R.id.imageView);
        release_date=findViewById(R.id.release_date);
        vote=findViewById(R.id.vote);
        overview=findViewById(R.id.overview);

        recycleTrailers=findViewById(R.id.recycle_videos);
        videoAdapter=new VideoAdapter(this,vm);
        recycleTrailers.setLayoutManager(new LinearLayoutManager(this));
       recycleTrailers.setAdapter(videoAdapter);


        Intent intent=getIntent();
        movie_id=intent.getStringExtra("movie_id");
        Toast.makeText(this, movie_id, Toast.LENGTH_SHORT).show();
        if(intent.getStringExtra("poster").contains("http"))
            url=intent.getStringExtra("poster");
        else
            url=path+intent.getStringExtra("poster");
        Picasso
                .with(this)
                .load(url)
                .into(poster);
        final String release=intent.getStringExtra("release");
        final String original_title=intent.getStringExtra("original_title");
        if(intent.getStringExtra("vote")!=null)
             votes=intent.getStringExtra("vote");
        final String overviews=intent.getStringExtra("overview");
        release_date.setText(release);
        vote.setText(votes);
        overview.setText(overviews);
        add_to_favor=findViewById(R.id.Add);
        add_to_favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieRoom r= Room.databaseBuilder(MovieDetails.this, MovieRoom.class,"table").allowMainThreadQueries().build();
                DataSource d=new DataSource();
                d.setMname(original_title);
                d.setOverview(overviews);
                d.setVote(votes);
                d.setRelease_date(release);
                d.setPoster(url);
                List<DataSource> dataSources=new ArrayList<>();
                dataSources=r.dao().getFavouriteMovies();
                int flag=0;
                for(int i=0;i<dataSources.size();i++) {
                    if(dataSources.get(i).getMname().equals(original_title)) {

                        r.dao().deleteData(dataSources.get(i));
                        r.dao().deleteData(d);
                        flag=1;
                        Toast.makeText(MovieDetails.this, dataSources.get(i).getMname()+" removed from favourites", Toast.LENGTH_SHORT).show();

                    }
                }
                if(flag==0)
                {
                    r.dao().insertData(d);
                    Toast.makeText(MovieDetails.this,dataSources.get(dataSources.size()-1).getMname()+" Added to favourites", Toast.LENGTH_SHORT).show();
                }
                r.close();
            }
        });


    }


    @NonNull
    @Override
    public Loader<VideoModel[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new FetchVideos(this,movie_id);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<VideoModel[]> loader, VideoModel[] s) {
        this.vm=s;
        VideoAdapter videoAdapter = new VideoAdapter(this, this.vm);
        recycleTrailers.setLayoutManager(new LinearLayoutManager(this));
        recycleTrailers.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader< VideoModel[] > loader) {

    }
}
