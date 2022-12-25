package com.zzwarn.clscheckin.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ZoomBitmap {
    public static Bitmap zoomImage(Bitmap img, double newWidth, double newHeight) {
        float width = img.getWidth();
        float height = img.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(img, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
