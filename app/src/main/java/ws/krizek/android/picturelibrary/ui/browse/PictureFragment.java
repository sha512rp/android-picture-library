package ws.krizek.android.picturelibrary.ui.browse;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ws.krizek.android.picturelibrary.data.Picture;
import ws.krizek.android.picturelibrary.R;
import ws.krizek.android.picturelibrary.db.PictureDriver;

public class PictureFragment extends Fragment {
    private static final String ARG_DIRECTORY = "directoryPath";

    private static final String DEFAULT_PICTURE_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Pictures";

    private String directoryPath = DEFAULT_PICTURE_DIRECTORY;
    private File directory;

    private RecyclerView recList;
    private OnFragmentInteractionListener mListener;
    private PictureAdapter mAdapter;

    private List<Picture> pictureDataset;

    public static PictureFragment newInstance(String directory) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DIRECTORY, directory);
        fragment.setArguments(args);
        return fragment;
    }

    public PictureFragment() {
        pictureDataset = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            directoryPath = getArguments().getString(ARG_DIRECTORY);
        }

        directory = new File(directoryPath);
        if (directory.isDirectory()) {
            pictureDataset.clear();
            new ImageFinder().execute(directoryPath);
        }

        mAdapter = new PictureAdapter(getActivity().getApplicationContext(), pictureDataset);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        recList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recList.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        glm.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(glm);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recList.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPictureDataset(List<Picture> pictureDataset) {
        this.pictureDataset.clear();
        for (Picture picture: pictureDataset) {
            this.pictureDataset.add(picture);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class ImageFinder extends AsyncTask<String, Void, Void> {

        public void walk(File root) {

            Picture picture;
            File[] list = root.listFiles();

            for (File f : list) {
                if (f.isDirectory()) {
                    walk(f);
                }
                else {
                    String filename = f.getName().toLowerCase();
                    if (filename.endsWith("jpg")||
                            filename.endsWith("jpeg") ||
                            filename.endsWith("png") ||
                            filename.endsWith("bmp")) {
                        picture = PictureDriver.getByPath(f.getAbsolutePath());
                        pictureDataset.add(picture);
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(String... path) {
            walk(new File(path[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
