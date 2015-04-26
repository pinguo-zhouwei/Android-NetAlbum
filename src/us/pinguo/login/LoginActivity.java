package us.pinguo.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import us.pinguo.album.AsyncTaskActivity;
import us.pinguo.album.R;
import us.pinguo.model.AlbumManager;
import us.pinguo.model.User;
import us.pinguo.network.AsyncResult;

/**
 * Created by Mr 周先森 on 2015/4/22.
 */
public class LoginActivity extends AsyncTaskActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText mEditUserName;
    private EditText mEditPass;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    private void initView() {
        mEditPass = (EditText) findViewById(R.id.login_pass);
        mEditUserName = (EditText) findViewById(R.id.login_user_name);
        mBtnLogin = (Button) findViewById(R.id.btn_user_login);
        mBtnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "haha.........");
        switch (v.getId()) {
            case R.id.btn_user_login:
                hideSoftwareKeyboard(mEditPass);
                hideSoftwareKeyboard(mEditUserName);
                userLogin();

                break;
        }
    }

    public void userLogin() {
        Log.i(TAG, "haha.........");
        String userName = mEditUserName.getText().toString().trim();
        String pass = mEditPass.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
            return;
        }

        doLogin(userName, pass);

    }

    public void doLogin(String userName, String password) {
        AlbumManager manager = new AlbumManager(this);
        showProgressDialog();
        attachAsyncTaskResult(manager.userLogin(userName, password), new AsyncResult<User>() {
            @Override
            public void onSuccess(User user) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                if (user != null) {
                    Log.i(TAG, "UserName:" + user.userName + "  pass:" + user.password);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
