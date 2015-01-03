package ws.krizek.android.picturelibrary.bitmap;

import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by sharp on 3.1.15.
 */
public class LoadBitmapOnGlobalLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private ImageView view;
    private String path;

    public LoadBitmapOnGlobalLayout(ImageView view, String path) {
        this.view = view;
        this.path = path;
    }

    @Override
    public void onGlobalLayout() {
        if (view.getWidth() != 0) {
            BitmapProcessor.loadImage(view, path);
        }
    }
}
