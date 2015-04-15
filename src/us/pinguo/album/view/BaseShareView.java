package us.pinguo.album.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import us.pinguo.album.R;

import java.util.List;

/**
 * Created by zhouwei on 15-1-8.
 */
public class BaseShareView extends Dialog implements AdapterView.OnItemClickListener {
    private GridView mGridView;
    public List<PicItem> mItems;
    private Context mContext;

    public BaseShareView(Context context) {
        super(context, R.style.ThemeDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_vedio_view_layout);
        WindowManager m = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.popup_dialog);
        getWindow().setAttributes(p);
        initView();

    }

    public void initView() {
        mGridView = (GridView) findViewById(R.id.vedio_share_view);
        PicShareAdapter adapter = new PicShareAdapter();
        mGridView.setAdapter(adapter);
        int size = mItems.size();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int allWidth = (int) (95 * size * density);
        int itemWidth = (int) (85 * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.FILL_PARENT);
        mGridView.setLayoutParams(params);
        mGridView.setColumnWidth(itemWidth);
        mGridView.setHorizontalSpacing(0);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setNumColumns(size);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class PicShareAdapter extends BaseAdapter {
        public PicShareAdapter() {
            initData();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PicItem item = mItems.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_share_item, null);
                viewHolder.content = (TextView) convertView.findViewById(R.id.pic_share_item_content);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.pic_share_item_icon);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.imageView.setBackgroundResource(item.resId);
            viewHolder.content.setText(item.content);
            return convertView;
        }

        class ViewHolder {
            TextView content;
            ImageView imageView;
        }
    }

    public void initData() {

    }

    class PicItem {
        PicItem(int resId, String content) {
            this.resId = resId;
            this.content = content;
        }

        public int resId;
        public String content;
    }
}
