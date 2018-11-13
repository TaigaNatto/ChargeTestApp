package com.natto.chargetestapp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.Intent
import android.view.View
import android.app.PendingIntent
import android.util.Log
import android.widget.Toast
import com.android.vending.billing.IInAppBillingService
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

  var mService: IInAppBillingService? = null

  val normalItems= arrayListOf(arrayOf("item_banana_1_normal","200"))
  val premiumItems= arrayListOf(arrayOf("item_banana_1","100"))

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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == 1001) {
      val responseCode = data!!.getIntExtra("RESPONSE_CODE", 0)
      val purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA")

      if (resultCode == Activity.RESULT_OK) {
        try {
          val jo = JSONObject(purchaseData)
          val productId = jo.getString("productId")

          Toast.makeText(this, "購入成功しました", Toast.LENGTH_SHORT).show()
          // 購入成功後すぐに消費する
          // use();
        } catch (e: JSONException) {
          Toast.makeText(this, "Failed to parse purchase data.", Toast.LENGTH_SHORT).show()
          e.printStackTrace()
        }
      } else {
        Toast.makeText(this, "課金に失敗しました", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun buy() {
    try {
      // 購入リクエストの送信
      // item001 はGoogle Play Developer Consoleで作成した値を使う
      val buyIntentBundle = mService?.getBuyIntent(3, packageName, "item_banana_1", "inapp", "hoge")
      // レスポンスコードを取得する
      val response = buyIntentBundle?.getInt("RESPONSE_CODE")
      // 購入可能
      // BILLING_RESPONSE_RESULT_OK
      if (response == 0) {
        // 購入フローを開始する
        val pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT") as PendingIntent
        // 購入トランザクションの完了
        startIntentSenderForResult(
            pendingIntent.intentSender,
            1001,
            Intent(),
            Integer.valueOf(0),
            Integer.valueOf(0),
            Integer.valueOf(0))
      } else if (response == 1) {
        Toast.makeText(this, "購入がキャンセルされた", Toast.LENGTH_SHORT).show()
      } else if (response == 7) {
        Toast.makeText(this, "既に同じものを購入している", Toast.LENGTH_SHORT).show()
      }// BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED
      // BILLING_RESPONSE_RESULT_USER_CANCELED
    } catch (e: Exception) {
      e.printStackTrace()
      Log.d("EEE", e.message)
      Toast.makeText(this, "購入は失敗した", Toast.LENGTH_SHORT).show()
    }
  }

  fun use() {
    try {
      // 購入したものを全て消費する
      val ownedItems = mService?.getPurchases(3, packageName, "inapp", null)

      var response = ownedItems?.getInt("RESPONSE_CODE")
      if (response == 0) {
        val ownedSkus = ownedItems?.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")
        val purchaseDataList = ownedItems?.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
        val signatureList = ownedItems?.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")
        val continuationToken = ownedItems?.getString("INAPP_CONTINUATION_TOKEN")

        for (i in 0 until purchaseDataList!!.size) {
          val purchaseData = purchaseDataList[i]
          val `object` = JSONObject(purchaseData)
          val productId = `object`.getString("productId")
          val purchaseToken = `object`.getString("purchaseToken")

          // 消費する
          response = mService!!.consumePurchase(3, packageName, purchaseToken)

          // 正常終了
          if (response == 0) {
            Toast.makeText(this, "$productId + \"を消費しました。\"", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(this, purchaseData, Toast.LENGTH_SHORT).show()
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun check() {
    try {
      // 購入したものを確認する
      val ownedItems = mService?.getPurchases(3, packageName, "inapp", null)

      val response = ownedItems?.getInt("RESPONSE_CODE")
      if (response == 0) {
        val ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")
        val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
        val signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")
        val continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN")

        for (i in 0 until purchaseDataList!!.size) {
          val purchaseData = purchaseDataList!!.get(i)
          val `object` = JSONObject(purchaseData)
          val productId = `object`.getString("productId")
          val purchaseToken = `object`.getString("purchaseToken")

          Toast.makeText(this, "$productId,$purchaseToken", Toast.LENGTH_SHORT).show()
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
