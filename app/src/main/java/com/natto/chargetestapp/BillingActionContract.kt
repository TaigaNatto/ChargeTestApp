package com.natto.chargetestapp

import android.content.Context
import android.content.Intent

interface BillingActionContract {
  fun start(context: Context)
  fun buy(context: Context)
  fun use(context: Context)
  fun end(context: Context)
  fun result(context: Context,requestCode: Int, resultCode: Int, data: Intent?)
}