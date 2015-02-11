package ws.krizek.android.picturelibrary.ui.showpicture;

import ws.krizek.android.picturelibrary.bitmap.BitmapProcessor;
import ws.krizek.android.picturelibrary.config.Constants;
import ws.krizek.android.picturelibrary.data.Picture;
import ws.krizek.android.picturelibrary.ui.showpicture.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.List;

import ws.krizek.android.picturelibrary.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ShowPictureActivity extends Activity implements GestureDetector.OnGestureListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    public static final String EXTRA_PICTURE_INDEX = "ws.krizek.android.picturelibrary.PICTURE_INDEX";
    public static final String EXTRA_PICTURE_COLLECTION = "ws.krizek.android.picturelibrary.PICTURE_COLLECTION";


    private boolean decorVisible = false;

    private int mControlsHeight;
    private int mShortAnimTime;

    private List<Picture> pictureDataset;

    private int swipeMinDistance;
    private int swipeThresholdVelocity;
    private int swipeMaxOffPath;

    private ImageView imageView;
    private View controlsView;

    private GestureDetector gestureScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_picture);


        final ViewConfiguration vc = ViewConfiguration.get(getApplicationContext());
        swipeMinDistance = vc.getScaledPagingTouchSlop();
        swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
        swipeMaxOffPath = vc.getScaledTouchSlop();

        controlsView = findViewById(R.id.fullscreen_content_controls);
        imageView = (ImageView) findViewById(R.id.image_fullscreen);

        gestureScanner = new GestureDetector(this, this);

        pictureDataset = (List<Picture>) getIntent().getSerializableExtra(EXTRA_PICTURE_COLLECTION);


        setPicture(pictureDataset.get(getIntent().getIntExtra(EXTRA_PICTURE_INDEX, 0)));
    }
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    private void toggleDecorUI() {
        if (mControlsHeight == 0) {
            mControlsHeight = controlsView.getHeight();
        }
        if (mShortAnimTime == 0) {
            mShortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        }
        controlsView.animate()
                .translationY(decorVisible ? 0 : mControlsHeight)
                .setDuration(mShortAnimTime);
        decorVisible = !decorVisible;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hideSystemUI();
    }

    private void setPicture(Picture p) {
        imageView.setTag(p);

        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                BitmapProcessor.loadImage(imageView, ((Picture) imageView.getTag()).getAbsolutePath());
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        imageView.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        toggleDecorUI();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > swipeThresholdVelocity) {
            Log.d(Constants.LOG, "So flingy...");
            int i = pictureDataset.indexOf((Picture)imageView.getTag());
            if (e1.getX() - e2.getX() > swipeMinDistance) {
                // gesture right
                if (i < pictureDataset.size() - 1) {
                    setPicture(pictureDataset.get(i + 1));
                }
            } else if (e2.getX() - e1.getX() > swipeMinDistance) {
                // gesture left
                if (i > 0) {
                    setPicture(pictureDataset.get(i - 1));
                }
            }
        } else {
            Log.d(Constants.LOG, "Not flingy enough.");
        }

        return false;
    }
}
