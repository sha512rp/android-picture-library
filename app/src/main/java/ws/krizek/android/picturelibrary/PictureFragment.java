package ws.krizek.android.picturelibrary;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {
    private static final String ARG_DIRECTORY = "directoryPath";

    private static final String DEFAULT_PICTURE_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Pictures";

    private String directoryPath = DEFAULT_PICTURE_DIRECTORY;
    private File directory;

    private RecyclerView recList;
    private OnFragmentInteractionListener mListener;
    private PictureAdapter mAdapter;
    private List<Picture> pictureDataset;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param directory Place to look for pictures.
     * @return A new instance of fragment PictureFragment.
     */
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
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        recList = (RecyclerView) getView().findViewById(R.id.my_recycler_view);

        recList.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        glm.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(glm);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class ImageFinder extends AsyncTask<String, Void, Void> {

        public void walk(File root) {

            File[] list = root.listFiles();

            for (File f : list) {
                if (f.isDirectory()) {
                    Log.d(Constants.LOG, "Dir: " + f.getAbsoluteFile());
                    walk(f);
                }
                else {
                    Log.d(Constants.LOG, "File: " + f.getAbsoluteFile());
                    String filename = f.getName().toLowerCase();
                    if (filename.endsWith("jpg")||
                            filename.endsWith("jpeg") ||
                            filename.endsWith("png") ||
                            filename.endsWith("bmp")) {
                        pictureDataset.add(new Picture(f.getAbsolutePath()));
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
