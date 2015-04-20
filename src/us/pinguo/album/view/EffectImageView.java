package us.pinguo.album.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Mr 周先森 on 2015/4/20.
 */
public class EffectImageView extends ImageView {
    private static final String TAG = EffectImageView.class.getSimpleName();
    private Bitmap mBitmap;

    public EffectImageView(Context context) {
        super(context);
    }

    public EffectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EffectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageBmp(Bitmap bmp) {
        mBitmap = bmp;
        Log.i(TAG, "EffectImageView:-->" + "setImage");
        this.setImageBitmap(bmp);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }


}
