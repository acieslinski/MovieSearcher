package pl.acieslinski.moviefun.models;

/**
 * @author Arkadiusz Cieśliński 18.11.15.
 *         <acieslinski@gmail.com>
 */

public class SearchEvent {
    private Search mSearch;

    public SearchEvent(Search search) {
        mSearch = search;
    }

    public Search getSearch() {
        return mSearch;
    }

    public void setSearch(Search search) {
        mSearch = search;
    }
}
