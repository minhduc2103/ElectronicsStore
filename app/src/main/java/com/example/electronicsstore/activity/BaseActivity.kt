package com.example.electronicsstore.activity

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.electronicsstore.R

abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: MaterialDialog? = null
    private var alertDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createProgressDialog()
        createAlertDialog()
    }

    private fun createProgressDialog() {
        progressDialog = MaterialDialog(this).apply {
            cancelable(false)
            lifecycleOwner(this@BaseActivity)
            title(text = getString(R.string.app_name))
            message(text = getString(R.string.msg_waiting_message))
        }
    }

    fun showProgressDialog(show: Boolean) {
        progressDialog?.let {
            if (show && !it.isShowing) {
                it.show()
            } else if (!show && it.isShowing) {
                it.dismiss()
            }
        }
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        alertDialog?.dismiss()
    }

    fun setCancelProgress(isCancelable: Boolean) {
        progressDialog?.setCancelable(isCancelable)
    }

    private fun createAlertDialog() {
        alertDialog = MaterialDialog(this).apply {
            lifecycleOwner(this@BaseActivity)
            cancelable(false)
            title(text = getString(R.string.app_name))
            positiveButton(R.string.action_ok)
        }
    }

    fun showAlertDialog(errorMessage: String?) {
        alertDialog?.show {
            message(text = errorMessage ?: "")
        }
    }

    fun showAlertDialog(@StringRes resourceId: Int) {
        alertDialog?.show {
            message(res = resourceId)
        }
    }

    fun showToastMessage(message: String?) {
        Toast.makeText(this, message ?: "", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        progressDialog?.dismiss()
        alertDialog?.dismiss()
        super.onDestroy()
    }
}
