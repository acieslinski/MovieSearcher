/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private boolean mIsPosterAvailable;

    public Video() {
        mTitle = "";
        mRuntime = "";
        mPlot = "";
        mPosterLink = "";
        mGenre = "";
        mIsPosterAvailable = false;
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

    public boolean isPosterAvailable() {
        return mIsPosterAvailable;
    }

    public void setIsPosterAvailable(boolean isPosterAvailable) {
        mIsPosterAvailable = isPosterAvailable;
    }
}
