package org.leaffun.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * 所有功能列表
 */
public class FuncListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func_list);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("功能列表");
        }

        findViewById(R.id.book).setOnClickListener(v->{
            startActivity(new Intent(this, BookListActivity.class));
        });

        findViewById(R.id.nfc).setOnClickListener(v->{
            startActivity(new Intent(this, NFCActivity.class));
        });
    }
}
