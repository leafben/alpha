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
    private float absX;
    private boolean clickDown;
    private boolean mCanOpenMoreInOneContainer = false;
    private float mFirstX;
    private float mFirstY;
    private float mHistoryX = 0.0F;
    private float mHistoryY = 0.0F;
    private View mItemView;
    private boolean mItemViewDown;
    private List<SlideView> mOutterOpenMenuPlaceHolder = new ArrayList();
    private boolean mResponseWhenMenuOpen = true;
    private boolean mSlideMode = false;
    private LinearLayout menuLay;
    private boolean right;
    private int spaceX = 0;

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

    private float getMenuWidth()
    {
        float width = 0f;
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
                width += this.spaceX * (this.menuLay.getChildCount() - 1);
            }
        }
        return width;
    }

    private void init()
    {
        this.menuLay = new LinearLayout(getContext());
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        localLayoutParams.gravity = Gravity.END;
        this.menuLay.setLayoutParams(localLayoutParams);
        this.menuLay.setGravity(Gravity.END);
        this.menuLay.setBackgroundColor(Color.WHITE);
        this.menuLay.setOnClickListener(new View.OnClickListener()
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

        if (this.mOutterOpenMenuPlaceHolder != null)
        {
            Object localObject = ObjectAnimator.ofFloat(this.mItemView, "translationX", this.mItemView.getX(), -getMenuWidth());
            ((ObjectAnimator)localObject).setDuration(100L);
            ((ObjectAnimator)localObject).start();
            this.mSlideMode = true;

            if (this.mCanOpenMoreInOneContainer) {
                if (!this.mOutterOpenMenuPlaceHolder.contains(this)) {
                    this.mOutterOpenMenuPlaceHolder.add(this);
                }
            }else{
                localObject = this.mOutterOpenMenuPlaceHolder.iterator();
                while (((Iterator)localObject).hasNext())
                {
                    SlideView localSlideView2 = (SlideView)((Iterator)localObject).next();
                    if (localSlideView2 != this) {
                        localSlideView2.closeView();
                    }
                }
                this.mOutterOpenMenuPlaceHolder.clear();
                this.mOutterOpenMenuPlaceHolder.add(this);
            }

        }
    }

    public void addMenu(String paramString, int backgroundColor, int textColor, final View.OnClickListener paramOnClickListener)
    {
        final TextView localTextView = new TextView(getContext());
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        localTextView.setPadding(dp2px(17), 0, dp2px(17), 0);
        localTextView.setGravity(Gravity.CENTER);
        localTextView.setLayoutParams(localLayoutParams);
        localTextView.setText(paramString);
        localTextView.setTextSize(14);
        localTextView.setBackgroundColor(getResources().getColor(backgroundColor));
        localTextView.setTextColor(getResources().getColor(textColor));
        localTextView.setOnClickListener(new View.OnClickListener()
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

    public void addMenuView(final View paramView, @NonNull final View.OnClickListener paramOnClickListener)
    {
        ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
        localLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        localLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        paramView.setLayoutParams(localLayoutParams);
        paramView.setOnClickListener(new View.OnClickListener()
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
        if (this.mOutterOpenMenuPlaceHolder == null) {
            return;
        }
        Iterator localIterator = this.mOutterOpenMenuPlaceHolder.iterator();
        while (localIterator.hasNext())
        {
            SlideView localSlideView2 = (SlideView)localIterator.next();
            localSlideView2.closeView();
        }
        this.mOutterOpenMenuPlaceHolder.clear();
    }

    public void closeOtherMenus()
    {
        if (this.mOutterOpenMenuPlaceHolder == null) {
            return;
        }
        Iterator localIterator = this.mOutterOpenMenuPlaceHolder.iterator();
        while (localIterator.hasNext())
        {
            SlideView localSlideView2 = (SlideView)localIterator.next();
            if (localSlideView2 != this) {
                localSlideView2.closeView();
            }
        }
        this.mOutterOpenMenuPlaceHolder.clear();
        this.mOutterOpenMenuPlaceHolder.add(this);
    }

    public void closeView()
    {
        if ((this.mItemView == null) || (this.mItemView.getX() == 0.0F)) {
            mSlideMode = false;
            return;
        }

        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.mItemView, "translationX", this.mItemView.getX(), 0.0F);
        localObjectAnimator.setDuration(100L);
        localObjectAnimator.start();
        localObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideMode = false;
            }
        });


        if (mOutterOpenMenuPlaceHolder != null && mOutterOpenMenuPlaceHolder.contains(this)){
            this.mOutterOpenMenuPlaceHolder.remove(this);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
        if(menuLay.getChildCount()>0) {
            getParent().requestDisallowInterceptTouchEvent(this.mSlideMode);
            switch (paramMotionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    this.mHistoryX = paramMotionEvent.getX();
                    this.mHistoryY = paramMotionEvent.getY();
                    this.mFirstX = paramMotionEvent.getX();
                    this.mFirstY = paramMotionEvent.getY();
                    this.clickDown = true;
                    if (this.mItemView == null) {
                        this.mItemView = getChildAt(1);
                    }
                    this.mItemViewDown = isTouchPointInView(this.mItemView, (int) paramMotionEvent.getRawX(), (int) paramMotionEvent.getRawY());
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (!this.mItemViewDown) {

                        return super.onInterceptTouchEvent(paramMotionEvent);
                    }

                    float nowX = paramMotionEvent.getX();
                    float nowY = paramMotionEvent.getY();
                    float absY = Math.abs(nowY - this.mHistoryY);
                    this.absX = Math.abs(nowX - this.mHistoryX);
                    float firstMoveX = Math.abs(nowX - this.mFirstX);
                    if ((firstMoveX > 10.0F) || (Math.abs(nowY - this.mFirstY) > 10.0F)) {
                        this.clickDown = false;//移动接管
                    }

                    this.right = nowX > this.mHistoryX;
                    this.mHistoryX = nowX;
                    this.mHistoryY = nowY;

                    if (this.mSlideMode || ((absY < this.absX) && firstMoveX > 50)) {
                        float disX;
                        if (this.right) {

                            disX = this.mItemView.getX() + absX;
                        } else {

                            disX = this.mItemView.getX() - absX;

                            if ((!this.mCanOpenMoreInOneContainer) && (this.mOutterOpenMenuPlaceHolder != null)) {
                                closeOtherMenus();
                            }
                        }

                        if (disX > 0.0F) {
                            disX = 0.0F;
                        }
                        if (disX < -getMenuWidth()) {
                            disX = -getMenuWidth();
                        }
                        this.mItemView.setX(disX);
                        this.mSlideMode = true;
                    } else {
//                        if(absY>absX){
//                            closeAllMenu();
//                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:

                case MotionEvent.ACTION_UP:

                    if ((this.mItemViewDown) && this.mSlideMode) {
                        if (this.right) {
                            if (this.mItemView.getX() != 0.0F) {

                                closeView();
                            } else {

                                closeView();
                            }
                        } else {

                            openView();
                        }

                        if (this.mSlideMode || !this.clickDown) {
                            //滑动时，区域过大时禁止点击
                            return true;
                        }


                        if (!this.mResponseWhenMenuOpen) {
                            return true;
                        }
                    } else {

                    }
                    break;
                default:

                    break;
            }
        }
        return super.onInterceptTouchEvent(paramMotionEvent);
    }

    public void setCanOpenMoreInOneContainer(boolean paramBoolean)
    {
        this.mCanOpenMoreInOneContainer = paramBoolean;
    }

    public void setMenuLayBackgroundColor(int paramInt)
    {
        if (this.menuLay != null) {
            this.menuLay.setBackgroundColor(getResources().getColor(paramInt));
        }
    }

    public void setOutterOpenMenuContainer(List<SlideView> paramList)
    {
        this.mOutterOpenMenuPlaceHolder = paramList;
    }

    public void setResponseClickWhenMenuIsClosing(boolean paramBoolean)
    {
        this.mResponseWhenMenuOpen = paramBoolean;
    }

    public void clearMenu()
    {
        if (this.menuLay != null && this.menuLay.getChildCount() > 0)
        {
            menuLay.removeAllViews();
        }
    }

    public int dp2px(float dpValue)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

