package pl.acieslinski.moviefun.models;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

import pl.acieslinski.moviefun.connection.Api;

/**
 * Created by acieslinski on 07.11.15.
 */
public class Video {
    @SerializedName(value = Api.FIELD_TITLE)
    private String mTitle;

    @SerializedName(value = Api.FIELD_RUNTIME)
    private String mRuntime;

    @SerializedName(value = Api.FIELD_PLOT)
    private String mPlot;

    @SerializedName(value = Api.FIELD_GENRE)
    private String mGenre;

    @SerializedName(value = Api.FIELD_YEAR)
    private String mYear;

    @SerializedName(value = Api.FIELD_POSTER)
    private String mPosterLink;

    public Video() {
        mTitle = "";
        mRuntime = "";
        mPlot = "";
        mPosterLink = "";
        mGenre = "";
        mYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getRuntime() {
        return mRuntime;
    }

    public void setRuntime(String runTime) {
        mRuntime = runTime;
    }

    public String getPlot() {
        return mPlot;
    }

    public void setPlot(String plot) {
        mPlot = plot;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public String getPosterLink() {
        return mPosterLink;
    }

    public void setPosterLink(String posterLink) {
        mPosterLink = posterLink;
    }
}
