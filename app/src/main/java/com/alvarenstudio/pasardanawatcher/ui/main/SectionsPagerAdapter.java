package com.alvarenstudio.pasardanawatcher.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.alvarenstudio.pasardanawatcher.R;
import com.alvarenstudio.pasardanawatcher.ui.reksadana.ReksadanaFragment;
import com.alvarenstudio.pasardanawatcher.ui.saham.SahamFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0){
            return SahamFragment.newInstance("", "");
        }
        else{
            return ReksadanaFragment.newInstance("", "");
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}