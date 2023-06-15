package com.example.cari_matang2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.cari_matang2.databinding.ItemPageBinding
import com.example.cari_matang2.model.PagesItem
import com.example.cari_matang2.model.PartsPageItem

class PagesAdapter(private val context: Context): PagerAdapter() {

    var pages = mutableListOf<PagesItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = pages.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pageBinding = ItemPageBinding.inflate(LayoutInflater.from(context), container, false)

        bindItem(pageBinding, pages[position])
        container.addView(pageBinding.root)
        return pageBinding.root
    }

    private fun bindItem(pageBinding: ItemPageBinding, page: PagesItem) {
        val partsPageAdapter = PartsPageAdapter()

        partsPageAdapter.partsPage = page.partsPage as MutableList<PartsPageItem>

        pageBinding.rvPages.setHasFixedSize(true)
        pageBinding.rvPages.adapter = partsPageAdapter
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
        container.removeView(`object` as View)
}
