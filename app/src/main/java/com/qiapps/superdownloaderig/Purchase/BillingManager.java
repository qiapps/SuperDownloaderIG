package com.qiapps.superdownloaderig.Purchase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.android.billingclient.api.*;
import java.util.List;

public class BillingManager {
    private static final String TAG = "BillingManager";
    private BillingClient billingClient;
    private BillingListener listener;

    public interface BillingListener {
        void onPurchaseCompleted(Purchase purchase);
        void onPurchaseError(String error);
    }

    public BillingManager(Context context, BillingListener listener) {
        this.listener = listener;
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing service connected.");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected.");
            }
        });
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else {
            listener.onPurchaseError("Erro na compra: " + billingResult.getDebugMessage());
        }
    };

    public void querySkuDetails(List<String> skuList, String skuType, SkuDetailsResponseListener callback) {
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build();
        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                callback.onSkuDetailsResponse(billingResult, skuDetailsList);
            } else {
                Log.e(TAG, "Erro ao buscar SKUs: " + billingResult.getDebugMessage());
            }
        });
    }

    public void launchPurchaseFlow(Activity activity, SkuDetails skuDetails) {
        BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        billingClient.launchBillingFlow(activity, purchaseParams);
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            listener.onPurchaseCompleted(purchase);
        }
    }

    public void endConnection() {
        billingClient.endConnection();
    }
}

