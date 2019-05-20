package com.example.movies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.example.movies.database.DataSource;
@Database(entities = {DataSource.class},version = 2)
public abstract class MovieRoom extends RoomDatabase {
    public  abstract com.example.movies.database.MoviesDao dao();
}
