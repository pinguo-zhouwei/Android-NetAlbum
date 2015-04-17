package us.pinguo.album;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import us.pinguo.album.effect.PictureSpecialEffects;
import us.pinguo.album.view.HorizontalListView;

/**
 * Created by Mr 周先森 on 2015/4/17.
 */
public class EditPicActivity extends Activity implements AdapterView.OnItemClickListener {
    private HorizontalListView mEffectListView;

    private EffectAdapter mAdapter;
    private String[] mEffectList = new String[]{"怀旧", "锐化", "黑白"};
    private String mPath;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pic_layout);
        initView();
    }

    public void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPath = bundle.getString("path");
        }
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        mImageView = (ImageView) findViewById(R.id.edit_source_img);
        ImageLoader.getInstance().displayImage("file://" + mPath, mImageView);
        mEffectListView = (HorizontalListView) findViewById(R.id.special_effect_list);
        mAdapter = new EffectAdapter();
        mEffectListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mEffectListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            ImageLoader.getInstance().loadImage("file://" + mPath, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage != null) {
                        Bitmap bitmap = PictureSpecialEffects.getRememberEffect(loadedImage);
                        mImageView.setImageBitmap(bitmap);
                    }

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

        }
    }

    class EffectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEffectList.length;
        }

        @Override
        public Object getItem(int position) {
            return mEffectList[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.effect_list_item, null);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.effect_item_name);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tvName.setText(mEffectList[position]);

            return convertView;
        }

        class ViewHolder {
            TextView tvName;
        }
    }
}
