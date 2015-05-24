package us.pinguo.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.LogUtil;
import us.pinguo.album.AsyncTaskActivity;
import us.pinguo.album.NetAlbumActivity;
import us.pinguo.album.R;
import us.pinguo.model.AlbumManager;
import us.pinguo.model.UserInfo;
import us.pinguo.network.AsyncResult;
import us.pinguo.thridlogin.AccessTokenKeeper;
import us.pinguo.thridlogin.Constants;

import java.text.SimpleDateFormat;

/**
 * Created by Mr 周先森 on 2015/4/28.
 */
public class LoginActivity extends AsyncTaskActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText mUserName;
    private EditText mPassword;

    private AuthInfo mAuthInfo;
    /**
     * 登陆认证对应的listener
     */
    private AuthListener mLoginListener = new AuthListener();
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    public void initView() {
        TextView textView = (TextView) findViewById(R.id.title_text_title);
        textView.setText(R.string.login);
        findViewById(R.id.title_back_btn).setOnClickListener(this);
        findViewById(R.id.login_register).setOnClickListener(this);
        findViewById(R.id.btn_user_login).setOnClickListener(this);
        findViewById(R.id.sina_login).setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.login_user_name);
        mPassword = (EditText) findViewById(R.id.login_pass);
        // 创建授权认证信息
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
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
            case R.id.title_back_btn:
                finish();
                break;
            case R.id.sina_login:
                if (null == mSsoHandler && mAuthInfo != null) {
                    mSsoHandler = new SsoHandler(this, mAuthInfo);
                }

                if (mSsoHandler != null) {
                    mSsoHandler.authorize(mLoginListener);
                } else {
                    LogUtil.e(TAG, "Please setWeiboAuthInfo(...) for first");
                }
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
        if (pass.length() < 6 || pass.length() > 20) {
            Toast.makeText(this, "密码应为6-20位字符", Toast.LENGTH_SHORT).show();
            return;
        }
        doLogin(userName, pass);

    }

    public void doLogin(String userName, String password) {
        AlbumManager manager = new AlbumManager(this);
        showProgressDialog();
        attachAsyncTaskResult(manager.userLogin(userName, password), new AsyncResult<UserInfo>() {
            @Override
            public void onSuccess(UserInfo user) {
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
                Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 登入按钮的监听器，接收授权结果。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        new java.util.Date(accessToken.getExpiresTime()));
                //  String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
                //  mTokenView.setText(String.format(format, accessToken.getToken(), date));

                AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    R.string.album, Toast.LENGTH_SHORT).show();
        }
    }
}
