package org.leaffun.view.slide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 底层为侧滑按钮列表,LinearLayout
 * 第二层为原itemView
 */
public class SlideView extends FrameLayout
{
    private String TAG = "SlideView";

    private View mItemView;
    private LinearLayout menuLay;
    /**
     * 容器
     */
    private List<SlideView> mOuterMenuContainer = new ArrayList<>();
    /**
     * 同一个容器内多开
     */
    private boolean mMultiOpen = false;
    /**
     * down在可触发滑动的区域
     */
    private boolean mDownEventInSlideRange;
    /**
     * 是否处于测滑状态
     */
    private boolean mInSliding = false;
    /**
     * 侧滑触发距离
     */
    private float mSlidingDis = 50f;

    public SlideView(@NonNull Context paramContext)
    {
        super(paramContext);
        init();
    }
    
    public SlideView(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        init();
    }
    
    public SlideView(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private float mFirstX;
    private float mFirstY;
    private float mHistoryX = 0.0F;
    private float mHistoryY = 0.0F;
    private boolean moveToRightX;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
        if(menuLay.getChildCount()<=0) {
            return super.onInterceptTouchEvent(paramMotionEvent);
        }

        //侧滑时：上级视图不要拦截事件
        getParent().requestDisallowInterceptTouchEvent(this.mInSliding);

        switch (paramMotionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.mHistoryX = paramMotionEvent.getX();
                this.mHistoryY = paramMotionEvent.getY();
                this.mFirstX = paramMotionEvent.getX();
                this.mFirstY = paramMotionEvent.getY();

                if (this.mItemView == null) {
                    this.mItemView = getChildAt(1);
                }
                this.mDownEventInSlideRange = isTouchPointInView(this.mItemView, (int) paramMotionEvent.getRawX(), (int) paramMotionEvent.getRawY());
                Log.e(TAG,"Down判断：是否在滑动区域"+mDownEventInSlideRange);
                return super.onInterceptTouchEvent(paramMotionEvent);
            case MotionEvent.ACTION_MOVE:
                if (!this.mDownEventInSlideRange) {
                    Log.e(TAG,"Move判断：Down不在滑动区域");
                    return super.onInterceptTouchEvent(paramMotionEvent);
                }else{
                    Log.e(TAG,"Move判断：Down在滑动区域");
                }

                float nowX = paramMotionEvent.getX();
                float nowY = paramMotionEvent.getY();
                float absY = Math.abs(nowY - this.mHistoryY);
                float firstMoveX = Math.abs(nowX - this.mFirstX);
                float absX = Math.abs(nowX - this.mHistoryX);

                if (!mInSliding && (firstMoveX < mSlidingDis) && (Math.abs(nowY - this.mFirstY) < mSlidingDis)) {
                    Log.e(TAG,"Move判断：处于点击区域");
                    return super.onInterceptTouchEvent(paramMotionEvent);
                }

                if (!mInSliding && !((absY < absX) && firstMoveX > mSlidingDis)) {
                    Log.e(TAG,"Move判断：未满足是横向滑动且大于触发距离");
                    return super.onInterceptTouchEvent(paramMotionEvent);
                }

                float disX;
                this.moveToRightX = nowX > this.mHistoryX;
                if (this.moveToRightX) {
                    disX = this.mItemView.getX() + absX;
                } else {
                    disX = this.mItemView.getX() - absX;
                    if ((!this.mMultiOpen) && (this.mOuterMenuContainer != null)) {
                        closeOtherMenus();
                    }
                }
                if(!mInSliding){
                    disX += 50;
                }
                if (disX > 0.0F) {
                    disX = 0.0F;
                }
                float menuWidth = getMenuWidth();
                Log.e(TAG,"Move判断：menuWidth "+menuWidth);
                if (disX < -menuWidth) {
                    disX = -menuWidth;
                }
                Log.e(TAG,"Move判断：计算滑开的距离X "+disX);
                this.mItemView.setX(disX);
//                ViewGroup.LayoutParams params = menuLay.getLayoutParams();
//                params.width = (int) Math.abs(disX);
//                menuLay.setLayoutParams(params);
                Log.e(TAG,"Move判断：menuWidth "+menuWidth);
                this.mInSliding = true;
                this.mHistoryX = nowX;
                this.mHistoryY = nowY;

                return super.onInterceptTouchEvent(paramMotionEvent);
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG,"ACTION_CANCEL事件");
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"ACTION_UP事件");
                if (!mInSliding){
                    Log.e(TAG,"未滑动");
                    return super.onInterceptTouchEvent(paramMotionEvent);
                }

                if (this.moveToRightX) {
                    Log.e(TAG,"自动关闭");
                    closeView();
                } else {
                    Log.e(TAG,"自动打开");
                    openView();
                }

