package com.natto.chargetestapp

import android.content.Context
import android.content.Intent

interface BillingActionContract {
  fun start(context: Context)
  fun buy(context: Context,id:String)
  fun use(context: Context,id:String)
  fun end(context: Context)
  fun result(context: Context,requestCode: Int, resultCode: Int, data: Intent?)
}