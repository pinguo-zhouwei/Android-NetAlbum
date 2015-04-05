package us.pinguo.album;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import us.pinguo.album.utils.BitmapCache;
import us.pinguo.album.utils.ZoomOutPageTransformer;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouwei on 14-10-11.
 */
public class SeekBigPicFragment extends Fragment{
    ViewPager viewPager;
    MyPagerAdapter adapter;
    File files[];
    BitmapCache cache;
    int currentItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new  File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        files = file.listFiles();
        //获取cache实例
        cache = BitmapCache.getCacheInstance();
        //获取显示的第一张照片的位置
        currentItem = getArguments().getInt("index");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_big_pic,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        //设置ViewPager的切换动画
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        adapter = new MyPagerAdapter();
        System.out.println("当前index："+currentItem);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem);
        adapter.notifyDataSetChanged();
        return view;
    }

    class MyPagerAdapter extends PagerAdapter{
        /**
         * ImageView 缓存
         */
        Map<Integer,ImageView> viewCache ;
        public  MyPagerAdapter(){
            viewCache = new HashMap<Integer, ImageView>();
        }
        @Override
        public int getCount() {
            return files.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view==o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            if(viewCache.containsKey(position)){
               imageView = viewCache.get(position);
           //    imageView.setImageBitmap(cache.getBitmap(files[position].toString()));
            }else{
                imageView = new ImageView(getActivity());
                imageView.setMaxWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setMaxHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setImageBitmap(cache.getBitmap(files[position].toString()));
                viewCache.put(position,imageView);
                ((ViewPager)container).addView(imageView);
            }

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           // ImageView imageView = (ImageView) object;
           // imageView = null;
        }
    }
}
