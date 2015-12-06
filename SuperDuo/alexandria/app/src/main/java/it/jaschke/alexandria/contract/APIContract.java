package it.jaschke.alexandria.contract;

/**
 * Created by thomasthiebaud on 06/12/15.
 */
public final class APIContract {

    private APIContract() {}

    public static final String FETCH_BOOK = "it.jaschke.alexandria.services.action.FETCH_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";
    public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

    public static final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    public static final String QUERY_PARAM = "q";
    public static final String ISBN_PARAM = "isbn:";

    public static final String ITEMS = "items";

    public static final String VOLUME_INFO = "volumeInfo";

    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String AUTHORS = "authors";
    public static final String DESC = "description";
    public static final String CATEGORIES = "categories";
    public static final String IMG_URL_PATH = "imageLinks";
    public static final String IMG_URL = "thumbnail";
}
