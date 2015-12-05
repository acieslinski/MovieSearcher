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
