package com.natto.chargetestapp

import android.content.Context
import android.content.Intent
import com.amazon.device.iap.PurchasingService
import org.koin.dsl.module.applicationContext

class AmazonStoreBillingAction :BillingActionContract {

  private var purchasingService:PurchasingService?=null

  override fun start(context: Context) {
    PurchasingService.registerListener(context,AmazonPurchasingListener())
  }

  override fun buy(context: Context,sku:String) {
    PurchasingService.purchase(sku)
  }

  override fun use(context: Context,sku:String) {

  }

  override fun end(context: Context) {

  }

  override fun result(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {

  }
}