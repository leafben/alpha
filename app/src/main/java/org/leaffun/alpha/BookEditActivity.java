package org.leaffun.alpha;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import static org.leaffun.alpha.BookUtil.addCategory;
import static org.leaffun.alpha.BookUtil.addItem;

/**
 * 记事本-添加分类
 */
public class BookEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);
        View root = findViewById(R.id.root);
        EditText edit = findViewById(R.id.edit);

        String extra = getIntent().getStringExtra("category");
        if (!TextUtils.isEmpty(extra)) {
            findViewById(R.id.add_item).setVisibility(View.VISIBLE);
            findViewById(R.id.add_category).setVisibility(View.GONE);
            findViewById(R.id.add_item).setOnClickListener(v -> {
                String str = edit.getText().toString();
                if (addItem(this, extra, str)) {
                    Snackbar.make(root, "添加["+ extra +"]记录成功", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(root, "添加失败", Snackbar.LENGTH_SHORT).show();
                }
            });


        }else{
            findViewById(R.id.add_item).setVisibility(View.GONE);
            findViewById(R.id.add_category).setVisibility(View.VISIBLE);
            findViewById(R.id.add_category).setOnClickListener(v -> {

                String str = edit.getText().toString();

                if (addCategory(this, str)) {
                    Snackbar.make(root, "添加分类成功", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(root, "添加失败", Snackbar.LENGTH_SHORT).show();
                }

            });
        }
    }

}
