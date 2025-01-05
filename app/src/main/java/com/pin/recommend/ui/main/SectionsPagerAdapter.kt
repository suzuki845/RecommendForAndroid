package com.pin.recommend.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager?) :
    FragmentPagerAdapter(
        fm!!
    ) {
    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        when (position) {
            0 -> return CharacterDetailsFragment.newInstance(position)
            1 -> return StoryListFragment.newInstance(position)
            2 -> return SpecialContentsFragment.newInstance(position)
            3 -> return PaymentDetailsFragment.newInstance(position)
            4 -> return EventDetailsFragment.newInstance(position)
        }
        throw Exception("Missing Fragment")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 5
    }

    companion object {
        private val TAB_TITLES = arrayOf(
            "ホーム",
            "ストーリー",
            "スペシャル",
            "Pay&貯金",
            "カレンダー"
        )
    }
}