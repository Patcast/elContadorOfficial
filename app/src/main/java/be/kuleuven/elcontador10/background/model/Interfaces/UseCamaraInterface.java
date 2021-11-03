package be.kuleuven.elcontador10.background.model.Interfaces;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.List;

public interface UseCamaraInterface {
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    @RequiresApi(api = Build.VERSION_CODES.R)
    void StartCamara();
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) ;
    void onPermissionsGranted(int requestCode, @NonNull List<String> perms);
    void onPermissionsDenied(int requestCode, @NonNull List<String> perms) ;
}
