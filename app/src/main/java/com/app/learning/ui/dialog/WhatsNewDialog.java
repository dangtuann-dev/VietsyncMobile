package com.app.learning.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class WhatsNewDialog extends Dialog {

    public WhatsNewDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_whats_new);

        MaterialButton btnClose = findViewById(R.id.btnCloseWhatsNew);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }
    }
}
