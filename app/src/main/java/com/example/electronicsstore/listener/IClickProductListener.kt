package com.example.electronicsstore.listener

import com.example.electronicsstore.model.Product

interface IClickProductListener {
    fun onClickProductItem(product: Product?)
}