                return true;
            default:
                Log.e(TAG,"其他事件");
                return super.onInterceptTouchEvent(paramMotionEvent);
        }


    }
    float width = 0f;
    private float getMenuWidth()
    {
        if(width!=0){
            return width;
        }
        if (this.menuLay != null)
        {
            if (this.menuLay.getChildCount() > 0)
            {
                int i = 0;
                while (i < this.menuLay.getChildCount())
                {
                    width += this.menuLay.getChildAt(i).getMeasuredWidth();
                    i += 1;
                }
            }
        }
        return width;
    }
    
    private void init()
    {
        this.menuLay = new LinearLayout(getContext());
        LayoutParams localLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        localLayoutParams.gravity = Gravity.END;
        this.menuLay.setLayoutParams(localLayoutParams);
        this.menuLay.setGravity(Gravity.END);
        this.menuLay.setBackgroundColor(Color.WHITE);
        this.menuLay.setOnClickListener(new OnClickListener()
        {
            public void onClick(View paramAnonymousView) {

            }
        });
        addView(this.menuLay);
    }

    private boolean isTouchPointInView(View paramView, int x, int y)
    {
        int[] arrayOfInt = new int[2];
        paramView.getLocationOnScreen(arrayOfInt);

        int  i = arrayOfInt[0];
        int  j = arrayOfInt[1];
        int  k = paramView.getMeasuredWidth();
        int  m = paramView.getMeasuredHeight();
        return (y >= j) && (y <= j + m) && (x >= i) && (x <= i + k);
    }

    private void openView()
    {
        if (this.mItemView == null) {
            return;
        }

        if (this.mOuterMenuContainer != null)
        {
            Object localObject = ObjectAnimator.ofFloat(this.mItemView, "translationX", this.mItemView.getX(), -getMenuWidth());
            ((ObjectAnimator)localObject).setDuration(100L);
            ((ObjectAnimator)localObject).start();
            this.mInSliding = true;

            if (this.mMultiOpen) {
                if (!this.mOuterMenuContainer.contains(this)) {
                    this.mOuterMenuContainer.add(this);
                }
            }else{
                localObject = this.mOuterMenuContainer.iterator();
                while (((Iterator)localObject).hasNext())
                {
                    SlideView localSlideView2 = (SlideView)((Iterator)localObject).next();
                    if (localSlideView2 != this) {
                        localSlideView2.closeView();
                    }
                }
                this.mOuterMenuContainer.clear();
                this.mOuterMenuContainer.add(this);
            }

        }
    }

    public void addMenu(String paramString, int backgroundColor, int textColor, final OnClickListener paramOnClickListener)
    {
        final TextView localTextView = new TextView(getContext());
        LayoutParams localLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        localTextView.setPadding(dp2px(15), 0, dp2px(15), 0);
        localTextView.setGravity(Gravity.CENTER);
        localTextView.setLayoutParams(localLayoutParams);
        localTextView.setText(paramString);
        localTextView.setTextSize(14);
        localTextView.setBackgroundColor(getResources().getColor(backgroundColor));
        localTextView.setTextColor(getResources().getColor(textColor));
        localTextView.setOnClickListener(new OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                if (SlideView.this.mItemView.getX() != 0.0F)
                {

                    SlideView.this.closeView();
                    if (paramOnClickListener != null) {
                        paramOnClickListener.onClick(localTextView);
                    }
                }
            }
        });
        this.menuLay.addView(localTextView);
    }

    public void addMenuView(final View paramView, @NonNull final OnClickListener paramOnClickListener)
    {
        ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
        localLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        localLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        paramView.setLayoutParams(localLayoutParams);
        paramView.setOnClickListener(new OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                if (SlideView.this.mItemView.getX() != 0.0F)
                {
                    
                    SlideView.this.closeView();
                    if (paramOnClickListener != null) {
                        paramOnClickListener.onClick(paramView);
                    }
                }
            }
        });
        this.menuLay.addView(paramView);
    }
    
    public void closeAllMenu()
    {
        if (this.mOuterMenuContainer == null) {
            return;
        }
        Iterator localIterator = this.mOuterMenuContainer.iterator();
        while (localIterator.hasNext())
        {
            SlideView localSlideView2 = (SlideView)localIterator.next();
            localSlideView2.closeView();
        }
        this.mOuterMenuContainer.clear();
    }
    
    public void closeOtherMenus()
    {
        if (this.mOuterMenuContainer == null) {
            return;
        }
        Iterator localIterator = this.mOuterMenuContainer.iterator();
        while (localIterator.hasNext())
        {
            SlideView localSlideView2 = (SlideView)localIterator.next();
            if (localSlideView2 != this) {
                localSlideView2.closeView();
            }
        }
        this.mOuterMenuContainer.clear();
        this.mOuterMenuContainer.add(this);
    }
    
    public void closeView()
    {
        if ((this.mItemView == null) || (this.mItemView.getX() == 0.0F)) {
            mInSliding = false;
            return;
        }
       
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.mItemView, "translationX", this.mItemView.getX(), 0.0F);
        localObjectAnimator.setDuration(100L);
        localObjectAnimator.start();
        localObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInSliding = false;
            }
        });

        
        if (mOuterMenuContainer != null && mOuterMenuContainer.contains(this)){
            this.mOuterMenuContainer.remove(this);
        }
    }


    public void setMultiOpen(boolean paramBoolean)
    {
        this.mMultiOpen = paramBoolean;
    }
    
    public void setMenuLayBackgroundColor(int paramInt)
    {
        if (this.menuLay != null) {
            this.menuLay.setBackgroundColor(getResources().getColor(paramInt));
        }
    }
    
    public void setOuterMenuContainer(List<SlideView> paramList)
    {
        this.mOuterMenuContainer = paramList;
    }
    
    public void clearMenu()
    {
        if (this.menuLay != null && this.menuLay.getChildCount() > 0)
        {
            menuLay.removeAllViews();
            mOuterMenuContainer = null;
        }
    }

    public int dp2px(float dpValue)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

