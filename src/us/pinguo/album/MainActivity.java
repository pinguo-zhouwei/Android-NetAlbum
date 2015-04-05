package us.pinguo.album;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;


public class MainActivity extends FragmentActivity {
  private FragmentManager fm = null;
  private FragmentTransaction ft = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 如果使用add，必须在这里判断saveInstanceState
         */
        if(savedInstanceState!=null){
            return;
        }

       fm = getFragmentManager();
       ft = fm.beginTransaction();
       CameraFragment fragment = CameraFragment.getInstance();
       ft.add(R.id.main_container,fragment,"CameraFragment");
       ft.commit();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 当点击屏幕的时候进行聚焦
         */
       final  CameraFragment f = (CameraFragment) fm.findFragmentByTag("CameraFragment");
            f.reAutoFoucus();//自动对焦
            f.showSeekBar();//显示变焦条
        //判断是否离开屏幕
        if(event.getAction()==MotionEvent.ACTION_UP){
            /**
             * 延迟3秒后隐藏变焦条
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    f.hideSeekBar();
                }
            },3000);
        }

        return true;
    }


}
