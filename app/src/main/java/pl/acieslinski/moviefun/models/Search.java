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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Objects;

import pl.acieslinski.moviefun.ApplicationContract;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class Search implements Parcelable {
    private static final String TAG = Search.class.getSimpleName();

    @DatabaseField(
            generatedId = true,
            columnName = ApplicationContract.DatabaseContract._ID)
    protected long mId;

    @DatabaseField(
            canBeNull = false,
            uniqueCombo = true,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_SEARCH)
    private String mSearch;

    @Nullable
    @DatabaseField(
            canBeNull = false,
            uniqueCombo = true,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_YEAR)
    private String mYear;

    @Nullable
    @DatabaseField(
            canBeNull = false,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_DATE)
    private Date mDate;

    @Nullable
    @DatabaseField(
            canBeNull = false,
            uniqueCombo = true,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_TYPE)
    private Type mType;

    public Search() {
        mSearch = "";
        mYear = "";
        mType = Type.ALL;
        mDate = new Date();
    }

    public Search(Parcel in){
        String[] strings = new String[3];
        long[] longs = new long[2];

        in.readStringArray(strings);
        in.readLongArray(longs);

        mSearch = strings[0];
        mYear = strings[1];
        mType = Type.valueOf(strings[2]);

        mId = longs[0];
        mDate = new Date(longs[1]);
    }


    public String getSearch() {
        return mSearch;
    }

    public String getSearchEncoded() {
        String search = "";
        try {
            search = URLEncoder.encode(mSearch, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return search;
    }

    public void setSearch(String search) {
        mSearch = search;
    }

    @Nullable
    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    @Nullable
    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    @Nullable
    public Date getDate() {
        return mDate;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {mSearch, mYear, mType.toString()});
        dest.writeLongArray(new long[] {mId, mDate.getTime()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Search createFromParcel(Parcel in) {
            return new Search(in);
        }

        public Search[] newArray(int size) {
            return new Search[size];
        }
    };
}
