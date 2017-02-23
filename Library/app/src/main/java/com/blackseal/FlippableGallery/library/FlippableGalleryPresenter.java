package com.blackseal.FlippableGallery.library;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by theresasun on 23/02/2017.
 */
public class FlippableGalleryPresenter {
    ViewPager pager;
    CircleIndicator pagerCircleIndicator;
    private int targetWidth;
    private FlipperAdapter flipperAdapter;

    public FlippableGalleryPresenter(ViewGroup container) {
        initViews(container);
    }

    private void initViews(ViewGroup container) {
        pager = (ViewPager) container.findViewById(R.id.pager_content);
        pagerCircleIndicator = (CircleIndicator) container.findViewById(R.id.pager_indicator);
        flipperAdapter = new FlipperAdapter();
        pager.setPageTransformer(true, new FlipperTransformation());
        pager.setAdapter(flipperAdapter);
        pager.setOffscreenPageLimit(3);
        int pagerWidth = (int) (container.getResources().getDisplayMetrics().widthPixels * 7.0f / 9.0f);
        ViewGroup.LayoutParams lp = pager.getLayoutParams();
        lp.width = pagerWidth;
        this.targetWidth = pagerWidth;
        pager.setLayoutParams(lp);
        pager.setPageMargin(40);
        container.setOnTouchListener((view, motionEvent) -> pager.dispatchTouchEvent(motionEvent));
    }

    public void setData(List<View> list) {
        flipperAdapter.setData(list);
    }

    public class FlipperAdapter extends PagerAdapter {
        List<View> viewList = new ArrayList<>();

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position), position);
            return viewList.get(position);
        }

        public void setData(List<View> list) {
            viewList.clear();
            for (View view : list) {
                viewList.add(view);
            }
            notifyDataSetChanged();
            pagerCircleIndicator.setViewPager(pager);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    public class FlipperTransformation implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            float width = page.getWidth();
            float rotate = 20 * Math.abs(position);
            if (position < 0) {
                page.setPivotX(width);
                page.setRotationY(rotate);
            } else if (position >= 0) {
                page.setPivotX(0);
                page.setRotationY(-rotate);
            }
        }
    }
}
