package com.example.footballapps.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter



class MatchViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val mFragmentList : List<Fragment> = ArrayList()
    private val mFragmentTitleList : List<String> = ArrayList()

    override fun getItem(position: Int): Fragment = mFragmentList[position]

    override fun getCount(): Int = mFragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = mFragmentTitleList[position]

}