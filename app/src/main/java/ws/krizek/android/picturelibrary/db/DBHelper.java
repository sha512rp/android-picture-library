package ws.krizek.android.picturelibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ws.krizek.android.picturelibrary.config.Constants;

/**
 * Created by sharp on 13.1.15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "PictureLibrary.db";
    public static final int DB_VERSION = 6;

    public static final String PICTURES_TABLE_NAME = "pictures";
    public static final String PICTURES_COLUMN_ID = "picture_id";
    public static final String PICTURES_COLUMN_PATH = "path";
    public static final String PICTURES_COLUMN_FAVORITE = "favorite";

    public static final String TAGS_TABLE_NAME= "tags";
    public static final String TAGS_COLUMN_ID = "tag_id";
    public static final String TAGS_COLUMN_LABEL = "label";

    public static final String PICTURES_TAGS_TABLE_NAME= "pictures_tags";
    public static final String PICTURES_TAGS_COLUMN_ID = "picture_tag_id";
    public static final String PICTURES_TAGS_COLUMN_PICTURE_ID = "picture_id";
    public static final String PICTURES_TAGS_COLUMN_TAG_ID = "tag_id";

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( "CREATE TABLE " + PICTURES_TABLE_NAME + " (" +
                PICTURES_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                PICTURES_COLUMN_PATH + " TEXT, " +
                PICTURES_COLUMN_FAVORITE + " INTEGER)");

        db.execSQL("CREATE TABLE " + TAGS_TABLE_NAME + " (" +
                TAGS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                TAGS_COLUMN_LABEL + " TEXT)");

        db.execSQL("CREATE TABLE " + PICTURES_TAGS_TABLE_NAME + " (" +
                PICTURES_TAGS_COLUMN_ID + " INTEGER PRIMARY KEY, " +

                PICTURES_TAGS_COLUMN_PICTURE_ID + " INTEGER REFERENCES " +
                    PICTURES_TABLE_NAME + " (" + PICTURES_COLUMN_ID +
                    ") ON UPDATE CASCADE ON DELETE CASCADE, " +

                PICTURES_TAGS_COLUMN_TAG_ID + " INTEGER REFERENCES " +
                    TAGS_TABLE_NAME + " (" + TAGS_COLUMN_ID +
                    ") ON UPDATE CASCADE ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TAGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TAGS_TABLE_NAME);
        onCreate(db);

        // TODO debug
        db.execSQL("INSERT INTO tags (label) VALUES ('Cats')");
        db.execSQL("INSERT INTO tags (label) VALUES ('Universe')");
        db.execSQL("INSERT INTO tags (label) VALUES ('Nature')");
    }


}
