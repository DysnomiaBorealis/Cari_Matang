package com.example.cari_matang2.presentation.content

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewpager.widget.ViewPager
import com.example.cari_matang2.adapter.PagesAdapter
import com.example.cari_matang2.databinding.ActivityContentBinding
import com.example.cari_matang2.model.Material
import androidx.activity.viewModels
import com.example.cari_matang2.model.Content
import com.example.cari_matang2.model.PagesItem
import com.example.cari_matang2.presentation.main.MainActivity
import com.example.cari_matang2.repository.Repository
import com.example.cari_matang2.utils.*
import com.google.firebase.database.*
import com.google.gson.Gson

@Suppress("DEPRECATION")
class ContentActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_MATERIAL = "extra_material"
        const val EXTRA_POSITION = "extra_position"
    }
    private lateinit var contentBinding: ActivityContentBinding
    private lateinit var pagesAdapter: PagesAdapter
    private lateinit var contentDatabase: DatabaseReference
    private var currentPosition = 0
    private var materialPosition = 0

    private val listenerContent = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            if (snapshot.value != null){
                showData()

                val json = Gson().toJson(snapshot.value)
                val content = Gson().fromJson(json, Content::class.java)

                pagesAdapter.pages = content?.pages as MutableList<PagesItem>
            }else{
                showEmptyData()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            showDialogError(this@ContentActivity, error.message)
        }

    }

    private fun showEmptyData() {
        contentBinding.apply {
            ivEmptyDataContent.visible()
            vpContent.gone()
        }
    }

    private fun showData() {
        contentBinding.apply {
            ivEmptyDataContent.gone()
            vpContent.visible()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentBinding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(contentBinding.root)

        //Init
        pagesAdapter = PagesAdapter(this)
        contentDatabase = FirebaseDatabase.getInstance().getReference("contents")

        onAction()
        getDataIntent()
        viewPagerCurrentPosition()

    }

    private fun viewPagerCurrentPosition() {
        contentBinding.vpContent.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val totalIndex = pagesAdapter.count
                currentPosition = position
                val textIndex = "${currentPosition + 1} / $totalIndex"
                contentBinding.tvIndexContent.text = textIndex

                if (currentPosition == 0){
                    contentBinding.btnPrevContent.invisible()
                    contentBinding.btnPrevContent.disabled()
                }else{
                    contentBinding.btnPrevContent.visible()
                    contentBinding.btnPrevContent.enabled()
                }
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }


    private fun getDataIntent() {
        if (intent != null){
            materialPosition = intent.getIntExtra(EXTRA_POSITION, 0)
            val material = intent.getParcelableExtra<Material>(EXTRA_MATERIAL)

            contentBinding.tvTitleContent.text = material?.titleMaterial

            material?.let { getDataContent(material) }
        }
    }

    private fun getDataContent(material: Material) {
        showLoading()
        contentDatabase
            .child(material.idMaterial.toString())
            .addValueEventListener(listenerContent)

        contentBinding.vpContent.adapter = pagesAdapter
        contentBinding.vpContent.setPagingEnabled(false)

        //Init untuk tampilan awal index
        val textIndex = "${currentPosition + 1} / ${pagesAdapter.count}"
        contentBinding.tvIndexContent.text = textIndex

    }

    private fun showLoading() {
        contentBinding.swipeContent.isRefreshing = true
    }

    private fun hideLoading() {
        contentBinding.swipeContent.isRefreshing = false
    }

    private fun onAction() {
        contentBinding.apply {
            btnCloseContent.setOnClickListener { finish() }

            btnNextContent.setOnClickListener {
                if (currentPosition < pagesAdapter.count - 1){
                    contentBinding.vpContent.currentItem += 1
                }else{
                    startActivity<MainActivity>(
                        MainActivity.EXTRA_POSITION to materialPosition + 1
                    )
                    finish()
                }
            }

            btnPrevContent.setOnClickListener {
                contentBinding.vpContent.currentItem -= 1
            }

            swipeContent.setOnRefreshListener {
                getDataIntent()
            }
        }
    }


}