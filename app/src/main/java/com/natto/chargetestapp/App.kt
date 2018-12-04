package com.natto.chargetestapp

import android.app.Application
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    startKoin(
        this, listOf(this.billingModule)
    )
  }

  private val billingModule: Module = applicationContext {
    //FireのみAmazonAppStoreのIAPを使用
    if (DeviceConfig.isFireTab()) {
      factory { AmazonStoreBillingAction() as BillingActionContract }
    } else {
      factory { PlayStoreBillingAction() as BillingActionContract }
    }
  }
}