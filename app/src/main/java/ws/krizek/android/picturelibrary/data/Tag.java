package ws.krizek.android.picturelibrary.data;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Represents a tag.
 * Only one class for each label.
 */
public class Tag {
    private static Dictionary<String, Tag> tags = new Hashtable<>();
    private String label;

    private Tag(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static Tag getTag(String label) {
        if (tags.get(label) == null) {
            tags.put(label, new Tag(label));
        }
        return tags.get(label);
    }
}
