package us.pinguo.album;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import us.pinguo.album.utils.ZoomOutPageTransformer;
import us.pinguo.album.view.SharePicViewDialog;
import us.pinguo.model.PhotoInfoCache;
import us.pinguo.model.PhotoItem;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr 周先森 on 2015/4/15.
 */
public class PhotoDetailActivity extends Activity implements SharePicViewDialog.ShareClickListener, View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private List<PhotoItem> mPhotoItemList;
    private PhotoPagerAdapter mAdapter;
    private SharePicViewDialog mSharePicViewDialog;

    private PhotoItem mCurrentPhotoItem;
    private int mCurrentIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_layout);
        initData();
        initView();
    }

    public void initView(){
        mViewPager = (ViewPager) findViewById(R.id.photo_detail_viewPager);
        //设置ViewPager的切换动画
        mViewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        mAdapter = new PhotoPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentIndex);
        mAdapter.notifyDataSetChanged();
        findViewById(R.id.photo_bottom_view).setOnClickListener(this);
        findViewById(R.id.btn_edit).setOnClickListener(this);
    }

    public void initData(){
        Bundle bundle  = getIntent().getExtras();
        if(bundle!=null){
            mCurrentIndex = bundle.getInt("index");
        }
        PhotoInfoCache photoInfoCache = new PhotoInfoCache();
        mPhotoItemList = photoInfoCache.getPhoto();

        mCurrentPhotoItem = mPhotoItemList.get(mCurrentIndex);
        Log.i("zhouwei","PhotoDetailActivity "+mPhotoItemList.size());
    }

    @Override
    public void onShareItemClick(SharePicViewDialog dialog, int position) {
        Uri uri = null;
        if(mCurrentPhotoItem!=null){
            uri = Uri.fromFile(new File(mCurrentPhotoItem.photoUri));
        }
        if(uri == null){
            return;
        }
        boolean isShareWebExist;
        switch (position){
            case SharePicViewDialog.POSITION_WEIXIN:
              isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.WEIXIN_FRIENDS);
              if (isShareWebExist) {
                  mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                          SharePicViewDialog.ShareWebName.WEIXIN_FRIENDS);
              }
              break;
            case SharePicViewDialog.POSITION_WEIXIN_FRIEND:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.WEIXIN_FRIEDNS_LINES);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.WEIXIN_FRIEDNS_LINES);
                }
                break;
            case SharePicViewDialog.POSITION_QZINE:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.QZONE);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.QZONE);
                }
                break;
            case SharePicViewDialog.POSITION_WEIBO:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.WEIBO);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.WEIBO);
                }
                break;
            case SharePicViewDialog.POSITION_QQ:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.QQ);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.QQ);
                }
                break;
            case SharePicViewDialog.POSITION_SAVE:

                break;
            case SharePicViewDialog.POSITION_MORE:

                break;
      }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photo_bottom_view:
                showShareDialog();
                break;
            case R.id.btn_edit:
                Bundle bundle = new Bundle();
                bundle.putString("path", mCurrentPhotoItem.photoUri);
                Intent intent = new Intent(this, EditPicActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void showShareDialog() {
        if (mSharePicViewDialog == null || !mSharePicViewDialog.isShowing()) {
            mSharePicViewDialog = new SharePicViewDialog(this);
            mSharePicViewDialog.setShareClickListener(this);
            mSharePicViewDialog.show();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        mCurrentPhotoItem = mPhotoItemList.get(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    class PhotoPagerAdapter extends PagerAdapter {
        DisplayImageOptions displayImageOptions;
        /**
         * ImageView 缓存
         */
        Map<Integer,ImageView> viewCache ;
        public  PhotoPagerAdapter(){
            displayImageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)
                    .build();
            viewCache = new HashMap<Integer, ImageView>();
        }
        @Override
        public int getCount() {
            return mPhotoItemList == null ? 0:mPhotoItemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view==o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoItem photoItem = mPhotoItemList.get(position);
            // mCurrentPhotoItem = photoItem;
            Log.i("zhouwei","url:"+photoItem.photoUri);
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.photo_detail_item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.photo_detail_img);
            ImageLoader.getInstance().displayImage("file://"+photoItem.photoUri,new ImageViewAware(imageView),displayImageOptions);
            ((ViewPager)container).addView(view);
            /*ImageView imageView = null;
            if(viewCache.containsKey(position)){
                imageView = viewCache.get(position);
                //    imageView.setImageBitmap(cache.getBitmap(files[position].toString()));
            }else{
                imageView = new ImageView(PhotoDetailActivity.this);
                imageView.setMaxWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setMaxHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageLoader.getInstance().displayImage(photoItem.photoUri,imageView,displayImageOptions);
                viewCache.put(position,imageView);
                ((ViewPager)container).addView(imageView);
            }*/

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // ImageView imageView = (ImageView) object;
            // imageView = null;
        }
    }
}
