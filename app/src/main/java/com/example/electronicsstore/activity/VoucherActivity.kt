package com.example.electronicsstore.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicsstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.electronicsstore.MyApplication.Companion.get
import com.example.electronicsstore.adapter.VoucherAdapter
import com.example.electronicsstore.adapter.VoucherAdapter.IClickVoucherListener
import com.example.electronicsstore.event.VoucherSelectedEvent
import com.example.electronicsstore.model.Voucher
import com.example.electronicsstore.utils.Constant
import org.greenrobot.eventbus.EventBus

class VoucherActivity : BaseActivity() {
    private var listVoucher: MutableList<Voucher>? = null
    private var voucherAdapter: VoucherAdapter? = null
    private var amount = 0
    private var voucherSelectedId: Long = 0
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voucher)

        dataIntent
        initToolbar()
        initUi()
        listVoucherFromFirebase
    }

    private val dataIntent: Unit
        get() {
            val bundle = intent.extras ?: return
            voucherSelectedId = bundle.getLong(Constant.VOUCHER_ID, 0)
            amount = bundle.getInt(Constant.AMOUNT_VALUE, 0)
        }

    private fun initUi() {
        val rcvVoucher = findViewById<RecyclerView>(R.id.rcv_voucher)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvVoucher.layoutManager = linearLayoutManager
        listVoucher = ArrayList()
        voucherAdapter = VoucherAdapter(
            this,
            listVoucher,
            amount,
            object : IClickVoucherListener {
                override fun onClickVoucherItem(voucher: Voucher?) {
                    voucher?.let { handleClickVoucher(it) }
                }
            })
        rcvVoucher.adapter = voucherAdapter
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        tvToolbarTitle.text = getString(R.string.title_voucher)
    }

    @get:SuppressLint("NotifyDataSetChanged")
    private val listVoucherFromFirebase: Unit
        get() {
            showProgressDialog(true)
            mValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showProgressDialog(false)

                    resetListVoucher()
                    for (dataSnapshot in snapshot.children) {
                        val voucher = dataSnapshot.getValue(Voucher::class.java)
                        if (voucher != null) {
                            listVoucher!!.add(0, voucher)
                        }
                    }

                    if (voucherSelectedId > 0 && listVoucher != null && listVoucher!!.isNotEmpty()) {
                        for (voucher in listVoucher!!) {
                            if (voucher.id == voucherSelectedId) {
                                voucher.isSelected = true
                                break
                            }
                        }
                    }
                    if (voucherAdapter != null) voucherAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    showProgressDialog(false)
                    showToastMessage(getString(R.string.msg_get_date_error))
                }
            }
            get(this).voucherDatabaseReference
                .addValueEventListener(mValueEventListener as ValueEventListener)
        }

    private fun resetListVoucher() {
        if (listVoucher != null) {
            listVoucher!!.clear()
        } else {
            listVoucher = ArrayList()
        }
    }

    private fun handleClickVoucher(voucher: Voucher) {
        EventBus.getDefault().post(VoucherSelectedEvent(voucher))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        voucherAdapter?.release()
        if (mValueEventListener != null) {
            get(this).voucherDatabaseReference
                .removeEventListener(mValueEventListener!!)
        }
    }
}
