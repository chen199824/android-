package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mobilesafe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import util.StreamUtil;
import util.ToastUtil;

public class SplashActivity extends Activity {
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERROR = 102;
    private static final int JSON_ERROR = 104;
    private static final int IO_ERROR = 103;
    private TextView tv_version_name;
    private RelativeLayout rl_root;
    private int mLocalVersionCode;
    private static final String tag = "SplashActivity";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json异常");
                    enterHome();
                    break;
            }
        }
    };

    //进入程序主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        initData();
        initAnimation();
    }

    //添加淡入的动画
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);
    }

    private void initData() {
        tv_version_name.setText("版本名称" + getVersionName());
        mLocalVersionCode = getVersionCode();
        //获取服务端版本号
        checkVersion();
    }

    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();

                try {
                    URL url = new URL("http://10.0.2.2:8080/update74.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);
                    //获取响应码
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        //将流转换为字符串
                        String json = StreamUtil.streamToString(is);
                        Log.i(tag, json);
                        //jaon解析
                        JSONObject jsonObject = new JSONObject(json);
                        String verSionName = jsonObject.getString("verSionName");
                        String verSionDes = jsonObject.getString("verSionDes");
                        String verSionCode = jsonObject.getString("verSionCode");
                        String downloadUrl = jsonObject.getString("downloadUrl");
                        //比对版本号
                        if (mLocalVersionCode < Integer.parseInt(verSionCode)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                   long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            };
        }.start();
    }

    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout)findViewById(R.id.rl_root);
    }
}
