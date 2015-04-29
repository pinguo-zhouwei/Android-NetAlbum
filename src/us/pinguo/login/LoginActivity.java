package us.pinguo.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import us.pinguo.album.AsyncTaskActivity;
import us.pinguo.album.NetAlbumActivity;
import us.pinguo.album.R;
import us.pinguo.model.AlbumManager;
import us.pinguo.model.User;
import us.pinguo.network.AsyncResult;

/**
 * Created by Mr 周先森 on 2015/4/28.
 */
public class LoginActivity extends AsyncTaskActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText mUserName;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    public void initView() {
        findViewById(R.id.login_register).setOnClickListener(this);
        findViewById(R.id.btn_user_login).setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.login_user_name);
        mPassword = (EditText) findViewById(R.id.login_pass);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_user_login:
                hideSoftwareKeyboard(mUserName);
                hideSoftwareKeyboard(mPassword);
                userLogin();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String userName = data.getStringExtra("userName");
                mUserName.setText(userName);
            }
        }
    }

    public void userLogin() {
        Log.i(TAG, "haha.........");
        String userName = mUserName.getText().toString().trim();
        String pass = mPassword.getText().toString().trim();
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
                Intent intent = new Intent(LoginActivity.this, NetAlbumActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LoginActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
