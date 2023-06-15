package com.example.cari_matang2.presentation.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import com.example.cari_matang2.R
import com.example.cari_matang2.databinding.ActivityChangePasswordBinding
import com.example.cari_matang2.utils.hideSoftKeyboard
import com.example.cari_matang2.utils.showDialogError
import com.example.cari_matang2.utils.showDialogLoading
import com.example.cari_matang2.utils.showDialogSuccess

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var changePasswordBinding: ActivityChangePasswordBinding
    private lateinit var dialogLoading: AlertDialog
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        changePasswordBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(changePasswordBinding.root)

        //Init
        currentUser = FirebaseAuth.getInstance().currentUser
        dialogLoading = showDialogLoading(this)

        onAction()
    }


    private fun onAction() {
        changePasswordBinding.apply {
            btnChangePassword.setOnClickListener {
                val oldPass = etOldPassword.text.toString().trim()
                val newPass = etNewPassword.text.toString().trim()
                val confirmNewPass = etConfirmNewPassword.text.toString().trim()

                if (checkValidation(oldPass, newPass, confirmNewPass)){
                    hideSoftKeyboard(this@ChangePasswordActivity, changePasswordBinding.root)
                    changePasswordToServer(oldPass, newPass)
                }
            }

            btnCloseChangePassword.setOnClickListener { finish() }
        }
    }

    private fun changePasswordToServer(oldPass: String, newPass: String) {
        dialogLoading.show()
        currentUser?.let { mCurrentUser ->
            val credential = EmailAuthProvider.getCredential(mCurrentUser.email.toString(), oldPass)

            mCurrentUser.reauthenticate(credential)
                .addOnSuccessListener {
                    mCurrentUser.updatePassword(newPass)
                        .addOnSuccessListener {
                            dialogLoading.dismiss()
                            val dialogSuccess = showDialogSuccess(this, getString(R.string.success_change_pass))
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
                .addOnFailureListener {
                    dialogLoading.dismiss()
                    showDialogError(this, it.message.toString())
                }
        }
    }
    private fun checkValidation(oldPass: String, newPass: String, confirmNewPass: String): Boolean {
        changePasswordBinding.apply {
            when{
                oldPass.isEmpty() -> {
                    etOldPassword.error = getString(R.string.please_field_your_old_password)
                    etOldPassword.requestFocus()
                }
                oldPass.length < 8 -> {
                    etOldPassword.error = getString(R.string.please_field_your_password_more_than_8)
                    etOldPassword.requestFocus()
                }
                newPass.isEmpty() -> {
                    etNewPassword.error = getString(R.string.please_field_your_new_password)
                    etNewPassword.requestFocus()
                }
                newPass.length < 8 -> {
                    etNewPassword.error = getString(R.string.please_field_your_password_more_than_8)
                    etNewPassword.requestFocus()
                }
                confirmNewPass.isEmpty() -> {
                    etConfirmNewPassword.error = getString(R.string.please_field_your_confirm_new_password)
                    etConfirmNewPassword.requestFocus()
                }
                confirmNewPass.length < 8 -> {
                    etConfirmNewPassword.error = getString(R.string.please_field_your_password_more_than_8)
                    etConfirmNewPassword.requestFocus()
                }
                newPass != confirmNewPass -> {
                    etNewPassword.error = getString(R.string.your_password_didnt_match)
                    etNewPassword.requestFocus()
                    etConfirmNewPassword.error = getString(R.string.your_password_didnt_match)
                    etConfirmNewPassword.requestFocus()
                }
                else -> return true
            }
        }
        return false
    }
}
