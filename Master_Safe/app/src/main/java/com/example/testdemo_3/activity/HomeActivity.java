package com.example.testdemo_3.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testdemo_3.R;

public class HomeActivity extends AppCompatActivity {

    /**
     * 存储标题
     */
    private String[] mTitleStrs;

    /**
     * 存储图像
     */
    private int[] mDrawableIds;

    /**
     * 网格对象
     */
    private GridView gv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 初始化UI
        initUI();

        // 初始化数据
        initData();
    }

    /**
     * 1.初始化UI
     */
    private void initUI() {
        gv_home = findViewById(R.id.gv_home);
    }

    /**
     * 2.初始化数据
     */
    private void initData() {
        // 1.初始化每个图标的标题
        mTitleStrs = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        // 2.初始化每个图标的图像
        mDrawableIds = new int[]{R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps, R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan, R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};
        // 3.为GridView设置数据适配器
        gv_home.setAdapter(new MyAdapter());
        // 4.注册GridView中单个条目的点击事件
        gv_home.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            switch (position) {
                case 0:
                    //手机防盗界面
                    break;
                case 1:
                    //通信卫士界面
                    break;
                case 2:
                    //软件管理界面
                    break;
                case 3:
                    //进程控制界面
                    startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));

                    break;
                case 4:
                    //流量统计界面
                    break;
                case 5:
                    //手机杀毒界面
                    startActivity(new Intent(getApplicationContext(),AnitVirusActivity.class));
                    break;
                case 6:
                    //缓存清理界面
                    break;
                case 7:
                    //高级工具界面
                    break;
                case 8:
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 3.自定义的数据适配器类
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 统计条目的总数
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            // 根据索引获取对象
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            // 获取索引
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 获取视图
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = view.findViewById(R.id.tv_title);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}


