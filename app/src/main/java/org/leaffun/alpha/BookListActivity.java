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

import static org.leaffun.alpha.BookUtil.getCategory;
import static org.leaffun.alpha.BookUtil.toast;

/**
 * 记事本-分类列表
 */
public class BookListActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);



        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();
        List<String> category = getCategory(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("分类列表("+ (category != null ? category.size() : 0) +")");
        }
        if(category!=null) {
            List<SlideView> container = new ArrayList<>();
            rv.setAdapter(new RecyclerView.Adapter() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View inflate = LayoutInflater.from(BookListActivity.this).inflate(R.layout.recyc_category, viewGroup, false);
                    return new CategoryVH(inflate);
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    CategoryVH vh = (CategoryVH) viewHolder;
                    vh.tv.setText(category.get(i));
                    vh.slide.clearMenu();
                    vh.slide.addMenu("删除", R.color.red,R.color.white,v->{
                        //todo 事务
                        BookUtil.removeItems(BookListActivity.this,category.get(i));
                        category.remove(i);
                        if(BookUtil.putCategory(BookListActivity.this,category)){
                            notifyDataSetChanged();
                        }
                    });
                    vh.slide.setOutterOpenMenuContainer(container);
                    vh.tv.setOnClickListener(v->{
                        startActivity(new Intent(BookListActivity.this, BookItemsActivity.class).putExtra("category", category.get(i)));
                    });
                }


                @Override
                public int getItemCount() {
                    return category.size();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_1:
                startActivity(new Intent(this,BookEditActivity.class));
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

    static class CategoryVH extends RecyclerView.ViewHolder{

        private TextView tv;
        private SlideView slide;

        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.txt);
            slide = itemView.findViewById(R.id.slide);
        }
    }

}
