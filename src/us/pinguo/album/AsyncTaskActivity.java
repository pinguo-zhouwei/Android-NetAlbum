package us.pinguo.album;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import us.pinguo.album.view.CommonProgressDialog;
import us.pinguo.network.AsyncFuture;
import us.pinguo.network.AsyncResult;

/**
 * Created by Mr 周先森 on 2015/4/26.
 */
public class AsyncTaskActivity extends Activity {
    private AsyncFuture<?> mAsyncTaskFuture;

    private CommonProgressDialog mProgressDialog;

    // 取消上一次的异步调用
    protected void cancelPrevAsyncFuture() {
        if (mAsyncTaskFuture != null) {
            mAsyncTaskFuture.cancel(true);
        }
    }

    // 发起异步调用并关联任务结果
    protected <T> void attachAsyncTaskResult(AsyncFuture<T> future, AsyncResult<T> result) {
        cancelPrevAsyncFuture();

        // 记录异步任务对象，以在退出界面时作取消操作
        mAsyncTaskFuture = future;
        future.get(result); // TODO: AsyncResult参数如果不设置模板类型呢？
    }

    public void showProgressDialog() {
        mProgressDialog = new CommonProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelPrevAsyncFuture();

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 隐藏软件盘
     *
     * @param input
     */
    public void hideSoftwareKeyboard(EditText input) {
        //隐藏光标
        input.setCursorVisible(false);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
