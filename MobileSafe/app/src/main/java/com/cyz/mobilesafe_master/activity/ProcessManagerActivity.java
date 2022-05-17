package com.cyz.mobilesafe_master.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cyz.db.domain.ProcessInfo;
import com.cyz.mobilesafe_engine.ProcessInfoProvider;
import com.cyz.mobilesafe_master.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends Activity implements View.OnClickListener {
    TextView tv_process_count, tv_memory_info;
    ListView lv_process_list;
    Button bt_select_all, bt_clean, bt_select_reverse, bt_setting;
    int mProcessCount;
    List<ProcessInfo> mProcessInfoList;
    ArrayList<ProcessInfo> mSystemList;
    ArrayList<ProcessInfo> mCustomerList;
    MyAdapter mAapter;


    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            mAapter = new MyAdapter();
            lv_process_list.setAdapter(mAapter);
        };
    };

    class MyAdapter extends BaseAdapter{

        //获取数据适配器中条目总数，修改为两种（纯文本，图片+文字）
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        //指定索引指向的条目类型，条目类型状态码指定（0（复用系统），1）
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size()+1){
                return 0;//返回0，代表纯文本条目的状态码
            }else {
             return 1;//代表图片+文字条目状态码
            }
        }

        //listview中添加两个描述条目
        @Override
        public int getCount() {
            return mCustomerList.size()+mSystemList.size()+2;
        }

        @Override
        public ProcessInfo getItem(int position) {
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
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            int type = getItemViewType(position);
            System.out.println(type);
            System.out.println(view);
            if (type == 0){
                //展示灰色纯文本条目
                ViewTitleHolder holder = null;
                if (view  == null){
                    view = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView)view.findViewById(R.id.tv_title);
                    System.out.println(holder.tv_title);
                    view.setTag(holder);
                }else {
                    holder = (ViewTitleHolder)view.getTag();
                }
                if (position == 0){
                    holder.tv_title.setText("用户进程（"+mCustomerList.size()+ ")");
                }else {
                    holder.tv_title.setText("系统进程（"+mSystemList.size()+ ")");
                }
                return view;
            } else {
                //展示图片+文字
                ViewHolder holder = null;
                if (view == null){
                    view = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    System.out.println(holder);
                    System.out.println(view);
                    System.out.println(R.id.iv_icon);
                    holder.iv_icon = (ImageView)view.findViewById(R.id.iv_icon);
                    System.out.println(holder.iv_icon);
                    holder.tv_name = (TextView)view.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView)view.findViewById(R.id.tv_memory_info);
                    holder.cb_box = (CheckBox)view.findViewById(R.id.cb_box);
                    view.setTag(holder);
                }else {
                    holder = (ViewHolder)view.getTag();
                    System.out.println(holder);
                }
                System.out.println(getItem(position).icon);
                System.out.println(holder);
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                holder.tv_memory_info.setText(strSize);
                //本应用不能被选中，将checkbox隐藏
                if (getItem(position).packageName.equals(getPackageManager())){
                    holder.cb_box.setVisibility(View.GONE);
                }else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);

                return view;
            }
        }


    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }
    static class ViewTitleHolder{
        TextView tv_title;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initUI();
        initTitleData();
        initListData();
    }

    private void initListData() {
        getData();
    }

    private void getData(){
        new Thread(){
            public void run(){
                // 2.准备填充ListView中数据适配器的数据
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplication());
                mSystemList = new ArrayList<ProcessInfo>();
                mCustomerList = new ArrayList<ProcessInfo>();

                for (ProcessInfo info : mProcessInfoList){
                    if (info.isSystem){
                        //系统进程
                        mSystemList.add(info);
                    }else {
                        //用户进程
                        mCustomerList.add(info);
                    }
                }
                // 3.发送空消息进行数据绑定
                mHandler.sendEmptyMessage(0);
            };
        }.start();
    }


    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数:"+mProcessCount);

        //获取可用内存大小并且格式化
        long availSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, availSpace);
        //获取总共内存大小并且格式化
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, totalSpace);

        tv_memory_info.setText("剩余/总共:"+strAvailSpace+"/"+strTotalSpace);
    }

    private void initUI() {
        tv_process_count = (TextView)findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView)findViewById(R.id.tv_memory_info);
        lv_process_list = (ListView) findViewById(R.id.lv_process_list);
        bt_select_all = (Button) findViewById(R.id.bt_select_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
        bt_clean = (Button) findViewById(R.id.bt_clean);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clean.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
