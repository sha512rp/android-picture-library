package ws.krizek.android.picturelibrary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ws.krizek.android.picturelibrary.data.Tag;
import ws.krizek.android.picturelibrary.util.MyApplication;

public class TagDriver {

    private static String[] allColumns = { DBHelper.TAGS_COLUMN_ID,
            DBHelper.TAGS_COLUMN_LABEL};

    private static SQLiteDatabase db = (new DBHelper(MyApplication.getAppContext())).getWritableDatabase();

    public static List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TAGS_TABLE_NAME,
                allColumns, null, null, null, null, "label");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tags.add(Tag.getTag(cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();

        return tags;
    }

    public static void updateTagId(Tag tag) {
        if (tag.getId() != null) return;

        Cursor cursor = db.query(DBHelper.TAGS_TABLE_NAME,
                allColumns, "label = '" + tag.getLabel() + "'", null, null, null, null);

        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            persist(tag);
        } else {
            tag.setId(cursor.getLong(0));
        }
        cursor.close();
    }

    public static void persist(Tag tag) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TAGS_COLUMN_LABEL, tag.getLabel());

        if (tag.getId() == null) {
            tag.setId(
                    db.insert(DBHelper.TAGS_TABLE_NAME, null, contentValues));
        } else {
            db.update(DBHelper.TAGS_TABLE_NAME, contentValues,
                    "tag_id = ? ", new String[]{Long.toString(tag.getId())});
        }
    }
}
