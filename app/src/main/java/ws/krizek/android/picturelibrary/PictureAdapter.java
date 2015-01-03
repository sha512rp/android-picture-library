package ws.krizek.android.picturelibrary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PictureViewHolder extends RecyclerView.ViewHolder {
        protected ImageButton mFavoriteButton;
        public PictureViewHolder(View v) {
            super(v);
            mFavoriteButton = (ImageButton) v.findViewById(R.id.card_favorite_button);
            mFavoriteButton.setOnClickListener(new FavoriteButtonOnClickListener());
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
        };
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PictureAdapter(String[] myDataset) {
        mDataset = myDataset;
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
        //holder.mPictureCardView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}