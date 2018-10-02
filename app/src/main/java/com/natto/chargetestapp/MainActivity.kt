package com.natto.chargetestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.natto.chargetestapp.R
import com.natto.billing.IInAppBillingService
import android.os.IBinder
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent

class MainActivity : AppCompatActivity() {

  var mService: IInAppBillingService? = null

  var mServiceConn: ServiceConnection = object : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName) {
      mService = null
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      mService = IInAppBillingService.Stub.asInterface(service)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
    serviceIntent.setPackage("com.android.vending")
    bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE)
  }

  public override fun onDestroy() {
    super.onDestroy()
    if (mService != null) {
      unbindService(mServiceConn)
    }
  }
}
