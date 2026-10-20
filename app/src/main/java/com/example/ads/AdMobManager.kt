package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * AdMob Configuration containing the real Ad Unit IDs provided by the user.
 */
object AdMobConfig {
    // 1. Banner debajo
    const val BANNER_AD_UNIT_ID = "ca-app-pub-1495262574338316/2803163788"

    // 2-8. Automatic Interstitial (Anuncios emergentes automáticos reales)
    val INTERSTITIAL_AD_UNIT_IDS = listOf(
        "ca-app-pub-1495262574338316/2903409205", // 2.
        "ca-app-pub-1495262574338316/2452733430", // 3.
        "ca-app-pub-1495262574338316/8970040322", // 4.
        "ca-app-pub-1495262574338316/1182179137", // 5.
        "ca-app-pub-1495262574338316/4962596335", // 6.
        "ca-app-pub-1495262574338316/2714752655", // 7.
        "ca-app-pub-1495262574338316/3503687777"  // 8.
    )
}

/**
 * Singleton Manager to initialize Mobile Ads, cycle through interstitial ad units,
 * and display automatic popups between levels and matches.
 */
object AdMobManager {
    private const val TAG = "AdMobManager"

    private var isInitialized = false
    private var currentInterstitial: InterstitialAd? = null
    private var isLoadingInterstitial = false
    private var interstitialIndex = 0

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                isInitialized = true
                Log.d(TAG, "Google Mobile Ads SDK Initialized: ${status.adapterStatusMap.keys}")
                // Preload first interstitial ad
                preloadNextInterstitial(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    /**
     * Cycles through the 7 Interstitial ad units in sequence with automatic fallback on no-fill.
     */
    fun preloadNextInterstitial(context: Context, retryCount: Int = 0) {
        if (isLoadingInterstitial || currentInterstitial != null) return
        isLoadingInterstitial = true

        val adUnitId = AdMobConfig.INTERSTITIAL_AD_UNIT_IDS[interstitialIndex % AdMobConfig.INTERSTITIAL_AD_UNIT_IDS.size]
        interstitialIndex++

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    currentInterstitial = interstitialAd
                    isLoadingInterstitial = false
                    Log.d(TAG, "Interstitial Ad Loaded successfully with ID: $adUnitId")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    currentInterstitial = null
                    isLoadingInterstitial = false
                    Log.w(TAG, "Interstitial Ad Unit $adUnitId failed to load: ${loadAdError.message}")
                    // Automatically try next real interstitial unit in rotation if under retry limit
                    if (retryCount < AdMobConfig.INTERSTITIAL_AD_UNIT_IDS.size - 1) {
                        preloadNextInterstitial(context, retryCount + 1)
                    }
                }
            }
        )
    }

    /**
     * Shows an automatic interstitial ad if available and triggers callback when dismissed or failed.
     */
    fun showAutomaticInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = currentInterstitial
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    currentInterstitial = null
                    preloadNextInterstitial(activity)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Failed to show interstitial: ${adError.message}")
                    currentInterstitial = null
                    preloadNextInterstitial(activity)
                    onDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial Ad displayed on screen.")
                }
            }
            ad.show(activity)
        } else {
            // Not ready yet, preload for next time and continue smoothly
            preloadNextInterstitial(activity)
            onDismissed()
        }
    }
}

/**
 * Bottom AdMob Banner Composable anchored underneath game controls.
 */
@Composable
fun AdMobBannerView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobConfig.BANNER_AD_UNIT_ID
) {
    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("AdMobBanner", "Banner ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w("AdMobBanner", "Banner ad failed: ${error.message}")
                }
            }
        }
    }

    DisposableEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
        onDispose {
            adView.destroy()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.Black.copy(alpha = 0.90f)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
