package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class FocusTextView extends androidx.appcompat.widget.AppCompatTextView {

//通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }
//
    public FocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isFocused(){
        return true;
    }
}
