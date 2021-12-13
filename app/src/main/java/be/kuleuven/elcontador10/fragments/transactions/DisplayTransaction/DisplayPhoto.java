package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.chrisbanes.photoview.PhotoView;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;


public class DisplayPhoto extends Fragment {


    ViewModel_DisplayTransaction viewModel;
    View view;
    MainActivity mainActivity;
    PhotoView photoView;
    Button deleteButton;
    NavController navController;

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
        deleteButton = view.findViewById(R.id.btn_deletePicture);
        photoView = view.findViewById(R.id.photo_view);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_DisplayTransaction.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        deleteButton.setOnClickListener(i->deleteExecution());

    }

    private void deleteExecution() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete transaction")
                .setMessage("Are you sure you want to delete this picture?")
                .setPositiveButton("Yes", (dialog, which) ->confirmDelete())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }

    private void confirmDelete() {
        navController.popBackStack();
        viewModel.deletePicture(getContext());
    }


    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenBitMap().observe(getViewLifecycleOwner(), this::setImageDownloaded);
        viewModel.getChosenImage().observe(getViewLifecycleOwner(), this::setImageAdded);
    }


    private void setImageDownloaded(Bitmap bitmap) {
        if(bitmap!=null) {
            photoView.setImageBitmap(bitmap);
        }
    }
    private void setImageAdded(ImageFireBase imageAdded) {
        if(imageAdded!=null) {
            photoView.setImageURI(imageAdded.getContentUri());
        }
    }
}