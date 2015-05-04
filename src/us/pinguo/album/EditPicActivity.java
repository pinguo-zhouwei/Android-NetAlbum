package us.pinguo.album;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import us.pinguo.album.effect.*;
import us.pinguo.album.utils.BitmapUtils;
import us.pinguo.album.view.EffectImageView;
import us.pinguo.album.view.HorizontalListView;
import us.pinguo.model.Storage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Mr 周先森 on 2015/4/17.
 */
public class EditPicActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = EditPicActivity.class.getSimpleName();
    private HorizontalListView mEffectListView;

    private EffectAdapter mAdapter;
    private String[] mEffectList = new String[]{"怀旧", "模糊", "浮雕", "底片", "黑白"};
    private String mPath;
    private EffectImageView mImageView;

    private Bitmap mSourceBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pic_layout);
        initView();
    }

    public void initView() {
        TextView textView = (TextView) findViewById(R.id.title_text_title);
        textView.setText(R.string.edit_pic);
        findViewById(R.id.title_back_btn).setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPath = bundle.getString("path");
        }
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        mImageView = (EffectImageView) findViewById(R.id.edit_source_img);
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
                    mImageView.setImageBitmap(loadedImage);
                    mSourceBitmap = loadedImage;
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        mEffectListView = (HorizontalListView) findViewById(R.id.special_effect_list);
        mAdapter = new EffectAdapter();
        mEffectListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mEffectListView.setOnItemClickListener(this);
        findViewById(R.id.btn_effect_img_save).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mAdapter.setCurrentPos(position);
        if (mSourceBitmap == null) {
            return;
        }
        Bitmap bitmap = null;
        switch (position) {
            case 0://怀旧
                Log.i("FFF", "【特效--怀旧】");
                bitmap = PictureSpecialEffects.getRememberEffect(mSourceBitmap);
                mImageView.setImageBmp(bitmap);
                break;
            case 1://模糊
                Log.i("FFF", "【特效--模糊】");
                Bitmap bmp = BlurImageEffect.blurImageAmeliorate(mSourceBitmap);
                mImageView.setImageBitmap(bmp);
                break;
            case 2://浮雕
                Log.i("FFF", "【特效--浮雕】");
                bitmap = ReliefImageEffect.getReliefBitmap(mSourceBitmap);
                mImageView.setImageBmp(bitmap);
                break;
            case 3://底片
                Log.i("FFF", "【特效--底片】");
                bitmap = NegativeImageEffect.getReliefBitmap(mSourceBitmap);
                mImageView.setImageBmp(bitmap);
                break;
            case 4://黑白
                Log.i("FFF", "【特效--黑白】");
                bitmap = BlackImageEffect.getBlackBitmap(mSourceBitmap);
                mImageView.setImageBmp(bitmap);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_effect_img_save:
                Bitmap bitmap = mImageView.getBitmap();
                if (bitmap == null) {
                    return;
                }
                String path = generatePicPath();
                if (path == null) {
                    return;
                }
                try {
                    BitmapUtils.saveBitmap(path, bitmap, 100);
                    Toast.makeText(this, R.string.save_pic_path, Toast.LENGTH_SHORT).show();
                    Storage.addStorage(this, path);//插入媒体数据库
                    this.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.title_back_btn:
                finish();
                break;
        }
    }

    private String generatePicPath() {
        File file = new File(AlbumConstant.EFFECT_PIC_PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.i(TAG, "create file failed");
                return null;
            }
        }
        return file.getAbsolutePath() + "/" + "effect_img_" + System.currentTimeMillis() + ".jpg";
    }

    class EffectAdapter extends BaseAdapter {
        private int currentPos = -1;

        public void setCurrentPos(int currentPos) {
            this.currentPos = currentPos;
            this.notifyDataSetChanged();
        }

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
            if (currentPos == position) {
                viewHolder.tvName.setBackgroundColor(Color.YELLOW);
            } else {
                viewHolder.tvName.setBackgroundResource(R.color.remember);
            }
            return convertView;
        }

        class ViewHolder {
            TextView tvName;
        }
    }
}
