package com.genar.genarweather

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.identifier.AdvertisingIdClient
import org.prebid.mobile.Host
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient


class GenarWeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(
            this
        ) { }

        initPrebid()
    }

    private fun initPrebid(){
        val host = Host.CUSTOM
        host.hostUrl = Constants.PrebidConstants.CUSTOM_HOST_SERVER
        PrebidMobile.setPrebidServerHost(host)
        PrebidMobile.setPrebidServerAccountId(Constants.PrebidConstants.ACCOUNT_ID)

        PrebidMobile.setPbsDebug(true)
        PrebidMobile.setShareGeoLocation(true)
        PrebidMobile.useExternalBrowser = true
        Thread {
            try{
                val info = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                TargetingParams.addUserData("oaid", info.id)
            }catch (t:Throwable){
                val info = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                TargetingParams.addUserData("oaid", info.id)
            }
        }.start()

        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().apply {
            putBoolean("IABConsent_CMPPresent",true)
            putString("IABConsent_SubjectToGDPR", "0")

            putString("IABConsent_ConsentString", "CPaYLJBPaYLJBIPAAAENCSCgAPAAAAAAAAAAGsQAQGsAAAAA.YAAAAAAAAAA") //146
            apply()
        }

        PrebidMobile.initializeSdk(applicationContext,
            object : SdkInitializationListener {
                override fun onSdkInit() {
                    Log.i(GenarWeatherApplication::javaClass.name, "Prebid initialized successfully")
                }

                override fun onSdkFailedToInit(error: InitError?) {
                    Log.w(GenarWeatherApplication::javaClass.name, "Prebid init failed. ${error.toString()}")
                }
            })
    }
}