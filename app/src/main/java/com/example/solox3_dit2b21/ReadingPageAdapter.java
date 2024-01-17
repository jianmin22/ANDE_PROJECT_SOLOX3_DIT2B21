package com.example.solox3_dit2b21;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ReadingPageAdapter extends FragmentStateAdapter {
    private final List<String> pages;

    public ReadingPageAdapter(FragmentActivity fa, List<String> pages) {
        super(fa);
        this.pages = pages;
    }

    @Override
    public Fragment createFragment(int position) {
        return ReadingFragment.newInstance(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}
