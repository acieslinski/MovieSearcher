## Movie searcher

It is a simple project, which shows couple of features of an android app. The project is written
according to the Google guide (http://source.android.com/source/code-style.html).

The main purpose of writing this app was to present the use of common libraries (like *retrofit*,
*OrmLite*, *rxandroid*). It shows how to use some android's components like *RecyclerView*,
*Spinner*, *GridView* or *EditText* and extend them for our own purpose. The project presents how we
can modify behavior of an activity for different devices like smartphones and tablets. The code is
written with a big conscious to readability, reliability, modularity, maintainability and security.
It complies with the DRY rule also. The interface was designed to be easy to use, clean and
responsive.

## Demo

![search videos](./gifs/mobile-portrait.gif)

![search videos](./gifs/mobile-land.gif)

## Dependencies

The project uses following dependencies:

* retrofit - fetching and parsing the json results from a server
* picasso - fetching and caching images
* ormlite - ORM helper for the sqlite database
* butterknife - simpler and better binding of views
* eventbus - easy and clear event based communication
* androidanimations - rich set of animations easy to use
* rxandroid - reactive programming for android