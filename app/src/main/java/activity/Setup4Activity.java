package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.example.mobilesafe.R;
import com.google.android.material.chip.ChipGroup;

import util.SpUtils;
import util.ToastUtil;

public class Setup4Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();
    }

    private void initUI() {
        CheckBox cb_box=(CheckBox) findViewById(R.id.cb_box);
        Boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security){
            cb_box.setText("安全设置已开启");
        }else {
            cb_box.setText("安全设置已关闭");
        }
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SpUtils.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,b);
                if (b){
                    cb_box.setText("安全设置已开启");
                }else {
                    cb_box.setText("安全设置已关闭");
                }
            }
        });

    }

    public void nextPage(View view){
        if (SpUtils.getBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,false)){
            Intent intent = new Intent(this, SetupOverActivity.class);
            startActivity(intent);
            SpUtils.putBoolean(getApplicationContext(),ConstantValue.SETUP_OVER,true);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else {
            ToastUtil.show(getApplicationContext(),"请开启防盗");
        }

    }
    public void prePage(View view){
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}
