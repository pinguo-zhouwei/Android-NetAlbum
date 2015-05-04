package us.pinguo.album.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import us.pinguo.album.R;

/**
 * dialog 类
 * Created by zhouwei on 15-1-5.
 */
public class CommonAlertDialog extends Dialog implements View.OnClickListener {
    public static final String COMMON_ALERT = "common_alert";
    public static final String EXSIT_TITLE = "exsit_title";
    public static final String NO_TITLE = "no_title";
    private Context mContext;
    private String mContent;
    private String mTitle;
    private PositiveOnClickLister mPositiveOnClickLister;
    private NegativeOnClickLister mNegativeOnClickLister;
    /**
     * 是否有标题
     */
    private String mIsHaveTiTle = COMMON_ALERT;

    private boolean mIsOneBtn = false;

    /**
     * @param context 上下文
     * @param content 显示的内容
     */
    public CommonAlertDialog(Context context, String content) {
        super(context, R.style.CommonDialog);
        mContext = context;
        mContent = content;
    }

    /**
     * @param context 上下文
     * @param resId   显示内容的资源id
     */
    public CommonAlertDialog(Context context, int resId) {
        super(context, R.style.CommonDialog);
        mContext = context;
        mContent = context.getString(resId);

    }

    /**
     * @param context     上下文
     * @param content     提示框显示的内容
     * @param isHaveTitle 提示框是否有标题，默认没有标题
     * @param title       标题的内容，只有有了上面这个参数后才需要这个参数
     */
    public CommonAlertDialog(Context context, String content, String isHaveTitle, String title) {
        super(context, R.style.CommonDialog);
        mContext = context;
        mContent = content;
        mIsHaveTiTle = isHaveTitle;
        mTitle = title;
    }

    public CommonAlertDialog(Context context, String content, String isHaveTitle, String title, boolean isOneBtn) {
        super(context, R.style.CommonDialog);
        mContext = context;
        mContent = content;
        mIsHaveTiTle = isHaveTitle;
        mTitle = title;
        this.mIsOneBtn = isOneBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIsHaveTiTle.equals(COMMON_ALERT)) {
            initCommonDialog();
        } else if (mIsHaveTiTle.equals(NO_TITLE)) {
            initCommonNoTitleDialog();
        } else if (mIsHaveTiTle.equals(EXSIT_TITLE)) {
            initCommonHaveTitleDialog(mIsOneBtn);
        }

    }

    /**
     * 初始化普通提示框
     */
    public void initCommonDialog() {
        setContentView(R.layout.common_dialog_layout);
        findViewById(R.id.common_dialog_btn_ok).setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.common_dialog_content);
        textView.setText(mContent);
    }

    /**
     * 初始化普通没有Title的提示框
     */
    public void initCommonNoTitleDialog() {
        setContentView(R.layout.common_dialog_no_title_layout);
        TextView textView = (TextView) findViewById(R.id.common_dialog_no_title_content);
        textView.setText(mContent);
        findViewById(R.id.common_dialog_no_title_btn_cancel).setOnClickListener(this);
        findViewById(R.id.common_dialog_no_title_btn_ok).setOnClickListener(this);
    }

    /**
     * 初始化普通有标题的提示框
     */
    public void initCommonHaveTitleDialog(boolean isOneBtn) {
        setContentView(R.layout.common_dialog_have_title_layout);
        if (isOneBtn) {
            findViewById(R.id.dialog_bottom_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.common_dialog_bottom_btn_ok).setVisibility(View.VISIBLE);
            findViewById(R.id.common_dialog_bottom_btn_ok).setOnClickListener(this);
        } else {
            findViewById(R.id.dialog_bottom_view).setVisibility(View.VISIBLE);
            findViewById(R.id.common_dialog_bottom_btn_ok).setVisibility(View.INVISIBLE);
            findViewById(R.id.common_dialog_have_title_btn_cancel).setOnClickListener(this);
            findViewById(R.id.common_dialog_have_title_btn_ok).setOnClickListener(this);
        }
        TextView title = (TextView) findViewById(R.id.common_dialog_have_title_Title);
        title.setText(mTitle);
        TextView textView = (TextView) findViewById(R.id.common_dialog_have_title_content);
        textView.setText(mContent);

    }

    public void setPositiveOnClickLister(PositiveOnClickLister mPositiveOnClickLister) {
        this.mPositiveOnClickLister = mPositiveOnClickLister;
    }

    public void setNegativeOnClickLister(NegativeOnClickLister mNegativeOnClickLister) {
        this.mNegativeOnClickLister = mNegativeOnClickLister;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_dialog_btn_ok:
                mPositiveOnClickLister.onClick(this);
                break;
            case R.id.common_dialog_no_title_btn_ok:
                mPositiveOnClickLister.onClick(this);
                break;
            case R.id.common_dialog_no_title_btn_cancel:
                mNegativeOnClickLister.onClick(this);
                break;
            case R.id.common_dialog_have_title_btn_ok:
                mPositiveOnClickLister.onClick(this);
                break;
            case R.id.common_dialog_have_title_btn_cancel:
                mNegativeOnClickLister.onClick(this);
                break;
            case R.id.common_dialog_bottom_btn_ok:
                mPositiveOnClickLister.onClick(this);
                break;
        }
    }

    /**
     * 确定按钮的回调事件
     */
    public interface PositiveOnClickLister {
        public void onClick(CommonAlertDialog dialog);
    }

    /**
     * 取消按钮的回调事件
     */
    public interface NegativeOnClickLister {
        public void onClick(CommonAlertDialog dialog);
    }
}
