package com.renbin.dynamicload;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.renbin.skin_library.SkinManager;

public class TwoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinManager.getInstance().loadSkinApk(Environment.getExternalStorageDirectory()+"/skin.apk");
                //换肤
                apply();
            }
        });
    }
}