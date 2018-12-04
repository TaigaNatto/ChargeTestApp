package com.natto.chargetest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import com.natto.chargetestapp.BillingActionContract
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

  //端末によってplaystoreかamazonのアクションクラスがはいる
  private val billingAction: BillingActionContract by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    billingAction.start(this)
  }

  public override fun onDestroy() {
    super.onDestroy()
    billingAction.end(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    billingAction.result(this, requestCode, resultCode, data)
  }

  fun buy(v: View) {
    billingAction.buy(this, "item001")
  }

  fun use(v: View) {
    billingAction.use(this, "item001")
  }
}
