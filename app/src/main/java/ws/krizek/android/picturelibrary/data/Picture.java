package ws.krizek.android.picturelibrary.data;


import android.util.Log;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ws.krizek.android.picturelibrary.config.Constants;

/**
 * Created by sharp on 3.1.15.
 */
public class Picture extends Persistable implements Serializable {
    private String absolutePath;
    private boolean favorite;
    private Set<Tag> tags;

    public Picture() {
        tags = new HashSet<>();
    }

    public Picture(String absolutePath) {
        this.absolutePath = absolutePath;

        tags = new HashSet<>();
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isFavorite() {
        //Log.d(Constants.LOG, "Picture: " +absolutePath+ " isFavorite = "+favorite);
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
