package com.example.manage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.manage.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoDao {

    /**
     * 获取安装在手机上应用相关信息集合的方法
     * @param context 上下文环境
     */
    public static List<AppInfo> getAppInfoList(Context context){
        // 0.声明集合
        List<AppInfo> appInfoList = new ArrayList<>();
        // 1.获取包管理对象
        PackageManager packageManager = context.getPackageManager();
        // 2.通过包管理对象来获取手机上所有应用信息，直接传递0即可
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        // 3.循环遍历上述集合,获取每一个安装在手机上的应用相关信息（包名，名称，路径，系统，图标）
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();
            // 4.包名
            appInfo.setPackagename(packageInfo.packageName);
            // 5.获取应用名称、图标，其信息存储在application节点中
            //ApplicationInfo applicationInfo = packageInfo.applicationInfo.applicationInfo;
            //appInfo.name = applicationInfo.loadLabel(packageManager).toString()+applicationInfo.uid;
           appInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString()+packageInfo.applicationInfo.uid);
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            // 6.判断应用是否为系统应用（状态机）
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                // 系统应用
                appInfo.setSystem(true);
            }else {
                // 非系统应用
                appInfo.setSystem(false);
            }
            // 7.判断应用是否为sd卡应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                // sd卡应用
                appInfo.setSdCard(true);
            }else {
                // 非sd卡应用
                appInfo.setSdCard(false);
            }
            // 8.加入集合中
            appInfoList.add(appInfo);
        }
        // 9.返回集合
        return appInfoList;
    }
}
