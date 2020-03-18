package Model;

import android.graphics.Bitmap;

public class FilterModel {

    private Bitmap bitmap;
    private String string;

    public FilterModel(Bitmap bitmap, String string) {
        this.bitmap = bitmap;
        this.string = string;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getString() {
        return string;
    }
}
