package com.example.testdemo_3.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.testdemo_3.R;
import com.example.testdemo_3.db.dao.BlackNumberDao;
import com.example.testdemo_3.db.domain.BlackNumberInfo;
import com.example.testdemo_3.untils.ToastUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//1，复用convertView
//2，对findViewById次数优化，使用ViewHolder
//3，将ViewHolder定义成静态的，不会去创建多个对象
//4，ListView如果有多个条目的时候，可以做分页算法，每一次显示加载20条，逆序返回
//
public class BlackNumberActivity extends Activity {
    private Button bt_add;
    private ListView lv_blacknumber;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private MyAdapter mAdapter;
    private int mMode = 1;
    private boolean mIsLoad = false;
    private int mCount;

    private  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 4.告知ListView可以去设置数据适配器了
            if (mAdapter == null){
                // 这里做一个非空判断，如果为空才创建，避免重复创建
                mAdapter = new MyAdapter();
                // 5.配置适配器
                lv_blacknumber.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }
        };
    };
    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }
        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
//            if(convertVIew == null){
//                view = View.inflate(getApplicationContext(),R.layout.listview_blacknumber_item,null);
//            }else{
//                view = convertView;
//            }

            //1，复用convertView

            //复用ViewHolder步骤一

//            ViewHolder viewHolder = null;
            ViewHolder holder = null;
            if (convertView == null){
                convertView = View.inflate(getApplicationContext(),R.layout.listview_blacknumber_item,null);
                //2,减少findViewById()次数
                //复用ViewHolder步骤三
                holder = new ViewHolder();
                //复用ViewHolder步骤四
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                //复用ViewHolder步骤五
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }



            // "删除"按钮的点击事件
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 1.数据库中的删除
                    mDao.delete(mBlackNumberList.get(position).getPhone());
                    // 2.集合中的删除
                    mBlackNumberList.remove(position);
                    // 3.通知适配器更新
                    if (mAdapter != null){
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            holder.tv_phone.setText(mBlackNumberList.get(position).phone);
            //tv_mode.setText(mBlackNumberList.get(position).mode);
            int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
            switch (mode){
                case 1:holder.tv_mode.setText("拦截短信");
                    break;
                case 2:holder.tv_mode.setText("拦截电话");
                    break;
                case 3:holder.tv_mode.setText("拦截所有");
                    break;
            }
            return convertView;
        }
    }
    //复用ViewHolder步骤二
    static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);

        iniUI();
        iniData();
    }

    private void iniData() {
        //获取数据库中所有的电话号码
        new Thread(){
            @Override
            public void run() {
                //1，获取操作黑名单数据库的对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                //2，查询部分数据
                mBlackNumberList = mDao.find(0);
                mCount = mDao.getCount();

                //3，通过消息机制告知主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
                super.run();
            }
        }.start();
    }
/*    private void loadData(int index) {
        //1，获取操作黑名单数据库的对象
        mDao = BlackNumberDao.getInstance(getApplicationContext());
        //2，查询部分数据
        mBlackNumberList = mDao.find(index);
        //3，通过消息机制告知主线程可以去使用包含数据的集合
        mHandler.sendEmptyMessage(0);
        super.run();
    }*/

    private void iniUI() {
        bt_add = (Button) findViewById(R.id.bt_add);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);

        bt_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        //监听其滚动状态
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener(){
            //滚动过程中状态发生改变调用方法
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                AbsListView.OnScrollListener.SCROLL_STATE_FLING    飞速滚动状态
//                AbsListView.OnScrollListener.SCROLL_STATE_IDLE      空闲状态
//                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL    拿手触摸滚动状态

                if (mBlackNumberList !=null){
                    //条件一：滚动到停止状态
                    //条件二：最后一个条目可见(最后一个条目的索引值>=数据适配器中集合的总条目数-1)
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1
                            && !mIsLoad){
                       /* mIsLoad防止重复加载的变量
                        如果当前正在加载mIsLoad就会变成true，本次加载完毕之后，再将mIsLoad改为false
                        如果下一次加载需要执行时，会使用mIsLoad进行判断，如果为true，则需要等待上一次加载完成，将其值改为false后才能加载*/

                        //如果条目总数大小大于集合大小的时候，才可以去继续加载更多
                        if (mCount>mBlackNumberList.size()){
                            new Thread(){
                                @Override
                                public void run() {
                                    //1，获取操作黑名单数据库的对象
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    //2，查询部分数据
                                    List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
                                    //3，添加下一页数据的过程
                                    mBlackNumberList.addAll(moreData);
                                    //4，通知适配器刷新
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }

            }
            //滚动过程中调用方法
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }
    protected void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view =View.inflate(getApplicationContext(),R.layout.dialog_add_blacknumber,null);
        dialog.setView(view,0,0,0,0);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

        Button bt_submit = (Button) view.findViewById(R.id.btn_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.btn_cancel);

        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        // 拦截短信
                        mMode = 1;
                        break;
                    case R.id.rb_phone:
                        // 拦截电话
                        mMode = 2;
                        break;
                    case R.id.rb_all:
                        // 拦截所有
                        mMode = 3;
                        break;

                }
            }
        });

        // “提交”按钮的点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.获取输入框中的电话号码
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    // 2.数据库插入——当前输入的拦截电话号码
                    mDao.insert(phone,mMode + "");
                    // 3.让数据库和集合保持同步（1.数据库中的数据重新读一遍；2.手动向集合中添加对象(插入数据构建的对象)）
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setPhone(phone);
                    blackNumberInfo.setMode(mMode + "");
                    // 4.将对象插入到集合的顶部
                    mBlackNumberList.add(0,blackNumberInfo);
                    // 5.通知数据适配器刷新（数据适配器中的集合发生改变）
                    if(mAdapter != null){
                        mAdapter.notifyDataSetChanged();
                    }
                    // 6.关闭对话框
                    dialog.dismiss();
                }else {
                    ToastUtil.show(getApplicationContext(),"请输入拦截号码");
                }
            }
        });

        // "取消"按钮的点击事件
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭对话框
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
