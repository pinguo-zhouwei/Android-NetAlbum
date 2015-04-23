package us.pinguo.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import us.pinguo.album.R;

/**
 * Created by Mr 周先森 on 2015/4/22.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText mEditUserName;
    private EditText mEditPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    private void initView() {
        mEditPass = (EditText) findViewById(R.id.login_pass);
        mEditUserName = (EditText) findViewById(R.id.login_user_name);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
