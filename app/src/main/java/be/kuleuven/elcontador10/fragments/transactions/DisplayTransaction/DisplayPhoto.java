package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;


public class DisplayPhoto extends Fragment {


    ViewModel_DisplayTransaction viewModel;
    View view;
    MainActivity mainActivity;
    PhotoView photoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.setHeaderText(getString(R.string.image_captured));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display_photo, container, false);
        photoView = view.findViewById(R.id.photo_view);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_DisplayTransaction.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenBitMap().observe(getViewLifecycleOwner(), this::setImage);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_delete,true);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_add,true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_delete,false);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_add,false);
    }

    private void setImage(Bitmap bitmap) {
        if(bitmap!=null) {
            photoView.setImageBitmap(bitmap);
        }
    }
}