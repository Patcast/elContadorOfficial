package be.kuleuven.elcontador10.background;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

public class Base64Encoder {
    public static String encodeImage(Image image) {
        return Base64Encoder.encodeImage(image);
    }

    public static Bitmap decodeImage(String encode) {
        byte[] decodedString = Base64.decode(encode, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
