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


public class DisplayPhoto extends Fragment {

    ImageView imViewPhoto;
    ViewModel_DisplayTransaction viewModel;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display_photo, container, false);
        //imViewPhoto = view.findViewById(R.id.displayPhoto);
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

    }

    private void setImage(Bitmap bitmap) {
        if(bitmap!=null) {

            PhotoView photoView = view.findViewById(R.id.photo_view);
            photoView.setImageBitmap(bitmap);
        }
    }
}