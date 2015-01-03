package ws.krizek.android.picturelibrary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import ws.krizek.android.picturelibrary.bitmap.BitmapProcessor;
import ws.krizek.android.picturelibrary.bitmap.LoadBitmapOnGlobalLayout;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    private List<Picture> pictureDataset;
    Context ctx;

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

    public PictureAdapter(Context ctx, List<Picture> pictureDataset) {
        this.pictureDataset = pictureDataset;
        this.ctx = ctx;
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


        vh.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, ((Picture)v.getTag()).getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        holder.mImageView.setImageBitmap(null);
        holder.mImageView.setTag(pictureDataset.get(position));

        // if this is called before ImageView is measured
//        if (holder.mImageView.getWidth() == 0) {
//            holder.mImageView.getViewTreeObserver().addOnGlobalLayoutListener(
//                    new LoadBitmapOnGlobalLayout(holder.mImageView,
//                            pictureDataset.get(position).getAbsolutePath()));
//        } else {
            BitmapProcessor.loadImage(holder.mImageView,
                    pictureDataset.get(position).getAbsolutePath());
//        }

        Log.d(Constants.LOG, "P:"+position+" "+pictureDataset.get(position).getAbsolutePath());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pictureDataset.size();
    }


}