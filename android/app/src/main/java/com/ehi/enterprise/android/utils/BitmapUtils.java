package com.ehi.enterprise.android.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.ehi.enterprise.android.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtils {

    public static void saveBitmapToPng(Bitmap bitmap, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        os.flush();
        os.close();
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static int getBitmapWidth(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null) {
            return bitmapDrawable.getBitmap().getWidth();
        }
        return 0;
    }
}
