package ws.krizek.android.picturelibrary.data;

/**
 * Created by sharp on 13.1.15.
 */
public abstract class Persistable {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
