package us.pinguo.album;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.model.AlbumManager;
import us.pinguo.model.PhotoItem;
import us.pinguo.model.PhotoUploadTask;
import us.pinguo.network.AsyncResult;
import us.pinguo.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/29.
 */
public class NetAlbumActivity extends AsyncTaskActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<PhotoItem> photoItemList = new ArrayList<PhotoItem>();
    private GridView mGridView;
    private GridViewAdapter mAdapter;
    private TextView mHeaderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_album_layout);
        initView();
        flush();
        //同步照片
        sncPhotos();
    }

    private void initView() {
        String userName = MyAlbum.getSharedPreferences().getString("userName", "");
        mHeaderName = (TextView) findViewById(R.id.album_user_name);
        mHeaderName.setText(userName);
        findViewById(R.id.btn_photo_import).setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.net_album_grid);
        mAdapter = new GridViewAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        findViewById(R.id.btn_photo_loginout).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
            photoItemList = dbPhotoTable.queryPhoto();
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void flush() {
        //判断是否有网络
        if (NetworkUtils.hasNet(this)) {
            PhotoUploadTask task = new PhotoUploadTask(this);
            new Thread(task).start();
        } else {
            Toast.makeText(this, R.string.net_nuconnect, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photo_import:
                lauchAlbum();
                break;
            case R.id.btn_photo_loginout:
                loginOut();
                Intent intent = new Intent(this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    public void loginOut() {
        SharedPreferences.Editor editor = MyAlbum.getSharedPreferences().edit();
        editor.putString("userId", "");
        editor.putString("userName", "");
        editor.commit();
    }

    /**
     * 同步服务器照片
     */
    public void sncPhotos() {
        if (!NetworkUtils.hasNet(this)) {
            Toast.makeText(this, R.string.net_nuconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        AlbumManager manager = new AlbumManager(this);
        manager.sncPhoto().get(new AsyncResult<List<PhotoItem>>() {
            @Override
            public void onSuccess(List<PhotoItem> items) {
                Toast.makeText(NetAlbumActivity.this, "照片同步成功", Toast.LENGTH_SHORT).show();
                //  photoItemList = items;
                //  mAdapter.notifyDataSetChanged();
                try {
                    DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
                    photoItemList = dbPhotoTable.queryPhoto();
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(NetAlbumActivity.this, "照片同步失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void lauchAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Uri uri = data.getData();
                //好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String path = cursor.getString(column_index);

                String photoUrl = path;
                Log.i("FUCK", "uri：" + photoUrl);
                PhotoItem item = generateItem(photoUrl);
                photoItemList.add(item);
                mAdapter.notifyDataSetChanged();
                try {
                    DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
                    dbPhotoTable.insert(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                flush();
            }

        }
    }

    public PhotoItem generateItem(String url) {
        PhotoItem photoItem = new PhotoItem();
        photoItem.url = url;
        photoItem.time = String.valueOf(System.currentTimeMillis());
        photoItem.isUpload = 0;
        photoItem.userId = MyAlbum.getSharedPreferences().getString("userId", "");
        return photoItem;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", position);
        bundle.putInt("flag", PhotoDetailActivity.FLAG_NET);
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoItemList.clear();
        photoItemList = null;
    }

    class GridViewAdapter extends BaseAdapter {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.img_defaut)
                .showImageOnLoading(R.drawable.img_defaut)
                .build();

        public GridViewAdapter() {

        }

        @Override
        public int getCount() {
            return photoItemList.size() > 0 ? photoItemList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return photoItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PhotoItem item = photoItemList.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_item_image);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            String url = "";
            if (item.url.startsWith("http://")) {//网络图片
                url = item.url;
            } else {//本地图片
                url = "file://" + item.url;
            }
            ImageLoader.getInstance().displayImage(url, viewHolder.imageView, options);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }

    }
}
