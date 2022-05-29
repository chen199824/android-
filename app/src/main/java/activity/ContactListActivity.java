package activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.mobilesafe.R;

public class ContactListActivity extends Activity {
    ListView lv_contact;
    Button adduser,delectuser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
    }

    private void initUI() {
        lv_contact=findViewById(R.id.lv_contact);
         adduser =(Button)findViewById(R.id.adduser);
         delectuser=(Button)findViewById(R.id.delectuser);
    }

}
