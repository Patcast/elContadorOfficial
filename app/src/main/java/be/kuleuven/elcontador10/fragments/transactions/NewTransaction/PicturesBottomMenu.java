package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import be.kuleuven.elcontador10.R;


public class PicturesBottomMenu extends BottomSheetDialogFragment {

    public interface PicturesBottomSheetListener{
        void onGalleryClick();
        void onTakePictureClick();
    }

    ConstraintLayout galleryButton;
    ConstraintLayout takePicButton;
    PicturesBottomSheetListener attachedListener;

    public PicturesBottomMenu (PicturesBottomSheetListener attachedListener) {
        this.attachedListener= attachedListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.bottom_menu_pick_gallery_or_camara, container, false);
        galleryButton = view.findViewById(R.id.bs_open_gallery);
        takePicButton = view.findViewById(R.id.bs_take_picture);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryButton.setOnClickListener(v -> attachedListener.onGalleryClick());
        takePicButton.setOnClickListener(v -> attachedListener.onTakePictureClick());
    }



}