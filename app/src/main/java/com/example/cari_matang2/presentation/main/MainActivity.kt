package com.example.cari_matang2.presentation.main

import android.os.Bundle
import android.content.Intent
import android.location.GnssAntennaInfo.Listener
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.cari_matang2.R
import android.view.View
import com.bumptech.glide.Glide
import com.example.cari_matang2.adapter.MaterialsAdapter
import com.example.cari_matang2.databinding.ActivityMainBinding
import com.example.cari_matang2.model.Material
import com.example.cari_matang2.model.User
import com.example.cari_matang2.presentation.camera.CameraActivity
import com.example.cari_matang2.presentation.content.ContentActivity
import com.example.cari_matang2.presentation.user.UserActivity
import com.example.cari_matang2.repository.Repository
import com.example.cari_matang2.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var materialsAdapter: MaterialsAdapter
    private lateinit var userDatabase: DatabaseReference
    private lateinit var materialDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null
    private var listenerUser = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            val user = snapshot.getValue(User::class.java)
            user?.let {
                binding.apply {
                    tvNameUserMain.text = it.nameUser

                    Glide
                        .with(this@MainActivity)
                        .load(it.avatarUser)
                        .placeholder(android.R.color.darker_gray)
                        .into(ivAvatarMain)
                }
            }
        }


        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("MainActivity", "[onCancelled] - ${error.message}")
            showDialogError(this@MainActivity, error.message)
        }
    }

    private var listenerMaterials = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            if (snapshot.value != null) {
                showData()
                val json = Gson().toJson(snapshot.value)
                val type = object : TypeToken<MutableList<Material>>() {}.type
                val materials = Gson().fromJson<MutableList<Material>>(json, type)

                materials?.let { materialsAdapter.materials = it }
            } else {
                showEmptyData()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("MainActivity", "[onCancelled] - ${error.message}")
            showDialogError(this@MainActivity, error.message)
        }

    }

    private fun showEmptyData() {
        binding.apply {
            ivEmptyData.visible()
            etSearchMain.disabled()
            rvMaterialsMain.gone()
        }
    }

    private fun showData() {
        binding.apply {
            ivEmptyData.gone()
            etSearchMain.enabled()
            rvMaterialsMain.visible()
        }
    }


    companion object {
        const val EXTRA_POSITION = "extra_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        materialsAdapter = MaterialsAdapter()
        userDatabase = FirebaseDatabase.getInstance().getReference("users")
        materialDatabase = FirebaseDatabase.getInstance().getReference("materials")
        currentUser = FirebaseAuth.getInstance().currentUser


        onAction()
        getDataFirebase()
        initBottomNav()
    }

    private fun getDataFirebase() {
        showLoading()
        userDatabase
            .child(currentUser?.uid.toString())
            .addValueEventListener(listenerUser)

        materialDatabase
            .addValueEventListener(listenerMaterials)

        binding.rvMaterialsMain.adapter = materialsAdapter
    }


    private fun showLoading() {
        binding.swipeMain.isRefreshing = true
    }

    private fun hideLoading() {
        binding.swipeMain.isRefreshing = false
    }

    private fun onAction() {
        binding.apply {
            ivAvatarMain.setOnClickListener {
                startActivity<UserActivity>()
            }

            etSearchMain.addTextChangedListener {
                materialsAdapter.filter.filter(it.toString())
            }

            etSearchMain.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val dataSearch = etSearchMain.text.toString().trim()
                    materialsAdapter.filter.filter(dataSearch)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
            swipeMain.setOnRefreshListener {
                getDataFirebase()
            }
        }

        materialsAdapter.onClick { material, position ->
            startActivity<ContentActivity>(
                ContentActivity.EXTRA_MATERIAL to material,
                ContentActivity.EXTRA_POSITION to position
            )
        }
    }

    private fun initBottomNav() {
        binding.btmNavMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    navigateToMainActivity()
                    return@setOnItemSelectedListener true
                }

                R.id.action_camera -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.action_user -> {
                    openUserActivity()
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun openUserActivity() {
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
    }
}