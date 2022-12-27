package com.example.contactsappassignmentrealmdb.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class Utils {

    public byte[] image2byteArray(ImageView image) {
        try {
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap byteArray2image(byte[] im) {
        Bitmap bit = null;

        try {
            bit = BitmapFactory.decodeByteArray(im, 0, im.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bit;
    }

    public byte[] image2byteArray(Bitmap bn) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bn.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
