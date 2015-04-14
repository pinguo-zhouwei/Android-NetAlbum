package us.pinguo;

import android.app.Application;
import android.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import us.pinguo.album.AlbumConstant;
import us.pinguo.album.MyAlbum;

import java.io.File;

/**
 * Created by Mr 周先森 on 2015/4/8.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

        .build();
        ImageLoader.getInstance().init(config);

        MyAlbum.createInstance(this);
        createFile();
    }

    private void createFile(){
        File file = new File(AlbumConstant.SAND_B0X_ROOT);
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.e("zhouwei","创建文件失败");
            }
        }
    }
}
