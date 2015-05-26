package us.pinguo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Mr 周先森 on 2015/5/24.
 */
public class SelectSkinActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView mListView;
    private SkinAdapter mAdapter;
    private int[] mColorArray = new int[]{
            0xFFA6E2CE
            , 0xFFFFC4C4
            , 0xFFEBC2EA
            , 0xFFFFE1A3
            , 0xFFFFCAB8
            , 0xFFA6D6FB};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_skin_layout);
        init();
    }

    private void init() {
        TextView textView = (TextView) findViewById(R.id.title_text_title);
        textView.setText("更换封面");
        findViewById(R.id.title_back_btn).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.skin_list);
        mListView.setOnItemClickListener(this);
        mAdapter = new SkinAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_back_btn) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt("color", mColorArray[position]);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    class SkinAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mColorArray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.skin_layout_item, null);
                viewHolder.skinColor = (TextView) convertView.findViewById(R.id.skin_color);
                viewHolder.skinName = (TextView) convertView.findViewById(R.id.skin_name);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.skinName.setText("模板" + (position + 1));
            viewHolder.skinColor.setText((position + 1) + "");
            viewHolder.skinColor.setBackgroundColor(mColorArray[position]);
            return convertView;
        }

        class ViewHolder {
            TextView skinColor;
            TextView skinName;
        }
    }
}
