package com.natto.chargetestapp

import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.model.ProductDataResponse
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.PurchaseUpdatesResponse
import com.amazon.device.iap.model.UserDataResponse
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.Receipt



class AmazonPurchasingListener : PurchasingListener {

  /**
   * onProductDataResponse --- プロダクト情報の取得
   *   - PurchasingService.getProductData() のコールバック関数
   *   - プロダクト情報を取得する必要がなければ、適当な実装でOK。
   */
  override fun onProductDataResponse(response: ProductDataResponse?) {
    val status = response?.requestStatus
    when (status) {
      ProductDataResponse.RequestStatus.SUCCESSFUL  // 成功
      -> {
        /** 利用不能なプロダクト  */
        for (s in response.unavailableSkus) {

        }
        /** 利用可能なプロダクト情報  */
        val products = response.productData
        for (key in products.keys) {
          val product = products[key]
        }
      }

      ProductDataResponse.RequestStatus.FAILED, ProductDataResponse.RequestStatus.NOT_SUPPORTED -> {
      }
    }
  }

  /**
   * onPurchaseResponse --- 購入情報の取得
   *   - 購入後に必要な処理があるならココで処理。
   *   - 自前サーバなどで処理させたりする場合もココで。
   */
  override fun onPurchaseResponse(response: PurchaseResponse?) {
    val receipt: Receipt? = null
    when (response?.requestStatus) {
      // 購入完了
      PurchaseResponse.RequestStatus.SUCCESSFUL
      -> {

      }

      // 購入済み
      PurchaseResponse.RequestStatus.ALREADY_PURCHASED
      -> {

      }

      // 購入に失敗したときだけでなく、購入画面でキャンセルを押した場合にも呼ばれる。
      PurchaseResponse.RequestStatus.FAILED,
      PurchaseResponse.RequestStatus.INVALID_SKU,
      PurchaseResponse.RequestStatus.NOT_SUPPORTED -> {

      }
    }
  }

  /**
   * onPurchaseUpdatesResponse --- 過去の購入状況の確認
   *   - PurchasingService.getPurchaseUpdates() のコールバック関数
   *   - ユーザーが購入した端末とは別の端末にインストールしたり、再インストールしたりしたときに、購入情報からアイテムを復元するときはココで。
   */
  override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse?) {
    val status = response?.requestStatus
    when (status) {
      PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL
      -> {
        /** 購入履歴を参照 **/
        for (receipt in response.receipts) {
          val receiptId = receipt.receiptId
          val sku = receipt.sku
          val productType = receipt.productType
          val purchaseDate = receipt.purchaseDate
          val cancelDate = receipt.cancelDate
        }

        // 読み込みきれていない購入履歴があれば、更に読み込む(？)
        if (response.hasMore()) {
          PurchasingService.getPurchaseUpdates(false)
        }
      }
      PurchaseUpdatesResponse.RequestStatus.FAILED, PurchaseUpdatesResponse.RequestStatus.NOT_SUPPORTED -> {
      }
    }
  }

  /**
   * onUserDataResponse --- アカウント情報の取得
   *   - PurchasingService.getUserData() のコールバック関数
   *   - アカウント情報を知る必要がなければ、適当な実装でOK。
   */
  override fun onUserDataResponse(response: UserDataResponse?) {
    val status = response?.requestStatus
    when (status) {
      UserDataResponse.RequestStatus.SUCCESSFUL  // 取得に成功
      -> {
        val currentUserId = response.userData.userId
        val currentMarketplace = response.userData.marketplace
      }

      UserDataResponse.RequestStatus.FAILED, UserDataResponse.RequestStatus.NOT_SUPPORTED -> {
      }
    }
  }
}