package us.pinguo.album;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import us.pinguo.album.utils.BitmapCache;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.model.PhotoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/29.
 */
public class NetAlbumActivity extends AsyncTaskActivity implements View.OnClickListener {
    private List<PhotoItem> photoItemList = new ArrayList<PhotoItem>();
    private GridView mGridView;
    private GridViewAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_album_layout);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_photo_import).setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.net_album_grid);
        mAdapter = new GridViewAdapter();
        mGridView.setAdapter(mAdapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photo_import:
                lauchAlbum();
                break;
        }
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
                Uri uri = data.getData();
                String photoUrl = uri.toString();
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
            }

        }
    }

    public PhotoItem generateItem(String url) {
        PhotoItem photoItem = new PhotoItem();
        photoItem.photoUri = url;
        photoItem.time = String.valueOf(System.currentTimeMillis());
        photoItem.isUpload = 0;
        return photoItem;
    }

    class GridViewAdapter extends BaseAdapter {
        BitmapCache cache;

        public GridViewAdapter() {
            cache = BitmapCache.getCacheInstance();
        }

        @Override
        public int getCount() {
            return photoItemList.size();
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
            ImageLoader.getInstance().displayImage(item.photoUri, viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }

    }
}
