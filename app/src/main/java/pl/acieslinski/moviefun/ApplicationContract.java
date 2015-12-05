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

package pl.acieslinski.moviefun;

import android.provider.BaseColumns;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public interface ApplicationContract {
    interface DatabaseContract extends BaseColumns {
        String SEARCH_FIELD_SEARCH = "search";
        String SEARCH_FIELD_YEAR = "year";
        String SEARCH_FIELD_DATE = "date";
        String SEARCH_FIELD_TYPE = "type";
    }
}
