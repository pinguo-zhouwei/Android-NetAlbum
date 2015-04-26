package us.pinguo.album.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import us.pinguo.album.R;

/**
 * Created by Mr 周先森 on 2015/4/26.
 */
public class CommonProgressDialog extends Dialog {
    private Context mContext;

    public CommonProgressDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
    }

    public CommonProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    protected CommonProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_layout);
        initView();
    }

    private void initView() {
        ImageView spaceshipImage = (ImageView) findViewById(R.id.common_dialog_iamge);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext, R.anim.dialog_loading_anim); // 加载动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);             // 使用ImageView显示动画
    }
}
