package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;

public class SettingItemView extends RelativeLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    CheckBox cd_box;
    TextView tv_des;
    String mDestitle;
    String mDesoff;
    String mDeson;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_itrm_view, this);

        TextView tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_des = (TextView) this.findViewById(R.id.tv_des);
        cd_box = (CheckBox) this.findViewById(R.id.cd_box);
        //获取自定义属性和原始属性
        initAttrs(attrs);
        tv_title.setText(mDestitle);
        tv_des.setText(mDeson);
    }

    private void initAttrs(AttributeSet attrs) {
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    public boolean isCheck() {
        return cd_box.isChecked();
    }

    public void setCheck(boolean isCheck) {
        cd_box.setChecked(isCheck);
        if (isCheck) {
            tv_des.setText(mDeson);
        } else {
            tv_des.setText(mDesoff);
        }
    }
}
