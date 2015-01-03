package ws.krizek.android.picturelibrary.data;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by sharp on 3.1.15.
 */
public class Picture {
    private String absolutePath;
    private boolean favorite;
    private Set<Tag> tags;

    public Picture(String absolutePath) {
        this.absolutePath = absolutePath;

        tags = new HashSet<>();
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
        }
    }
}
