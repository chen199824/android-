package util;

import android.content.Context;
import android.view.GestureDetector;
import android.widget.Toast;

public class ToastUtil {
    public static void show(Context ctx,String msg){
        Toast.makeText(ctx,msg, Toast.LENGTH_SHORT).show();
    }
}
