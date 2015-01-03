package ws.krizek.android.picturelibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import ws.krizek.android.picturelibrary.bitmap.BitmapProcessor;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    private List<Picture> pictureDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PictureViewHolder extends RecyclerView.ViewHolder {
        protected ImageButton mFavoriteButton;
        protected ImageView mImageView;

        public PictureViewHolder(View v) {
            super(v);

            mFavoriteButton = (ImageButton) v.findViewById(R.id.card_favorite_button);
            mFavoriteButton.setOnClickListener(new FavoriteButtonOnClickListener());

            mImageView = (ImageView) v.findViewById(R.id.card_picture);
        }

        private class FavoriteButtonOnClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                if (button.getTag() == true) {
                    button.setTag(false);
                    button.setImageResource(R.drawable.ic_not_favorite);
                } else {
                    button.setTag(true);
                    button.setImageResource(R.drawable.ic_favorite);
                }
            }
        }
    }

    public PictureAdapter(List<Picture> pictureDataset) {
        this.pictureDataset = pictureDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PictureAdapter.PictureViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picture_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        PictureViewHolder vh = new PictureViewHolder((View)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mPictureCardView.setText(pictureDataset[position]);

        BitmapProcessor.loadImage(holder.mImageView,
                pictureDataset.get(position).getAbsolutePath());

        Log.d(Constants.LOG, "W:"+holder.mImageView.getWidth()+"H:"+holder.mImageView.getHeight());
        Log.d(Constants.LOG, pictureDataset.get(position).getAbsolutePath());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pictureDataset.size();
    }


}