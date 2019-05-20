package com.example.movies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.example.movies.database.DataSource;
import java.util.List;

@Dao
public interface MoviesDao {
    @Insert
     void insertData(DataSource dataSource);
    @Delete
      void deleteData(DataSource dataSource);
    @Query("select * from favmovies")
    List<DataSource> getFavouriteMovies();
}
