package us.pinguo.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import us.pinguo.album.AsyncTaskActivity;
import us.pinguo.album.R;
import us.pinguo.model.AlbumManager;
import us.pinguo.network.AsyncResult;
import us.pinguo.utils.NetworkUtils;

/**
 * Created by Mr 周先森 on 2015/4/22.
 */
public class RegisterActivity extends AsyncTaskActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private EditText mEditUserName;
    private EditText mEditPass;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
    }

    private void initView() {
        TextView textView = (TextView) findViewById(R.id.title_text_title);
        textView.setText(R.string.register);
        findViewById(R.id.title_back_btn).setOnClickListener(this);
        mEditPass = (EditText) findViewById(R.id.register_pass);
        mEditUserName = (EditText) findViewById(R.id.register_user_name);
        mBtnLogin = (Button) findViewById(R.id.btn_register_login);
        mBtnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "haha.........");
        switch (v.getId()) {
            case R.id.btn_register_login:
                hideSoftwareKeyboard(mEditPass);
                hideSoftwareKeyboard(mEditUserName);
                userRegister();

                break;
            case R.id.title_back_btn:
                finish();
                break;
        }
    }

    public void userRegister() {
        //判断是否有网络
        if (!NetworkUtils.hasNet(this)) {
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "haha.........");
        String userName = mEditUserName.getText().toString().trim();
        String pass = mEditPass.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
            return;
        }

        doRegister(userName, pass);

    }

    public void doRegister(String userName, String password) {
        AlbumManager manager = new AlbumManager(this);
        showProgressDialog();
        attachAsyncTaskResult(manager.userRegister(userName, password), new AsyncResult<String>() {
            @Override
            public void onSuccess(String userName) {
                hideProgressDialog();
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                RegisterActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                hideProgressDialog();
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
