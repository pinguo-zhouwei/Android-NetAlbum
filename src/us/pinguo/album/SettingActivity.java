package us.pinguo.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

/**
 * Created by zhouwei on 14-10-9.
 */
public class SettingActivity extends Activity {
    private List<String> data = null;
    private SharedPreferences sp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("index_config",MODE_PRIVATE);
        getData();
        ListView lv = (ListView) findViewById(R.id.listview);
        final MyAdapter myAdapter = new MyAdapter(this);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.setSelection(position);
                myAdapter.notifyDataSetChanged();
                String ratio = data.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result",ratio);
                intent.putExtras(bundle);
                setResult(1,intent);
                SettingActivity.this.finish();
            }
        });

    }

    /**
     * 获取传递的数据
     */
    public void getData(){
        data = getIntent().getExtras().getStringArrayList("ratio");
    }
    class MyAdapter extends BaseAdapter{
       private Context context;
       private  int currentIndex =-1;
      public MyAdapter(Context c){
         this.context = c;
          currentIndex = sp.getInt("index",-1);
      }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * 设置当前选中的item
         * @param selection
         */
        public void setSelection(int selection){
            this.currentIndex = selection;
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("index",currentIndex);
            editor.commit();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item,null);
                holder.tv = (TextView) convertView.findViewById(R.id.ratio_name);
                holder.img = (ImageView) convertView.findViewById(R.id.check_img);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            holder.tv.setText(data.get(position));
            if(position==currentIndex){
              holder.img.setImageResource(R.drawable.icon_selected_single_press);
            }else{
                holder.img.setImageBitmap(null);
            }
            return convertView;
        }

        class ViewHolder{
            TextView tv;
            ImageView img;
        }
    }
}
