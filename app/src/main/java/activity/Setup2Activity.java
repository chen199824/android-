package activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.mobilesafe.R;

import util.SpUtils;
import view.SettingItemView;

public class Setup2Activity extends Activity {
    SettingItemView settingItemView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUI();
    }

    public void nextPage(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        if (settingItemView.isCheck()){
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }
    }

    public void prePage(View view) {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    public void initUI() {
        settingItemView = findViewById(R.id.siv_sim_bound);
        String sim_number = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            settingItemView.setCheck(false);
        } else {
            settingItemView.setCheck(true);
        }
        settingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheck = settingItemView.isCheck();
                settingItemView.setCheck(!isCheck);
                if (!isCheck) {
                    TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                    //获取 sim 卡序列号
                    String simSerialNumber = "1467823";
                    SpUtils.putString(getApplicationContext(),ConstantValue.SIM_NUMBER,simSerialNumber);
                }else {
                     SpUtils.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }
}
