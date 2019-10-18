package org.leaffun.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.leaffun.view.slide.SlideView;

import java.util.ArrayList;
import java.util.List;

import static org.leaffun.alpha.BookUtil.getItems;
import static org.leaffun.alpha.BookUtil.toast;

/**
 * 记事本-分类下的-记录
 */
public class BookItemsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private String category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        category = getIntent().getStringExtra("category");

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        List<String> items = getItems(this,category);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){

            actionBar.setTitle(category+"("+ (items != null ? items.size() : 0) +")");
        }

        if(items!=null) {
            List<SlideView> container = new ArrayList<>();
            rv.setAdapter(new RecyclerView.Adapter() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View inflate = LayoutInflater.from(BookItemsActivity.this).inflate(R.layout.recyc_items, viewGroup, false);
                    return new itemVH(inflate);
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    itemVH vh = (itemVH) viewHolder;
                    vh.tv.setText((i+1)+". "+items.get(i));
                    vh.slide.clearMenu();
                    vh.slide.addMenu("编辑", R.color.red,R.color.white,v->{


                    });
                    vh.slide.setOutterOpenMenuContainer(container);
                    vh.tv.setOnClickListener(v->{
                        startActivity(new Intent(BookItemsActivity.this,BookItemDetailActivity.class).putExtra("item",items.get(i)));
                    });
                }


                @Override
                public int getItemCount() {
                    return items.size();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_1:
                startActivity(new Intent(this,BookEditActivity.class).putExtra("category",category));
                break;
            default:
                toast(this, item.getTitle().toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    static class itemVH extends RecyclerView.ViewHolder{

        private TextView tv;
        private SlideView slide;

        public itemVH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.txt);
            tv.setTextSize(14);
            slide = itemView.findViewById(R.id.slide);
        }
    }

}
