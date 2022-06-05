package com.example.manage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.manage.R;
import com.example.manage.AppInfoDao;
import com.example.manage.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

    private ListView lv_app_list;

    private List<AppInfo> mAppInfoList;

    private MyAdapter mAdapter;

    // 系统应用所在的集合
    private List<AppInfo> mSystemList;

    // 用户应用所在的集合
    private List<AppInfo> mCustomerList;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 4.使用数据适配器
            mAdapter = new MyAdapter();
            lv_app_list.setAdapter(mAdapter);
        }
    };

    class MyAdapter extends BaseAdapter{

        // 在ListView中多添加一种类型条目，条目总数有2种
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        // 根据索引值决定展示哪种类型的条目
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1){
                // 纯文本条目
                return 0;
            }else {
                // 图文条目
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mCustomerList.size() + mSystemList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size() + 1){
                // 纯文本条目
                return null;
            }else {
                // 图文条目
                if (position < mCustomerList.size() + 1){
                    // 用户应用
                    return mCustomerList.get(position - 1);
                }else {
                    // 系统应用
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }
            // return mAppInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 判断当前索引指向的条目类型状态码
            int itemViewType = getItemViewType(position);
            if (itemViewType == 0){
                // 纯文本条目
                ViewTitleHolder holder = null;
                if (convertView == null){
                    convertView = View.inflate(getApplicationContext(),R.layout.list_app_item_text,null);
                    holder = new ViewTitleHolder();
                    holder.tv_title_des = convertView.findViewById(R.id.tv_title_des);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0){
                    holder.tv_title_des.setText("用户应用("+mCustomerList.size()+")");
                }else {
                    holder.tv_title_des.setText("系统应用("+mSystemList.size()+")");
                }
                return convertView;
            }else {
                // 图文条目
                ViewHolder holder = null;
                // 1.判断ConverView是否为空
                if (convertView == null){
                    convertView = View.inflate(getApplicationContext(),R.layout.list_app_item,null);
                    // 2.获取控件实例赋值给ViewHolder
                    holder = new ViewHolder();
                    holder.iv_icon_application = convertView.findViewById(R.id.iv_icon);
                    holder.tv_app_name = convertView.findViewById(R.id.tv_app_name);
                    holder.tv_app_location = convertView.findViewById(R.id.tv_app_location);
                    // 3.将Holder放到ConverView上
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                // 4.获取Holder中的控件实例，赋值
                holder.iv_icon_application.setBackground(getItem(position).getIcon());
                holder.tv_app_name.setText(getItem(position).getName());
                // 5.显示应用安装的位置
                if (getItem(position).isSdCard()){
                    holder.tv_app_location.setText("sd卡应用");
                }else {
                    holder.tv_app_location.setText("内存应用");
                }
                // 6.返回现有条目填充上数据的View对象
                return convertView;
            }
        }
    }

    static class ViewHolder{
        ImageView iv_icon_application;
        TextView tv_app_name;
        TextView tv_app_location;
    }

    static class ViewTitleHolder{
        TextView tv_title_des;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        // 初始化磁盘 & SD卡可用大小
        initTitle();

        // 初始化ListView
        intiListView();

    }

    /**
     * 初始化磁盘 & SD卡可用大小
     */
    private void initTitle() {
        // 0.初始化控件
        TextView tv_memory = findViewById(R.id.tv_memory);
        TextView tv_sd_memory = findViewById(R.id.tv_sd_memory);
        // 1.获取磁盘（内存，区分于手机运行内存）可用大小，磁盘路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        // 2.获取SD卡可用大小，SD卡路径
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 3.获取以上两个路径下文件夹的可用大小
        long space = getAvailSpace(path);
        long sdspace = getAvailSpace(sdpath);
        // 4.对bytes为单位的数值格式化
        String strSpace = Formatter.formatFileSize(this, space);
        String strSdSpace = Formatter.formatFileSize(this, sdspace);
        // 5.给控件赋值
        tv_memory.setText("磁盘可用：" + strSpace);
        tv_sd_memory.setText("sd卡可用：" + strSdSpace);
    }

    /**
     * 计算文件夹的可用大小
     * @param path 路径名
     * @return 可用空间大小，单位为byte=8bit
     */
    private long getAvailSpace(String path) {
        // 获取可用磁盘大小的对象
        StatFs statFs = new StatFs(path);
        // 获取可用区块的个数
        long availableBlocks = statFs.getAvailableBlocks();
        // 获取区块的大小
        long blockSize = statFs.getBlockSize();
        // 可用空间大小 = 区块大小 * 可用区块个数
        return availableBlocks * blockSize;
    }

    /**
     * 初始化ListView
     */
    private void intiListView() {
        // 1.初始化控件
        lv_app_list = findViewById(R.id.lv_app_list);
        new Thread(){
            @Override
            public void run() {
                // 2.准备填充ListView中数据适配器的数据
                mAppInfoList = AppInfoDao.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<>();
                mCustomerList = new ArrayList<>();
                // 分割集合
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem()){
                        mSystemList.add(appInfo);
                    }else {
                        mCustomerList.add(appInfo);
                    }
                }
                // 3.发送空消息进行数据绑定
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
