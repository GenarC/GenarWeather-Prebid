package com.genar.genarweather

import org.prebid.mobile.AdSize

class Constants {
    object PrebidConstants {
        const val CUSTOM_HOST_SERVER = "http://80.158.60.28:8000/openrtb2/auction?test=1"
        const val CUSTOM_HOST_LOCAL = "http://192.168.137.1:8000/openrtb2/auction?test=1"
        const val ACCOUNT_ID = "test_req"

        const val BANNER_CONFIG_ID = "test_imp_banner"
        val BANNER_ADSIZE = AdSize(320, 50)

        const val INTERSTITIAL_CONFIG_ID = "test_imp_interstitial"
        const val NATIVE_CONFIG_ID = "test_imp_native"
        const val REWARDED_CONFIG_ID = "test_imp_rewarded"



        //AdMob
        const val ADMOB_RENDERING_API_BANNER_ID = "ca-app-pub-1479896569354723/4434453233"
        const val ADMOB_RENDERING_API_INTERSTITIAL_ID = "ca-app-pub-1479896569354723/3859738164"
        const val ADMOB_RENDERING_API_NATIVE_ID = "ca-app-pub-1479896569354723/5702199224"

        //AdmobTest
        const val ADMOB_TEST_RENDERING_API_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        const val ADMOB_TEST_RENDERING_API_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    }
}