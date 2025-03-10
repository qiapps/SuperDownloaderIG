package com.qiapps.superdownloaderig.Purchase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.android.billingclient.api.*;
import java.util.List;
import java.util.Arrays;

public class BillingManager {
    private static final String TAG = "BillingManager";
    private final BillingClient billingClient;
    private BillingListener listener;

    public interface BillingListener {
        void onPurchaseCompleted(Purchase purchase);
        void onPurchaseError(String error);
        void onPurchaseRestored(List<Purchase> purchases);
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
                    //queryPurchases(); // Verifica compras existentes ao iniciar
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

    // Inicia fluxo de compra do produto
    public void launchPurchaseFlow(Activity activity, String productId, String skuType) {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(Arrays.asList(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(skuType)
                        .build()))
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null && !productDetailsList.isEmpty()) {
                ProductDetails productDetails = productDetailsList.get(0);
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(Arrays.asList(BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()))
                        .build();
                billingClient.launchBillingFlow(activity, purchaseParams);
            } else {
                listener.onPurchaseError("Erro ao buscar o produto: " + billingResult.getDebugMessage());
            }
        });
    }

    public void queryPurchases() { // Verifica se o usuário já comprou
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(), (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchases != null && !purchases.isEmpty()) {
                    listener.onPurchaseRestored(purchases);
                } else {
                    listener.onPurchaseRestored(null);
                }
            } else {
                listener.onPurchaseRestored(null);
                Log.e(TAG, "Erro ao recuperar compras: " + billingResult.getDebugMessage());
            }
        });
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Verifica se a compra já foi reconhecida
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.acknowledgePurchase(acknowledgeParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        listener.onPurchaseCompleted(purchase);
                    } else {
                        listener.onPurchaseError("Erro ao reconhecer a compra: " + billingResult.getDebugMessage());
                    }
                });
            } else {
                listener.onPurchaseCompleted(purchase);
            }
        }
    }


    public void endConnection() {
        billingClient.endConnection();
    }
}


