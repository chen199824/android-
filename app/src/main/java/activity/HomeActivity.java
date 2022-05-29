package activity;

import static android.view.View.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.mobilesafe.R;

import util.Md5Util;
import util.SpUtils;
import util.ToastUtil;

public class HomeActivity extends Activity {
    private GridView vg_home;
    String[] mTitleStr;
    int[] mDrawableIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }

    private void initData() {
        mTitleStr = new String[]{
                "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };
        mDrawableIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe,
                R.drawable.home_apps, R.drawable.home_taskmanager,
                R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };
        vg_home.setAdapter(new MyAdapter());
        vg_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        showDialog();
                        break;
                }
            }
        });

    }

    public void showDialog() {
        //判断本地是否存储密码
        String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            showSetPsdDialog();
        } else {
            showConfirmPsdDialog();
        }
    }

    /**
     * 设置密码对话框
     */
    public void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = inflate(this, R.layout.dialog_set_psd, null);
        //兼容低版本的系统 去除内边距
        dialog.setView(view,0,0,0,0);
        dialog.show();
        
        Button bt_submit =(Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd =(EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();
                if (psd!=null&&confirmPsd!=null){
                    if (psd.equals(confirmPsd)){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        SpUtils.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PSD, Md5Util.encode(psd));
                    }
                    else {
                        ToastUtil.show(getApplicationContext(),"确认密码错误");
                    }
                }else {
                    //提示密码输入为空
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }
            }
        });
        bt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认密码对话框
     */
    public void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = inflate(this, R.layout.dialog_confirm_psd, null);
        //兼容低版本的系统 去除内边距
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit =(Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString();
                if (confirmPsd!=null){
                    if (SpUtils.getString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PSD,"").equals(Md5Util.encode(confirmPsd))){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                    else {
                        ToastUtil.show(getApplicationContext(),"确认密码错误");
                    }
                }else {
                    //提示密码输入为空
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }
            }
        });
        bt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        vg_home = (GridView) findViewById(R.id.vg_home);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int i) {
            return mTitleStr[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = view1.findViewById(R.id.tv_title);
            ImageView iv_icon = view1.findViewById(R.id.iv_icon);

            tv_title.setText(mTitleStr[i]);
            iv_icon.setBackgroundResource(mDrawableIds[i]);

            return view1;
        }
    }
}
