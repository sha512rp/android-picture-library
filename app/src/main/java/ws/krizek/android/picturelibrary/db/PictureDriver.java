package ws.krizek.android.picturelibrary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ws.krizek.android.picturelibrary.config.Constants;
import ws.krizek.android.picturelibrary.data.Picture;
import ws.krizek.android.picturelibrary.data.Tag;
import ws.krizek.android.picturelibrary.util.MyApplication;

/**
 * Created by sharp on 13.1.15.
 */
public class PictureDriver {

    private static String[] allColumns = { DBHelper.PICTURES_COLUMN_ID,
            DBHelper.PICTURES_COLUMN_PATH, DBHelper.PICTURES_COLUMN_FAVORITE };

    private static SQLiteDatabase db = (new DBHelper(MyApplication.getAppContext())).getWritableDatabase();

    public static List<Picture> getAll() {
        List<Picture> pictures = new ArrayList<Picture>();

        Cursor cursor = db.rawQuery("SELECT pictures.path " +
                "FROM pictures ", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Picture picture = getByPath(cursor.getString(0));
            pictures.add(picture);
            cursor.moveToNext();
        }
        cursor.close();
        return pictures;
    }

    public static List<Picture> getUntagged() {
        List<Picture> pictures = new ArrayList<Picture>();

        Cursor cursor = db.rawQuery("SELECT DISTINCT pictures.path " +
                "FROM pictures " +
                "LEFT JOIN pictures_tags ON pictures.picture_id = pictures_tags.picture_id " +
                "WHERE pictures_tags.tag_id IS NULL", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Picture picture = getByPath(cursor.getString(0));
            pictures.add(picture);
            cursor.moveToNext();
        }
        cursor.close();
        return pictures;
    }

    public static List<Picture> getByTag(Tag tag) {
        List<Picture> pictures = new ArrayList<Picture>();

        Cursor cursor = db.rawQuery("SELECT DISTINCT pictures.path " +
                "FROM pictures " +
                "JOIN pictures_tags ON pictures.picture_id = pictures_tags.picture_id " +
                "WHERE pictures_tags.tag_id = " + tag.getId(), null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Picture picture = getByPath(cursor.getString(0));
            pictures.add(picture);
            cursor.moveToNext();
        }
        cursor.close();
        return pictures;
    }

    public static Map<String, Boolean> getPictureTags(Picture picture) {
        Map<String, Boolean> selectedTags = new HashMap<>();

        Log.d(Constants.LOG, "getPictureTags: picture_id = " + picture.getId());

        Cursor cursor = db.rawQuery("SELECT tags.label, pictures_tags.picture_id " +
                "FROM tags " +
                "LEFT JOIN pictures_tags ON (pictures_tags.tag_id = tags.tag_id " +
                "AND pictures_tags.picture_id = " + picture.getId() + ") " +
                "ORDER BY label", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            selectedTags.put(cursor.getString(0), Boolean.valueOf(cursor.getLong(1) != 0));
            cursor.moveToNext();
        }

        cursor.close();
        return selectedTags;
    }

    public static Picture getByPath(String path) {
        Picture picture;
        Cursor cursor = db.query(DBHelper.PICTURES_TABLE_NAME,
                allColumns, "path = '" + path + "'", null, null, null, null);

        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            picture = new Picture(path);
            persist(picture);
        } else {
            picture = cursorToPicture(cursor);
            // get picture tags
            cursor = db.rawQuery("SELECT tags.label " +
                    "FROM tags " +
                    "JOIN pictures_tags ON (pictures_tags.tag_id = tags.tag_id) " +
                    "WHERE pictures_tags.picture_id = " + picture.getId(), null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String label = cursor.getString(0);
                picture.addTag(Tag.getTag(label));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return picture;
    }

    private static Picture cursorToPicture(Cursor cursor) {
        Picture picture = new Picture();
        picture.setId(cursor.getLong(0));
        picture.setAbsolutePath(cursor.getString(1));
        picture.setFavorite(cursor.getDouble(2) > 0);
        return picture;
    }

    public static void persist(Picture picture) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.PICTURES_COLUMN_PATH, picture.getAbsolutePath());
        contentValues.put(DBHelper.PICTURES_COLUMN_FAVORITE, picture.isFavorite());

        if (picture.getId() == null) {
            picture.setId(
                    db.insert(DBHelper.PICTURES_TABLE_NAME, null, contentValues));
        } else {
            db.update(DBHelper.PICTURES_TABLE_NAME, contentValues,
                    "picture_id = ? ", new String[]{Long.toString(picture.getId())});
        }

        // delete all tags
        db.execSQL("DELETE FROM pictures_tags " +
                "WHERE picture_id = " + picture.getId());

        // persist all tags
        for (Tag tag: picture.getTags()) {
            if (tag.getId() == null) continue;

            contentValues = new ContentValues();
            contentValues.put(DBHelper.PICTURES_TAGS_COLUMN_PICTURE_ID, picture.getId());
            contentValues.put(DBHelper.PICTURES_TAGS_COLUMN_TAG_ID, tag.getId());

            Log.d(Constants.LOG, "pictures_tags_id: "+picture.getId() + " -> " + tag.getLabel());
            db.insert(DBHelper.PICTURES_TAGS_TABLE_NAME, null, contentValues);
        }
    }
}
