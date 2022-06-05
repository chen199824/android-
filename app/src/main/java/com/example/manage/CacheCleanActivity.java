package com.example.manage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.manage.R;
import com.example.manage.CacheInfo;

import java.lang.reflect.Method;
import java.util.List;

public class CacheCleanActivity extends AppCompatActivity {

    private static final int UPDATE_CACHE_APP = 100;
    private static final int CHECK_CACHE_APP = 101;
    private static final int CHECK_FINISH = 102;
    private static final int CLEAR_FINISH = 103;
    private Button btn_clear;
    private ProgressBar pb_bar;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private PackageManager mPm;
    private int index = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_CACHE_APP:
                    // 9.在线性布局中添加有缓存应用的条目
                    View view = View.inflate(getApplicationContext(), R.layout.list_cache_item, null);

                    ImageView iv_icon_application = view.findViewById(R.id.iv_icon_application);
                    TextView tv_app_name = view.findViewById(R.id.tv_app_name);
                    TextView tv_memory = view.findViewById(R.id.tv_memory);
                    ImageView iv_delete = view.findViewById(R.id.iv_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_icon_application.setBackground(cacheInfo.getIcon());
                    tv_app_name.setText(cacheInfo.getName());
                    tv_memory.setText(Formatter.formatFileSize(getApplicationContext(),cacheInfo.getCacheSize()));

                    ll_add_text.addView(view,0);

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 清除单项条目对应的应用的缓存内容
                            try {
                                Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                                // 2.获取调用方法对象
                                Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                // 3.获取对象调用方法
                                method.invoke(mPm, cacheInfo.getPackageName(), new IPackageDataObserver.Stub() {
                                    @Override
                                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                                        // 删除此应用缓存后，调用在子线程中

                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    /* 清理单项应用缓存的第二种方式
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.parse("package:" + cacheInfo.getPackageName()));
                    startActivity(intent);
                     */
                    break;
                case CHECK_CACHE_APP:
                    tv_name.setText((String)msg.obj);
                    break;
                case CHECK_FINISH:
                    tv_name.setText("扫描完成");
                    break;
                case CLEAR_FINISH:
                    // 从线性布局中移除所有的条目
                    ll_add_text.removeAllViews();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);

        // 初始化UI
        initUI();

        // 初始化数据
        initData();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        btn_clear = findViewById(R.id.btn_clear);
        pb_bar = findViewById(R.id.pb_bar);
        tv_name = findViewById(R.id.tv_name);
        ll_add_text = findViewById(R.id.ll_add_text);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.获取指定类的字节码文件
                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    // 2.获取调用方法对象
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    // 3.获取对象调用方法
                    method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            // 清除缓存完成后调用（考虑权限），运行在子线程中
                            Message message = Message.obtain();
                            message.what = CLEAR_FINISH;
                            mHandler.sendMessage(message);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 遍历手机中所有的应用，获取有缓存的应用，用作显示
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                // 1.获取包的管理者对象
                mPm = getPackageManager();
                // 2.获取安装在手机上的所有应用
                List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
                // 3.给进度条设置最大值（手机中所有应用的总数）
                pb_bar.setMax(installedPackages.size());
                // 4.遍历每一个应用，获取有缓存的应用（应用名称、图标、缓存大小、包名）
                for (PackageInfo packageInfo : installedPackages) {
                    String packageName = packageInfo.packageName; // 包名作为获取缓存信息的条件
                    getPackageCache(packageName);
                    pb_bar.setProgress(index++);
                    // 每循环一次就将检测应用的名称发送给主线程显示
                    Message message = Message.obtain();
                    message.what = CHECK_CACHE_APP;
                    String name = null;
                    try {
                        name = mPm.getApplicationInfo(packageName,0).loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    message.obj = name;
                    mHandler.sendMessage(message);
                }
                Message message = Message.obtain();
                message.what = CHECK_FINISH;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 获取应用的缓存大小
     * @param packageName 应用包名
     */
    @TargetApi(26)
    private void getPackageCache(String packageName) {
        // 0.创建IPackageStatsObserver对象
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                // 4.获取指定包名的缓存大小的过程，子线程中的代码，不能处理UI
                long cacheSize = pStats.cacheSize;
                // 5.判断缓存大小是否大于0
                if (cacheSize > 0) {
                    CacheInfo cacheInfo = null;
                    // 6.告知主线程更新UI
                    Message message = Message.obtain();
                    message.what = UPDATE_CACHE_APP;
                    try {
                        // 7.维护有缓存应用的Java Bean
                        cacheInfo = new CacheInfo();
                        cacheInfo.setCacheSize(cacheSize);
                        cacheInfo.setPackageName(pStats.packageName);
                        cacheInfo.setName(mPm.getApplicationInfo(pStats.packageName,0).loadLabel(mPm).toString());
                        cacheInfo.setIcon(mPm.getApplicationInfo(pStats.packageName,0).loadIcon(mPm));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    message.obj = cacheInfo;
                    // 8.发送消息
                    mHandler.sendMessage(message);
                }

            }
        };
        // 1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            // 2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            // 3.获取对象调用方法
            method.invoke(mPm,"com.android.browser",mStatsObserver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
