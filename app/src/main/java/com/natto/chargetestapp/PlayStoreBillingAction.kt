package com.natto.chargetestapp

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.vending.billing.IInAppBillingService
import org.json.JSONException
import org.json.JSONObject

class PlayStoreBillingAction : BillingActionContract {

  var mService: IInAppBillingService? = null

  var mServiceConn: ServiceConnection = object : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName) {
      mService = null
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      mService = IInAppBillingService.Stub.asInterface(service)
    }
  }

  override fun start(context: Context) {
    val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
    serviceIntent.setPackage("com.android.vending")
    context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE)
  }

  override fun use(context: Context) {
    try {
      // 購入したものを全て消費する
      val ownedItems = mService?.getPurchases(3, context.packageName, "inapp", null)

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
          response = mService!!.consumePurchase(3, context.packageName, purchaseToken)

          // 正常終了
          if (response == 0) {
            Toast.makeText(context, "$productId + \"を消費しました。\"", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(context, purchaseData, Toast.LENGTH_SHORT).show()
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun buy(context: Context) {
    try {
      // 購入リクエストの送信
      // item001 はGoogle Play Developer Consoleで作成した値を使う
      val buyIntentBundle = mService?.getBuyIntent(3, context.packageName, "item001", "inapp",
          "hoge")
      // レスポンスコードを取得する
      val response = buyIntentBundle?.getInt("RESPONSE_CODE")
      // 購入可能
      // BILLING_RESPONSE_RESULT_OK
      if (response == 0) {
        // 購入フローを開始する
        val pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT") as PendingIntent
        // 購入トランザクションの完了
        (context as AppCompatActivity).startIntentSenderForResult(
            pendingIntent.intentSender,
            1001,
            Intent(),
            Integer.valueOf(0),
            Integer.valueOf(0),
            Integer.valueOf(0))
      } else if (response == 1) {
        Toast.makeText(context, "購入がキャンセルされた", Toast.LENGTH_SHORT).show()
      } else if (response == 7) {
        Toast.makeText(context, "既に同じものを購入している", Toast.LENGTH_SHORT).show()
      }// BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED
      // BILLING_RESPONSE_RESULT_USER_CANCELED
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "購入は失敗した", Toast.LENGTH_SHORT).show()
    }
  }

  override fun end(context: Context) {
    if (mService != null) {
      context.unbindService(mServiceConn)
    }
  }

  override fun result(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == 1001) {
      val responseCode = data!!.getIntExtra("RESPONSE_CODE", 0)
      val purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA")

      Log.d("RESULT_CODE", responseCode.toString())
      if (resultCode == Activity.RESULT_OK) {
        try {
          val jo = JSONObject(purchaseData)
          val productId = jo.getString("productId")

          Toast.makeText(context, "購入成功しました", Toast.LENGTH_SHORT).show()
          // 購入成功後すぐに消費する
          // use();
        } catch (e: JSONException) {
          Toast.makeText(context, "Failed to parse purchase data.", Toast.LENGTH_SHORT).show()
          e.printStackTrace()
        }
      } else {
        Toast.makeText(context, "課金に失敗しました", Toast.LENGTH_SHORT).show()
      }
    }
  }
}