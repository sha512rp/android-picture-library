package ws.krizek.android.picturelibrary.ui.browse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ws.krizek.android.picturelibrary.data.Picture;
import ws.krizek.android.picturelibrary.R;
import ws.krizek.android.picturelibrary.bitmap.BitmapProcessor;
import ws.krizek.android.picturelibrary.data.Tag;
import ws.krizek.android.picturelibrary.db.PictureDriver;
import ws.krizek.android.picturelibrary.ui.showpicture.ShowPictureActivity;
import ws.krizek.android.picturelibrary.util.MyApplication;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    private List<Picture> pictureDataset;
    Context ctx;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PictureViewHolder extends RecyclerView.ViewHolder {
        protected ImageButton mFavoriteButton;
        protected ImageButton mTagsButton;
        protected ImageView mImageView;

        public PictureViewHolder(View v) {
            super(v);

            mFavoriteButton = (ImageButton) v.findViewById(R.id.card_favorite_button);
            mFavoriteButton.setOnClickListener(new FavoriteButtonOnClickListener());

            mTagsButton = (ImageButton) v.findViewById(R.id.card_tags_button);
            mTagsButton.setOnClickListener(new TagsButtonOnClickListener());

            mImageView = (ImageView) v.findViewById(R.id.card_picture);
        }

        public void setOnPreDrawListener() {
            mImageView.getViewTreeObserver().addOnPreDrawListener(
                    new LoadBitmapOnPreDrawListener());
        }

        private class FavoriteButtonOnClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                Picture picture = (Picture)mImageView.getTag();

                if (picture.isFavorite()) {
                    picture.setFavorite(false);
                    PictureDriver.persist(picture);
                    button.setImageResource(R.drawable.ic_not_favorite);
                } else {
                    picture.setFavorite(true);
                    PictureDriver.persist(picture);
                    button.setImageResource(R.drawable.ic_favorite);
                }
            }
        }

        private class TagsButtonOnClickListener implements View.OnClickListener {
            Map<String, Boolean> selectedTags;
            Picture picture;

            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                picture = (Picture)mImageView.getTag();
                selectedTags = PictureDriver.getPictureTags(picture);
                final String[] tagArray = selectedTags.keySet().toArray(new String[selectedTags.keySet().size()]);
                final boolean[] initialSelectedTags = new boolean[selectedTags.values().size()];

                int i = 0;
                for (Boolean b: selectedTags.values()) {
                    initialSelectedTags[i] = b.booleanValue();
                    i++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyApplication.getBrowseActivity());
                // Set the dialog title
                builder.setTitle(R.string.tag_selection_dialog_title)
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setMultiChoiceItems(tagArray, initialSelectedTags,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            selectedTags.put(tagArray[which], true);
                                        } else {
                                            selectedTags.put(tagArray[which], false);
                                        }
                                    }
                                })
                                // Set the action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Set<Tag> tags = new HashSet<Tag>();
                                Iterator it = selectedTags.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pairs = (Map.Entry) it.next();
                                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                                    if (pairs.getValue() == true) {
                                        tags.add(Tag.getTag((String)pairs.getKey()));
                                    }
                                    it.remove(); // avoids a ConcurrentModificationException
                                }
                                picture.setTags(tags);
                                PictureDriver.persist(picture);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);

                builder.create().show();
            }
        }

        private class LoadBitmapOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
            @Override
            public boolean onPreDraw() {
                BitmapProcessor.loadImage(mImageView, ((Picture) mImageView.getTag()).getAbsolutePath());
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
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
                //Toast.makeText(ctx, ((Picture)v.getTag()).getAbsolutePath(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ctx, ShowPictureActivity.class);
                intent.putExtra(ShowPictureActivity.EXTRA_PICTURE_INDEX, pictureDataset.indexOf(v.getTag()));
                intent.putExtra(ShowPictureActivity.EXTRA_PICTURE_COLLECTION, (Serializable)pictureDataset);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        Picture picture = pictureDataset.get(position);
        holder.mImageView.setImageBitmap(null);
        holder.mImageView.setTag(picture);

        if (picture.isFavorite()) {
            holder.mFavoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mFavoriteButton.setImageResource(R.drawable.ic_not_favorite);
        }

        holder.setOnPreDrawListener();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pictureDataset.size();
    }


}