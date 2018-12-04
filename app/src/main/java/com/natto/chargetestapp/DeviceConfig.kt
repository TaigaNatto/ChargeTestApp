package com.natto.chargetestapp

import android.os.Build

object DeviceConfig {
  fun isFireTab(): Boolean {
    return when (Build.DEVICE.substring(0, 2)) {
      "KF" -> true
      else -> false
    }
  }
}