package us.pinguo.album;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouwei on 14-9-29.
 */
public class CameraFragment extends Fragment implements View.OnClickListener{
    public String TAG = "CameraFragment";
    private Camera mCamera;
    private CameraPreview mPreview;
    private View mCameraView ;
    /**
     * 自定义调节相机焦距
     */
    private SeekBar seekBar;
    /**
     * 前后摄像头变换按钮
     */
    private ImageView btnSwichCamera;
    private ImageView btnChangeFlash;
    private ImageView btnSetting;

    private PopupWindow pop;
    /**
     * 当前的摄像头id
     */
    private int currentCameraId;

    SurfaceHolder mHolder;
    /** 显示当前拍摄的照片*/
    private ImageView picZoom;
    /**
     * 用SharedPreferences保存当前的分辨率
     */
    private SharedPreferences sp;

    public static final int MEDIA_TYPE_IMAGE = 1;//保存照片
    public static final int MEDIA_TYPE_VIDEO = 2;//保存视频

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if(success){//表示对焦成功
               Log.i(TAG,"对焦成功");
            }else{
              //对焦失败
                Log.w(TAG,"对焦失败");
            }
        }
    };
    /** 通过提供一个静态方法来提供一个Fragment的实例*/
    public static  CameraFragment getInstance(){
        CameraFragment cameraFragment = new CameraFragment();
        return cameraFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("ratio",Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //在onResume里面需要判断mCamera是否为null,如果为null需要重新打开摄像头
        System.out.println("onResume()...."+mCamera);
        reOpenCamera();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("onCreateView()....");
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraView = view;
        boolean isSafeOpen = safeCameraOpenInView(view);
        if(isSafeOpen==false){
            Log.e("CameraFragment","Camera 没有被打开");
            return view;
        }
        btnSwichCamera = (ImageView) view.findViewById(R.id.sw_camera);
        btnChangeFlash = (ImageView) view.findViewById(R.id.setting_flash);
        btnSetting = (ImageView) view.findViewById(R.id.setting_camera);
        seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        picZoom  = (ImageView) view.findViewById(R.id.pic_zoom);
        ImageView btnCapture = (ImageView)view.findViewById(R.id.btn_capture);
        picZoom.setOnClickListener(this);
        setBitmapToImageView();
        btnCapture.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());
        seekBar.setOnTouchListener(new SeekBarOnTouchListener());
        btnSetting.setOnClickListener(this);
        btnChangeFlash.setOnClickListener(this);
        btnSwichCamera.setOnClickListener(this);
        return view;
    }

    /**
     * 打开摄像头
     */
    public void reOpenCamera(){
        if(mCamera==null){
            //重新打开摄像头
            mCamera = Camera.open(currentCameraId);
           // mCamera.setDisplayOrientation(90);
            setCameraDisplayOrientation(getActivity(),currentCameraId,mCamera);
            //取出分辨率
            int width = sp.getInt("width",1280);
            int height = sp.getInt("height",720);
            System.out.println("width: "+width+"  height:"+height);
            Camera.Parameters p = mCamera.getParameters();
            //重新设置分辨率
            p.setPictureSize(width,height);
            p.setPreviewSize(width,height);
            mCamera.setParameters(p);
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }

    }
    /**
     * 初始化一个camera实例,和preview
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//默认打开的是后摄像头
        /** 默认预览的图片为横向，我们需要将显示的Orientation旋转以下*/
        //mCamera.setDisplayOrientation(90);
        setCameraDisplayOrientation(getActivity(),currentCameraId,mCamera);
        qOpened = (mCamera != null);
        mPreview = new CameraPreview(getActivity(), mCamera);
        FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        return qOpened;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.camera = null;
        }
    }

    /**
     * 给ImageView设置图片
     */
    private void setBitmapToImageView(){
        String path = sp.getString("url","");
        if(!path.equals("")){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            picZoom.setImageBitmap(bitmap);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 当Fragment销毁的时候，需要释放相机
         */
        releaseCameraAndPreview();
    }


    @Override
    public void onPause() {
        super.onPause();
        releaseCameraAndPreview();
        System.out.println("onPause()...");
    }
    /**
     * button的点击事件
     */
    @Override
    public void onClick(View v) {
        Camera.Parameters parameters = mCamera.getParameters();
        switch (v.getId()){
            case R.id.setting_flash://闪光灯设置
                if(pop!=null && pop.isShowing()){
                    pop.dismiss();
                }else{
                    View view = getActivity().getLayoutInflater().inflate(R.layout.flash_setting,null);
                    view.findViewById(R.id.flash_on).setOnClickListener(this);
                    view.findViewById(R.id.flash_off).setOnClickListener(this);
                    /** 得到一个popWindow的实例*/
                    pop = PopWindowUtils.getPopWindowInstance(300, ViewGroup.LayoutParams.WRAP_CONTENT,view);
                   /** 设置popWindow的显示位置*/
                    pop.showAsDropDown(btnChangeFlash, 0, 10);
                }
               break;
            case R.id.setting_camera://相机设置
                ArrayList<String> list = new ArrayList<String>();
                List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
                for(int i=0;i<sizes.size();i++){
                    String ratio = sizes.get(i).width+"*"+sizes.get(i).height;
                    list.add(ratio);
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("ratio",list);
                Intent intent = new Intent(getActivity(),SettingActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
                break;

            case R.id.flash_off://关闭闪光灯
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                pop.dismiss();
                break;
            case R.id.flash_on://打开闪光灯
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mCamera.setParameters(parameters);
               pop.dismiss();
                break;
            case R.id.pic_zoom://到相册
                Intent intent1 = new Intent(getActivity(),AlbumActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_capture:
                takePhoto();//拍照
                break;
            case R.id.sw_camera:
                //当前打开的是后摄像头
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    if (mCamera != null) {
                        mCamera.stopPreview();//停止预览
                        releaseCameraAndPreview();//释放资源
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;//重设id
                    }
                } else if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {//当前打开的是前摄像头
                    if (mCamera != null) {
                        mCamera.stopPreview();
                        releaseCameraAndPreview();
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                }
                /** 重启摄像头*/
                reOpenCamera();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==1){
            if(data!=null){
                System.out.println("进入onActivityResult 方法......");
                String result = data.getExtras().getString("result");
                System.out.println("result:"+result);
                String arr[] = result.split("\\*");
                int width = Integer.parseInt(arr[0]);
                int height = Integer.parseInt(arr[1]);
                //保存设置的分辨率到SharedPreferences
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("width",width);
                editor.putInt("height",height);
                editor.commit();
                System.out.println("w："+width+"  h："+height+"==="+mCamera);
                reOpenCamera();
                mCamera.stopPreview();
                Camera.Parameters p = mCamera.getParameters();
                //重新设置分辨率
                p.setPictureSize(width,height);
                p.setPreviewSize(width,height);
                mCamera.setParameters(p);
                mCamera.startPreview();
            }
        }
    }

    /**
     *  保存数据到SharedPreferences
     * @param data
     */
    public void savaDataToSharedPreferences(Map<String,Integer> data){
        for(Map.Entry<String,Integer> entry:data.entrySet()){
            String key = entry.getKey();
            int value = entry.getValue();
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key,value);
            editor.commit();
        }
    }
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
        final String TAG ="CameraPreview";
        Camera camera;
        Context context;
        int screenHeight;//屏幕的高度
        int screenWidth;//屏幕的宽度


         public CameraPreview(Context context,Camera c){
             super(context);
             this.context = context;
             camera = c;
             //安装一个SurfaceHolder.Callback，在surfaceview 创建或销毁的时候得到更新。
             mHolder = getHolder();
             mHolder.addCallback(this);
             //
             mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
         }
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            System.out.println("surfaceCreated..................");
            //surface已经创建，现在告诉相机在哪儿绘制预览view
            try {
                if(camera==null){
                    if(mCamera!=null){
                        camera = mCamera;
                    }else{
                        mCamera = camera = getCameraInstance();
                    }
                   //camera.setDisplayOrientation(90);
                    setCameraDisplayOrientation(getActivity(),currentCameraId,camera);
                }
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i1, int i2, int i3) {
           //如果你的相机可以更改或旋转，在这里注意那些事件
            //保证在重新设置大小，重置格式之前停止预览view
            if(mHolder.getSurface()==null){
                //预览view不存在
                return;
            }
            //在作出改变之前停止预览view
             try {
                 camera.stopPreview();
             }catch (Exception e){

             }
           //在这里设置preView的大小，重置大小
            //摄像头画面显示在Surface上
            if (camera != null) {
                getScreenSize();//获取屏幕的宽高
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                int[] a = new int[sizes.size()];
                int[] b = new int[sizes.size()];
                for (int i = 0; i < sizes.size(); i++) {
                    int supportH = sizes.get(i).height;
                    int supportW = sizes.get(i).width;
                    a[i] = Math.abs(supportW - screenHeight);
                    b[i] = Math.abs(supportH - screenWidth);
                    Log.d(TAG,"supportW:"+supportW+"supportH:"+supportH);
                    System.out.println("------->supportW:"+supportW+"supportH:"+supportH);
                }
                int minW=0,minA=a[0];
                for( int i=0; i<a.length; i++){
                    if(a[i]<=minA){
                        minW=i;
                        minA=a[i];
                    }
                }
                int minH=0,minB=b[0];
                for( int i=0; i<b.length; i++){
                    if(b[i]<minB){
                        minH=i;
                        minB=b[i];
                    }
                }
                Log.d(TAG,"result="+sizes.get(minW).width+"x"+sizes.get(minH).height);
                parameters.setPreviewSize(sizes.get(minW).width,sizes.get(minH).height); // 设置预览图像大小
                parameters.setPictureSize(sizes.get(minW).width,sizes.get(minH).height);//设置图片的大小
                List<Integer> list = parameters.getSupportedPreviewFrameRates();
                parameters.setPreviewFrameRate(list.get(list.size() - 1));
                camera.setParameters(parameters);
                //保存设置的分辨率
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("width",sizes.get(minW).width);
                editor.putInt("height",sizes.get(minH).height);
                editor.commit();
            }

            //用新的设置开启Preview
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                /**
                 * 设置自动对焦
                 * 注意对焦操作要在开启预览后，不然会抛异常
                 *
                 */
               camera.autoFocus(autoFocusCallback);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
           //这个时候应该释放camera
            if(camera!=null){
                camera.stopPreview();
            }
        }

        /**
         * 获取屏幕的高度，宽度
         */
        public void getScreenSize() {
            WindowManager wm = (WindowManager) context.getSystemService(
                    Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
    }

    /**
     * 拍照前。调整图片的方向
     */
     public void takePhoto(){
         MyOrientationDetector   cameraOrientation = new MyOrientationDetector(getActivity());
         if (mCamera != null) {
             mCamera.stopPreview();//设置参数前停止预览
             int orientation = cameraOrientation.getOrientation();
             System.out.println("当前orentation："+orientation);
             Camera.Parameters cameraParameter = mCamera.getParameters();
             cameraParameter.setRotation(90);
             cameraParameter.set("rotation", 90);
             if ((orientation >= 45) && (orientation < 135)) {
                 cameraParameter.setRotation(180);
                 cameraParameter.set("rotation", 180);
             }
             if ((orientation >= 135) && (orientation < 225)) {
                 cameraParameter.setRotation(270);
                 cameraParameter.set("rotation", 270);
             }
             if ((orientation >= 225) && (orientation < 315)) {
                 cameraParameter.setRotation(0);
                 cameraParameter.set("rotation", 0);
             }
             mCamera.setParameters(cameraParameter);
             mCamera.startPreview();//参数设置完成后开启预览
             /**
              * 设置图片旋转后的方位
              */
             //onOrientationChanged();
             mCamera.takePicture(null, null, mPicture);
         }


     }

    /**
     * 设置图片的方位，对通过JPEG {PictureCallback}.返回的图片影响
     */
    public void onOrientationChanged() {
        MyOrientationDetector   cameraOrientation = new MyOrientationDetector(getActivity());
        int orientation = cameraOrientation.getOrientation();
        mCamera.stopPreview();//设置参数前停止预览
        Camera.Parameters cameraParameter = mCamera.getParameters();
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraId, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        cameraParameter.setRotation(rotation);
        cameraParameter.set("rotation",rotation);
        System.out.println("拍照:rotation:"+rotation);
        mCamera.startPreview();//参数设置完成后开启预览
    }
    /**
     * Picture Callback 处理图片的预览和保存到文件
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Toast.makeText(getActivity(), "Image retrieval failed.", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
             //显示图片
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            picZoom.setImageBitmap(bitmap);
            //保存最后照的照片的地址
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("url",pictureFile.toString());
            editor.commit();
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                /**
                 *保存照片后，重启预览
                 */
                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("CameraDemo", "SD卡不可用");
            return null;
        }
        System.out.println("执行到getOutPutMediaFile ...........");
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * 自动聚焦
     */
   public void reAutoFoucus(){
       if(mCamera!=null){
           mCamera.autoFocus(autoFocusCallback);
       }
   }

    /**
     * 显示变焦条
     */
   public void showSeekBar(){
      seekBar.setVisibility(View.VISIBLE);
   }

    /**
     * 隐藏变焦条
     */
    public void hideSeekBar(){
       Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out);
        //设置隐藏动画
        seekBar.setAnimation(animation);
        seekBar.setVisibility(View.GONE);
    }

    /**
     * 方向变化监听器，监听传感器方向的改变
     * @author zw.yan
     *
     */
    public class MyOrientationDetector extends OrientationEventListener {
        int Orientation;
        public MyOrientationDetector(Context context ) {
            super(context );
        }
        @Override
        public void onOrientationChanged(int orientation) {
            Log.i("MyOrientationDetector ","onOrientationChanged:"+orientation);
            this.Orientation=orientation;
            Log.i("MyOrientationDetector","当前的传感器方向为"+orientation);
        }

        public int getOrientation(){
            return Orientation;
        }
    }

    public class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            //这里改变焦距
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.isZoomSupported()){
                /**
                 * rate变焦率
                 * 相机最大的zoom分成100,因为变焦条最大值为100，然后用rate*变焦条的progress
                 */
                float rate = parameters.getMaxZoom()/100f;

                parameters.setZoom((int)(progress*rate));
                mCamera.setParameters(parameters);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     *view的触屏事件
     */
   public class SeekBarOnTouchListener implements View.OnTouchListener {

       @Override
       public boolean onTouch(View view, MotionEvent motionEvent) {
           if(seekBar.getVisibility()!=View.VISIBLE){
               showSeekBar();
           }
           /**
            * 判断是否离开view，如果离开，3秒后隐藏聚焦条
            */
           if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
               /**
                * 延迟3秒后隐藏变焦条
                */
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       hideSeekBar();
                   }
               }, 3000);
           }
           return false;
       }
   }

    /**
     *  设置照片的显示朝向
     * @param activity
     * @param cameraId
     * @param camera
     */
    public  void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
