package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mobilesafe.R;

import util.SpUtils;

public class SetupOverActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpUtils.getBoolean(this,ConstantValue.SETUP_OVER,false)){
            //设置完成
            setContentView(R.layout.activity_setup_over);
        }else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //开启新的界面以后，关闭功能列表界面
            finish();
            //没有完成
        }
        initUI();
    }

    private void initUI() {
        TextView tv_phone = (TextView) findViewById(R.id.tv_phone);
        TextView tv_reset_setup=(TextView) findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
