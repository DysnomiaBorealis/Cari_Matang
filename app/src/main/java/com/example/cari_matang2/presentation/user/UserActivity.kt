package com.example.cari_matang2.presentation.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_LOCALE_SETTINGS
import com.bumptech.glide.Glide
import com.example.cari_matang2.adapter.MaterialsAdapter
import com.example.cari_matang2.databinding.ActivityUserBinding
import com.example.cari_matang2.model.User
import com.example.cari_matang2.presentation.changepassword.ChangePasswordActivity
import com.example.cari_matang2.presentation.login.LoginActivity
import com.example.cari_matang2.utils.showDialogError
import com.example.cari_matang2.utils.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserActivity : AppCompatActivity() {
    private lateinit var userBinding : ActivityUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private var listenerUser = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            val user = snapshot.getValue(User::class.java)
            user?.let {
                userBinding.tvNameUser.text = it.nameUser
                userBinding.tvEmailUser.text = it.emailUser

                Glide
                    .with(this@UserActivity)
                    .load(it.avatarUser)
                    .placeholder(android.R.color.darker_gray)
                    .into(userBinding.ivAvatarUser)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            if (!isFinishing) {
                showDialogError(this@UserActivity, error.message)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        userBinding = ActivityUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(userBinding.root)

        //Init
        firebaseAuth = FirebaseAuth.getInstance()
        userDatabase = FirebaseDatabase.getInstance().getReference("users")
        currentUser = firebaseAuth.currentUser

        getDataFirebase()

        onAction()
    }

    private fun onAction() {
        userBinding.apply {
            btnCloseUser.setOnClickListener { finish() }
            btnChangePasswordUser.setOnClickListener {
                startActivity<ChangePasswordActivity>()
            }
            btnChangeLanguageUser.setOnClickListener {
                startActivity(Intent(ACTION_LOCALE_SETTINGS))
            }
            btnLogoutUser.setOnClickListener {
                currentUser?.uid?.let { userDatabase.child(it).removeEventListener(listenerUser) }
                firebaseAuth.signOut()
                startActivity<LoginActivity>()
                finishAffinity()
            }

            swipeUser.setOnRefreshListener {
                getDataFirebase()
            }
        }
    }

    private fun getDataFirebase() {
        showLoading()
        userDatabase
            .child(currentUser?.uid.toString())
            .addValueEventListener(listenerUser)
    }

    private fun showLoading(){
        userBinding.swipeUser.isRefreshing = true
    }

    private fun hideLoading(){
        userBinding.swipeUser.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        currentUser?.uid?.let { userDatabase.child(it).removeEventListener(listenerUser) }
    }


}