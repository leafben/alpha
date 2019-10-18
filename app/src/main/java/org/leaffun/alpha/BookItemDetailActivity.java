package org.leaffun.alpha;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import static org.leaffun.alpha.BookUtil.addCategory;
import static org.leaffun.alpha.BookUtil.addItem;
import static org.leaffun.alpha.BookUtil.addItemDetail;

/**
 * 记事本-记录详情
 */
public class BookItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_item_detail);
        View root = findViewById(R.id.root);
        EditText edit = findViewById(R.id.edit);

        String extra = getIntent().getStringExtra("item");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(extra);
        }


        if (!TextUtils.isEmpty(extra)) {
            String itemDetail = BookUtil.getItemDetail(this, extra);
            edit.setText(itemDetail);
        }

        findViewById(R.id.save).setOnClickListener(v -> {
            String str = edit.getText().toString();
            if (addItemDetail(this, extra, str)) {
                Snackbar.make(root, "保存["+ extra +"]记录详情成功", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(root, "保存失败", Snackbar.LENGTH_SHORT).show();
            }
        });


    }

}
