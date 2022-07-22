package com.genar.genarweather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.nativead.NativeAd
import com.kwabenaberko.openweathermaplib.constant.Languages
import com.kwabenaberko.openweathermaplib.constant.Units
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import com.kwabenaberko.openweathermaplib.implementation.callback.ThreeHourForecastCallback
import com.kwabenaberko.openweathermaplib.model.threehourforecast.ThreeHourForecast
import com.kwabenaberko.openweathermaplib.model.threehourforecast.ThreeHourForecastWeather
import org.prebid.mobile.*
import java.util.*
import com.genar.genarweather.Constants.PrebidConstants.ADMOB_RENDERING_API_NATIVE_ID
import com.genar.genarweather.Constants.PrebidConstants.NATIVE_CONFIG_ID
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.OnAdManagerAdViewLoadedListener
import com.google.android.gms.ads.nativead.NativeCustomFormatAd
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.api.rendering.listeners.BannerViewListener
import java.lang.Exception


class MainActivity : AppCompatActivity(), BannerViewListener {

    private val TAG: String = "PrebidWeatherActivity"

    lateinit var helper: OpenWeatherMapHelper
    private lateinit var btnSearch: Button
    private lateinit var etLocation: EditText
    lateinit var rvSearchResults: RecyclerView
    lateinit var weatherList: List<ThreeHourForecastWeather>
    lateinit var nativeWrapper: LinearLayout


    private var adManagerAdView: AdManagerAdView? = null
    private var unifiedNativeAd: NativeAd? = null
    private var prebidNativeAdUnit: NativeAdUnit? = null
    private var prebidNativeAdLoader: AdLoader? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch = findViewById(R.id.btn_search)
        etLocation = findViewById(R.id.et_searchArea)
        rvSearchResults = findViewById(R.id.rv_weatherResults)
        nativeWrapper = findViewById(R.id.ll_nativeContainer)


        helper = OpenWeatherMapHelper("bc5af352194555accfb1492beb62c903")
        helper.setUnits(Units.METRIC)
        helper.setLanguage(Languages.ENGLISH)

