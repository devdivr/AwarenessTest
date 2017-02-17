package com.devdivr.awarenesstest.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.devdivr.awarenesstest.R;
import com.devdivr.awarenesstest.view.fence.FenceFragment;
import com.devdivr.awarenesstest.view.snapshot.SnapshotFragment;

public class MainActivity extends AppCompatActivity {

    public static Intent buildIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private class TabAdapter extends FragmentStatePagerAdapter {

        private static final int SNAPSHOP_FRAGMENT = 0;
        private static final int FENCE_FRAGMENT = 1;

        private int[] titles = new int[] {
                R.string.title_snapshop,
                R.string.title_fence,
        };

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SNAPSHOP_FRAGMENT:
                    return SnapshotFragment.newInstance();

                case FENCE_FRAGMENT:
                    return FenceFragment.newInstance();
            }
            throw new NoClassDefFoundError("No Fragment!!!");
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(titles[position]);
        }
    }
}
