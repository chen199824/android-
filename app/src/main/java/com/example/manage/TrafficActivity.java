package com.example.manage;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

import static android.net.TrafficStats.getMobileRxBytes;

public class TrafficActivity extends Activity {

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //获取流量(手机下载流量)
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //上传下载总流量
        long mobileTxBytes=TrafficStats.getMobileTxBytes();
        //wife，流量 下载总和
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        //wife，流量 上传下载总流量
        long totalTxBytes = TrafficStats.getTotalTxBytes();

    }
}
