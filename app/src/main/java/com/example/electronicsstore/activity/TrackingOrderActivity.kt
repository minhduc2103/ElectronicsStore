package com.example.electronicsstore.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electronicsstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.example.electronicsstore.MyApplication.Companion.get
import com.example.electronicsstore.adapter.ProductOrderAdapter
import com.example.electronicsstore.model.Order
import com.example.electronicsstore.model.RatingReview
import com.example.electronicsstore.utils.ConfigEmail
import com.example.electronicsstore.utils.Constant
import com.example.electronicsstore.utils.GlobalFunction.startActivity
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage

import java.util.Properties

class TrackingOrderActivity : BaseActivity() {
    private var rcvProducts: RecyclerView? = null
    private var layoutReceiptOrder: LinearLayout? = null
    private var dividerStep1: View? = null
    private var dividerStep2: View? = null
    private var imgStep1: ImageView? = null
    private var imgStep2: ImageView? = null
    private var imgStep3: ImageView? = null
    private var tvTakeOrder: TextView? = null
    private var tvTakeOrderMessage: TextView? = null

    private var orderId: Long = 0
    private var mOrder: Order? = null
    private var isOrderArrived = false
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_order)

        dataIntent
        initToolbar()
        initUi()
        initListener()
        orderDetailFromFirebase
    }

    private val dataIntent: Unit
        get() {
            val bundle = intent.extras ?: return
            orderId = bundle.getLong(Constant.ORDER_ID)
        }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.label_tracking_order)
    }

    private fun initUi() {
        rcvProducts = findViewById(R.id.rcv_products)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvProducts?.setLayoutManager(linearLayoutManager)
        layoutReceiptOrder = findViewById(R.id.layout_receipt_order)
        dividerStep1 = findViewById(R.id.divider_step_1)
        dividerStep2 = findViewById(R.id.divider_step_2)
        imgStep1 = findViewById(R.id.img_step_1)
        imgStep2 = findViewById(R.id.img_step_2)
        imgStep3 = findViewById(R.id.img_step_3)
        tvTakeOrder = findViewById(R.id.tv_take_order)
        tvTakeOrderMessage = findViewById(R.id.tv_take_order_message)
        val layoutBottom = findViewById<LinearLayout>(R.id.layout_bottom)
        layoutBottom.visibility = View.VISIBLE
    }

    private fun initListener() {
        layoutReceiptOrder!!.setOnClickListener {
            if (mOrder == null) return@setOnClickListener
            val bundle = Bundle()
            bundle.putLong(Constant.ORDER_ID, mOrder!!.id)
            startActivity(
                this@TrackingOrderActivity,
                ReceiptOrderActivity::class.java, bundle
            )
            finish()
        }

        imgStep1!!.setOnClickListener { updateStatusOrder(Order.STATUS_NEW) }
        imgStep2!!.setOnClickListener { updateStatusOrder(Order.STATUS_DOING) }
        imgStep3!!.setOnClickListener { updateStatusOrder(Order.STATUS_ARRIVED) }
        tvTakeOrder!!.setOnClickListener {
            if (isOrderArrived) {
                updateStatusOrder(Order.STATUS_COMPLETE)
            }
        }
    }

    private val orderDetailFromFirebase: Unit
        get() {
            showProgressDialog(true)
            mValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showProgressDialog(false)
                    mOrder = snapshot.getValue(Order::class.java)
                    if (mOrder == null) return

                    initData()
                }

                override fun onCancelled(error: DatabaseError) {
                    showProgressDialog(false)
                    showToastMessage(getString(R.string.msg_get_date_error))
                }
            }
            get(this).getOrderDetailDatabaseReference(orderId)
                .addValueEventListener(mValueEventListener as ValueEventListener)
        }

    private fun initData() {
        val adapter = ProductOrderAdapter(mOrder!!.products)
        rcvProducts!!.adapter = adapter

        when (mOrder!!.status) {
            Order.STATUS_NEW -> {
                imgStep1!!.setImageResource(R.drawable.ic_step_enable)
                dividerStep1!!.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                imgStep2!!.setImageResource(R.drawable.ic_step_disable)
                dividerStep2!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                imgStep3!!.setImageResource(R.drawable.ic_step_disable)

                isOrderArrived = false
                tvTakeOrder!!.setBackgroundResource(R.drawable.bg_button_disable_corner_16)
                tvTakeOrderMessage!!.visibility = View.GONE
            }

            Order.STATUS_DOING -> {
                imgStep1!!.setImageResource(R.drawable.ic_step_enable)
                dividerStep1!!.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                imgStep2!!.setImageResource(R.drawable.ic_step_enable)
                dividerStep2!!.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                imgStep3!!.setImageResource(R.drawable.ic_step_disable)

                isOrderArrived = false
                tvTakeOrder!!.setBackgroundResource(R.drawable.bg_button_disable_corner_16)
                tvTakeOrderMessage!!.visibility = View.GONE
            }

            Order.STATUS_ARRIVED -> {
                imgStep1!!.setImageResource(R.drawable.ic_step_enable)
                dividerStep1!!.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                imgStep2!!.setImageResource(R.drawable.ic_step_enable)
                dividerStep2!!.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                imgStep3!!.setImageResource(R.drawable.ic_step_enable)

                isOrderArrived = true
                tvTakeOrder!!.setBackgroundResource(R.drawable.bg_button_enable_corner_16)
                tvTakeOrderMessage!!.visibility = View.VISIBLE
            }
        }
    }

    private fun updateStatusOrder(status: Int) {
        if (mOrder == null) return
        val map: MutableMap<String, Any> = HashMap()
        map["status"] = status

        get(this).orderDatabaseReference
            .child(mOrder!!.id.toString())
            .updateChildren(map) { _: DatabaseError?, _: DatabaseReference? ->
                if (Order.STATUS_COMPLETE == status) {
                    sendEmail(this@TrackingOrderActivity, mOrder!!)

                    val bundle = Bundle()
                    val ratingReview = RatingReview(
                        RatingReview.TYPE_RATING_REVIEW_ORDER,
                        mOrder!!.id.toString()
                    )
                    bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview)
                    startActivity(
                        this@TrackingOrderActivity,
                        RatingReviewActivity::class.java, bundle
                    )
                    finish()
                }
            }
    }

    private fun sendEmail(context: Context, order: Order) {
        try {
            val properties = Properties().apply {
                put(ConfigEmail.MAIL_HOST_KEY, ConfigEmail.MAIL_HOST_VALUE)
                put(ConfigEmail.MAIL_POST_KEY, ConfigEmail.MAIL_POST_VALUE)
                put(ConfigEmail.MAIL_SSL_KEY, "true")
                put(ConfigEmail.MAIL_AUTH_KEY, "true")
            }

            val session = Session.getInstance(properties, object : jakarta.mail.Authenticator() {
                override fun getPasswordAuthentication(): jakarta.mail.PasswordAuthentication {
                    return jakarta.mail.PasswordAuthentication(
                        ConfigEmail.SENDER_EMAIL,
                        ConfigEmail.PASSWORD_SENDER_EMAIL
                    )
                }
            })

            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(
                Message.RecipientType.TO,
                InternetAddress(ConfigEmail.RECEIVER_EMAIL)
            )

            mimeMessage.subject = context.getString(R.string.app_name)
            val emailTitle = context.getString(R.string.msg_email_title) + " " + order.id
            val emailProduct =
                context.getString(R.string.msg_email_products) + " " + order.listProductsName
            val emailTotal = context.getString(R.string.msg_email_total) + " " + order.total
            val emailPaymentMethod =
                context.getString(R.string.msg_email_payment_method) + " " + order.paymentMethod
            val strMessage = """
                $emailTitle
                $emailProduct
                $emailTotal
                $emailPaymentMethod
                """.trimIndent()
            mimeMessage.setText(strMessage)

            val thread = Thread {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mValueEventListener != null) {
            get(this).getOrderDetailDatabaseReference(orderId)
                .removeEventListener(mValueEventListener!!)
        }
    }
}
