package com.example.cari_matang2.presentation.forgotpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.example.cari_matang2.R
import com.example.cari_matang2.databinding.ActivityForgotPasswordBinding
import com.example.cari_matang2.utils.hideSoftKeyboard
import com.example.cari_matang2.utils.showDialogError
import com.example.cari_matang2.utils.showDialogLoading
import com.example.cari_matang2.utils.showDialogSuccess

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialogLoading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        forgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(forgotPasswordBinding.root)

        //Init
        firebaseAuth = FirebaseAuth.getInstance()
        dialogLoading = showDialogLoading(this)

        onAction()
    }

    private fun onAction() {
        forgotPasswordBinding.apply {
            btnCloseForgotPassword.setOnClickListener { finish() }

            btnForgotPassword.setOnClickListener {
                val email = etEmailForgotPassword.text.toString().trim()

                if (checkValidation(email)){
                    hideSoftKeyboard(this@ForgotPasswordActivity, forgotPasswordBinding.root)
                    forgotPasswordToServer(email)
                }
            }
        }
    }

    private fun forgotPasswordToServer(email: String) {
        dialogLoading.show()
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                dialogLoading.dismiss()
                val dialogSuccess = showDialogSuccess(this, getString(R.string.success_forgot_pass))
                dialogSuccess.show()

                Handler(Looper.getMainLooper())
                    .postDelayed({
                        dialogSuccess.dismiss()
                        finish()
                    }, 1500)
            }
            .addOnFailureListener {
                dialogLoading.dismiss()
                showDialogError(this, it.message.toString())
            }
    }

    private fun checkValidation(email: String): Boolean {
        forgotPasswordBinding.apply {
            when{
                email.isEmpty() -> {
                    etEmailForgotPassword.error = getString(R.string.please_field_your_email)
                    etEmailForgotPassword.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmailForgotPassword.error = getString(R.string.please_use_valid_email)
                    etEmailForgotPassword.requestFocus()
                }
                else -> return true
            }
        }
        return false
    }
}