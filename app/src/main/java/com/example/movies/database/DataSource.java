package com.example.movies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favmovies")
public class DataSource {
    @PrimaryKey(autoGenerate = true)
    private int mno;
    @ColumnInfo(name="movie_name")
    private String mname;

    @ColumnInfo(name="release_date")
   private String release_date;

    @ColumnInfo(name="vote")
    private String vote;

    @ColumnInfo(name="overview")
    private String overview;

    @ColumnInfo(name="poster")
    private  String poster;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getMname() {
        return mname;
    }
    public void setMname(String mname) {
        this.mname = mname;
    }

    public int getMno() {
        return mno;
    }

    public void setMno(int mno) {
        this.mno = mno;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
