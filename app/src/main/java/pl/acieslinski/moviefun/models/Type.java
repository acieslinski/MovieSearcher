package pl.acieslinski.moviefun.models;

import android.content.res.Resources;

import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;

/**
 * @author Arkadiusz Cieśliński on 14.11.15.
 *         <acieslinski@gmail.com>
 */
public enum Type {
    ALL,
    MOVIE,
    EPISODE,
    SERIES;

    public String getCodeName() {
        switch (this) {
            case MOVIE:
            case EPISODE:
            case SERIES:
                return super.toString().toLowerCase();
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        int stringResourceId = 0;

        switch (this) {
            case ALL:
                stringResourceId = R.string.type_all;
                break;
            case MOVIE:
                stringResourceId = R.string.type_movie;
                break;
            case EPISODE:
                stringResourceId = R.string.type_episode;
                break;
            case SERIES:
                stringResourceId = R.string.type_series;
                break;
        }

        return stringResourceId == 0 ? "" : Application.getInstance().getString(stringResourceId);
    }
}
