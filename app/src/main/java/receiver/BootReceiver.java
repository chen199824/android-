package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import activity.ConstantValue;
import util.SpUtils;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
     //获取重启后手机的sim序列号
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        //获取 sim 卡序列号
        String simSerialNumber = "1467823";
        String sim_number= SpUtils.getString(context, ConstantValue.SIM_NUMBER,"");
        if (simSerialNumber!=sim_number){
            //发送短信给联系人号码
            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage("5556",null,"sim change",null,null);
        }
    }
}
