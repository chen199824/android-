package com.example.manage;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.manage.R;

public class BaseCacheCleanActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache_clean);

        // 1.生成选项卡1
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        // 2.生成选项卡2
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("sd_clear_cache").setIndicator("sd卡清理");

        // 3.告知点中选项卡的后续操作
        tab1.setContent(new Intent(this,CacheCleanActivity.class));
        tab2.setContent(new Intent(this,SDCacheCleanActivity.class)); // 测试跳转

        // 4.将此两个选项卡维护到host（选项卡宿主）中
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }
}