        btnSearch.setOnClickListener {
            helper.getThreeHourForecastByCityName(etLocation.text.toString(), object : ThreeHourForecastCallback {
                override fun onSuccess(threeHourForecast: ThreeHourForecast?) {
                    weatherList = threeHourForecast!!.list

                    val recyclerView = findViewById<RecyclerView>(R.id.rv_weatherResults)

                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerView.adapter = WeatherItemAdapter(weatherList, this@MainActivity)

                }

                override fun onFailure(throwable: Throwable?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Result could not be found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            //createNativeAdRequest(nativeWrapper, ADMOB_RENDERING_API_NATIVE_ID, NATIVE_CONFIG_ID,"11934135")
        }

        createBannerAdRequest()
    }

    private fun createBannerAdRequest(){
//        val dfpAdView = AdManagerAdView(this)
//        dfpAdView.adUnitId = "ca-app-pub-1479896569354723/4434453233"
//        dfpAdView.setAdSize(AdSize(320,50))
//        val request: AdRequest = AdRequest.Builder().build()
//
//        val bannerAdUnit = BannerAdUnit(Constants.PrebidConstants.BANNER_CONFIG_ID, 320, 50)
//        val parameters = BannerBaseAdUnit.Parameters()
//        parameters.api = listOf(Signals.Api(6), Signals.Api(7))
//        // alternate representation using an enum parameters.setApi(Arrays.asList(Signals.Api.MRAID_3, Signals.Api.OMID_1));
//
//        bannerAdUnit.parameters = parameters
//        bannerAdUnit.fetchDemand(request
//        ) {
//            dfpAdView.loadAd(request)
//        }

        val bannerContainer = findViewById<LinearLayout>(R.id.ll_bannerContainer)
        val bannerNoSDKView = BannerView(
            this,
            Constants.PrebidConstants.BANNER_CONFIG_ID,
            Constants.PrebidConstants.BANNER_ADSIZE
        )
        bannerNoSDKView.setAutoRefreshDelay(60000)
        bannerNoSDKView.setBannerListener(this)
        bannerContainer.addView(bannerNoSDKView)
        bannerNoSDKView.loadAd()
    }

    private fun createNativeAdRequest(
        wrapper: ViewGroup,
        adUnitId: String,
        configId: String?,
        customFormatId: String
    ) {
        prebidNativeAdUnit = NativeAdUnit(configId!!)
        configureNativeAdUnit(prebidNativeAdUnit!!)
        val adRequest = AdManagerAdRequest.Builder().build()
        prebidNativeAdLoader = createAdLoader(wrapper, adUnitId, customFormatId)
        prebidNativeAdUnit!!.fetchDemand(adRequest) {
            prebidNativeAdLoader!!.loadAd(adRequest)
        }
    }
    private fun destroyNativeAd() {
        if (adManagerAdView != null) {
            adManagerAdView!!.destroy()
            adManagerAdView = null
        }
        if (unifiedNativeAd != null) {
            unifiedNativeAd!!.destroy()
            unifiedNativeAd = null
        }
        if (prebidNativeAdUnit != null) {
            prebidNativeAdUnit!!.stopAutoRefresh()
            prebidNativeAdUnit = null
        }
        prebidNativeAdLoader = null
    }

    private fun inflatePrebidNativeAd(
        ad: PrebidNativeAd,
        wrapper: ViewGroup
    ) {
        val nativeContainer = View.inflate(wrapper.context, R.layout.layout_native, null)
        ad.registerView(nativeContainer, object : PrebidNativeAdEventListener {
            override fun onAdClicked() {}

            override fun onAdImpression() {}

            override fun onAdExpired() {}
        })
        val icon = nativeContainer.findViewById<ImageView>(R.id.imgIcon)
        loadImage(icon, ad.iconUrl)
        val title = nativeContainer.findViewById<TextView>(R.id.tvTitle)
        title.text = ad.title
        val image = nativeContainer.findViewById<ImageView>(R.id.imgImage)
        loadImage(image, ad.imageUrl)
        val description = nativeContainer.findViewById<TextView>(R.id.tvDesc)
        description.text = ad.description
        val cta = nativeContainer.findViewById<Button>(R.id.btnCta)
        cta.text = ad.callToAction
        wrapper.addView(nativeContainer)
    }

    private fun createAdLoader(
        wrapper: ViewGroup,
        adUnitId: String,
        customFormatId: String
    ): AdLoader? {
        val onGamAdLoaded = OnAdManagerAdViewLoadedListener { adManagerAdView: AdManagerAdView? ->
            Log.d(TAG, "Gam loaded")
            this.adManagerAdView = adManagerAdView
            wrapper.addView(adManagerAdView)
        }
        val onUnifiedAdLoaded =
            NativeAd.OnNativeAdLoadedListener { unifiedNativeAd: NativeAd ->
                Log.d(TAG, "Unified native loaded")
                this.unifiedNativeAd = unifiedNativeAd
            }
        val onCustomAdLoaded =
            NativeCustomFormatAd.OnCustomFormatAdLoadedListener { nativeCustomTemplateAd: NativeCustomFormatAd? ->
                Log.d(TAG, "Custom ad loaded")
                AdViewUtils.findNative(
                    nativeCustomTemplateAd!!,
                    object : PrebidNativeAdListener {
                        override fun onPrebidNativeLoaded(ad: PrebidNativeAd) {
                            inflatePrebidNativeAd(ad, wrapper)
                        }

                        override fun onPrebidNativeNotFound() {
                            Log.e(TAG, "onPrebidNativeNotFound")
                            // inflate nativeCustomTemplateAd
                        }

                        override fun onPrebidNativeNotValid() {
                            Log.e(TAG, "onPrebidNativeNotFound")
                            // show your own content
                        }
                    })
            }
        return AdLoader.Builder(wrapper.context, adUnitId)
            .forAdManagerAdView(onGamAdLoaded, AdSize.BANNER)
            .forNativeAd(onUnifiedAdLoaded)
            .forCustomFormatAd(customFormatId, onCustomAdLoaded
            ) { _: NativeCustomFormatAd?, _: String? -> }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Toast.makeText(wrapper.context, "DFP onAdFailedToLoad", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .build()
    }

    private fun configureNativeAdUnit(adUnit: NativeAdUnit) {
        adUnit.setContextType(NativeAdUnit.CONTEXT_TYPE.SOCIAL_CENTRIC)
        adUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.CONTENT_FEED)
        adUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.GENERAL_SOCIAL)
        val methods = ArrayList<NativeEventTracker.EVENT_TRACKING_METHOD>()
        methods.add(NativeEventTracker.EVENT_TRACKING_METHOD.IMAGE)
        methods.add(NativeEventTracker.EVENT_TRACKING_METHOD.JS)
        try {
            val tracker = NativeEventTracker(NativeEventTracker.EVENT_TYPE.IMPRESSION, methods)
            adUnit.addEventTracker(tracker)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val title = NativeTitleAsset()
        title.setLength(90)
        title.isRequired = true
        adUnit.addAsset(title)
        val icon = NativeImageAsset(20, 20, 20, 20)
        icon.imageType = NativeImageAsset.IMAGE_TYPE.ICON
        icon.isRequired = true
        adUnit.addAsset(icon)
        val image = NativeImageAsset(200, 200, 200, 200)
        image.imageType = NativeImageAsset.IMAGE_TYPE.MAIN
        image.isRequired = true
        adUnit.addAsset(image)
        val data = NativeDataAsset()
        data.len = 90
        data.dataType = NativeDataAsset.DATA_TYPE.SPONSORED
        data.isRequired = true
        adUnit.addAsset(data)
        val body = NativeDataAsset()
        body.isRequired = true
        body.dataType = NativeDataAsset.DATA_TYPE.DESC
        adUnit.addAsset(body)
        val cta = NativeDataAsset()
        cta.isRequired = true
        cta.dataType = NativeDataAsset.DATA_TYPE.CTATEXT
        adUnit.addAsset(cta)
    }

    private fun loadImage(
        image: ImageView,
        url: String
    ) {
        DownloadImageTask(image).execute(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyNativeAd()
    }

    override fun onAdLoaded(bannerView: BannerView?) {
        Log.d(TAG, "BannerAd is loaded")

    }

    override fun onAdDisplayed(bannerView: BannerView?) {
        Log.d(TAG, "BannerAd is displayed")

    }

    override fun onAdFailed(bannerView: BannerView?, exception: AdException?) {
        Log.d(TAG, "BannerAd is failed. Exception: ${exception.toString()}")
    }

    override fun onAdClicked(bannerView: BannerView?) {
        Log.d(TAG, "BannerAd is clicked")
    }

    override fun onAdClosed(bannerView: BannerView?) {
        Log.d(TAG, "BannerAd is closed")
    }


}