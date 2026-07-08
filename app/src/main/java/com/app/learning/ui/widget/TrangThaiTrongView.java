package com.app.learning.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class TrangThaiTrongView extends LinearLayout {

    private ImageView ivIllustration;
    private TextView tvMessage;
    private MaterialButton btnRetry;

    public TrangThaiTrongView(Context context) {
        super(context);
        init(context, null);
    }

    public TrangThaiTrongView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TrangThaiTrongView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(VERTICAL);
        setGravity(android.view.Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.view_empty_state, this, true);

        ivIllustration = findViewById(R.id.iv_illustration);
        tvMessage = findViewById(R.id.tv_message);
        btnRetry = findViewById(R.id.btn_retry);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TrangThaiTrongView);
            String message = a.getString(R.styleable.TrangThaiTrongView_emptyMessage);
            int imageRes = a.getResourceId(R.styleable.TrangThaiTrongView_emptyImage, R.drawable.ic_logo_placeholder);
            boolean showButton = a.getBoolean(R.styleable.TrangThaiTrongView_showRetryButton, false);
            String buttonText = a.getString(R.styleable.TrangThaiTrongView_retryButtonText);

            if (message != null) {
                tvMessage.setText(message);
            }
            ivIllustration.setImageResource(imageRes);
            btnRetry.setVisibility(showButton ? VISIBLE : GONE);
            if (buttonText != null) {
                btnRetry.setText(buttonText);
            }
            a.recycle();
        }
    }

    public void datThongBao(String message) {
        tvMessage.setText(message);
    }

    public void datAnhMinhHoa(int resId) {
        ivIllustration.setImageResource(resId);
    }

    public void datSuKienClickThuLai(OnClickListener listener) {
        btnRetry.setVisibility(VISIBLE);
        btnRetry.setOnClickListener(listener);
    }

    public void hienThiNutThuLai(boolean show) {
        btnRetry.setVisibility(show ? VISIBLE : GONE);
    }
}
