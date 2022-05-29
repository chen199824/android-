package activity;

import static android.view.View.inflate;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Md5Util;
import util.SpUtils;
import util.ToastUtil;

public class ContactListActivity extends Activity {
    ListView lv_contact;
    Button adduser, delectuser;
    String UserName[] = new String[]{"大头儿子", "小头爸爸", "围裙妈妈"
    };
    String tUserPhonNumble[] = new String[]{"198123456", "1328888854", "139556472"
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    public void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = inflate(this, R.layout.add_user, null);
        //兼容低版本的系统 去除内边距
        dialog.setView(view,0,0,0,0);
        dialog.show();

    }
    private void initData() {

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, getData(), R.layout.user_item,
                new String[]{"UserName", "tUserPhonNumble"}, new int[]{R.id.tUserName, R.id.tUserPhonNumble});
        lv_contact.setAdapter(simpleAdapter);
        //添加联系人
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });
    }

    private void initUI() {
        lv_contact = findViewById(R.id.lv_contact);
        adduser = (Button) findViewById(R.id.adduser);
        delectuser = (Button) findViewById(R.id.delectuser);

    }

    private List<? extends Map<String, ?>> getData() {
        List<Map<String, Object>> list;
        Map<String, Object> map;
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < UserName.length; i++) {
            map = new HashMap<String, Object>();
            map.put("UserName", UserName[i]);
            map.put("tUserPhonNumble", tUserPhonNumble[i]);
            list.add(map);
        }
        return list;
    }

}
