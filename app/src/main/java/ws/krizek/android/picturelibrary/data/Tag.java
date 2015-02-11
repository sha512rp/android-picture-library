package ws.krizek.android.picturelibrary.data;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import ws.krizek.android.picturelibrary.db.TagDriver;

/**
 * Represents a tag.
 * Only one class for each label.
 */
public class Tag extends Persistable implements Serializable{
    private static Dictionary<String, Tag> tags = new Hashtable<>();
    private String label;
    private int id;

    private Tag(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static Tag getTag(String label) {
        if (tags.get(label) == null) {
            Tag tag = new Tag(label);
            TagDriver.updateTagId(tag);
            tags.put(label, tag);
        }
        return tags.get(label);
    }

    public String getLabel() {
        return label;
    }
}
