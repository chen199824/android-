package com.cyz.mobilesafe_master.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.cyz.mobilesafe_master.R;
import com.cyz.mobilesafe_master.utils.SpUtil;

public class ProcessSettingActivity extends Activity {
    CheckBox cb_show_system;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initSystemShow();
    }

    private void initSystemShow() {
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);

        boolean showSystem = SpUtil.getBoolean(this, "show_system", false);
        cb_show_system.setChecked(showSystem);
        if (showSystem){
            cb_show_system.setText("显示系统进程");
        }else {
            cb_show_system.setText("隐藏系统进程");
        }
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    cb_show_system.setText("显示系统进程");
                }else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(ProcessSettingActivity.this, "show_system", isChecked);
            }
        });
    }
}
