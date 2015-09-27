package com.example.thomasthiebaud.android.movie.model.item;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;

import java.io.Serializable;

/**
 * Created by thiebaudthomas on 09/09/15.
 */
public class MovieItem implements Parcelable {
    private int id;
    private String title;
    private String posterPath;
    private String overview;
    private double voteAverage;
    private String releaseDate;

    public MovieItem() {

    }

    protected MovieItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "MovieItem{" +
                "releaseDate='" + releaseDate + '\'' +
                ", voteAverage=" + voteAverage +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", title='" + title + '\'' +
                ", id=" + id +
                '}';
    }

    public ContentValues toContentValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(DatabaseContract.MovieEntry._ID,this.getId());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_OVERVIEW,this.getOverview());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_POSTER_PATH,this.getPosterPath());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_RELEASE_DATE,this.getReleaseDate());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_TITLE, this.getTitle());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE, this.getVoteAverage());
        return movieValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
    }
}
