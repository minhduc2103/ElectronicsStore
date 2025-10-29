package com.example.electronicsstore.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.electronicsstore.fragment.ProductFragment
import com.example.electronicsstore.model.Category

class CategoryPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val listCategory: List<Category>?
) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return ProductFragment.newInstance(listCategory!![position].id)
    }

    override fun getItemCount(): Int {
        if (listCategory != null) return listCategory.size
        return 0
    }
}
