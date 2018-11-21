package com.mobile.wanda.promoter.network

import com.mobile.wanda.promoter.model.*
import com.mobile.wanda.promoter.model.orders.Order
import com.mobile.wanda.promoter.model.orders.ProductResults
import com.mobile.wanda.promoter.model.requests.*
import com.mobile.wanda.promoter.model.responses.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by kombo on 23/11/2017.
 */

interface RestInterface {

    @get:GET("vouchers/balance")
    val voucherBalance: Observable<VoucherBalance>

    @POST("/wanda-mobile/oauth/token")
    fun login(@Body loginCredentials: LoginCredentials): Observable<AuthCredentials>

    @POST("farmers/register")
    fun registerFarmer(@Body registrationDetails: FarmerRegistrationDetails): Observable<FarmerRegistrationResponse>

    @POST("farmers/add-farm")
    fun addFarm(@Body details: FarmDetails): Observable<FarmCreationResponse>

    @POST("farmers/add-farm-visit")
    fun addFarmVisit(@Body farmReview: FarmReview): Observable<FarmReviewResponse>

    @GET("products/categories?")
    fun getProductCategories(): Observable<ProductResults>

    @GET("products/types")
    fun searchProducts(@Query("category_id") categoryId: Long): Observable<ProductResults>

    @GET("products/products")
    fun getProductVariations(@Query("type_id") typeId: Long): Observable<ProductResults>

    @POST("farmers/orders/create")
    fun createOrder(@Body order: Order): Observable<PendingOrder>

    @POST("farmers/orders/place")
    fun placeOrder(@Body request: OrderPlacementRequest): Observable<PendingOrder>

    @POST("payments/pay-for-order")
    fun payOrder(@Body orderPayment: OrderPayment): Observable<PaymentResponse>

    @POST("payments/top-up-farmer-voucher")
    fun farmerVoucherTopUp(@Body farmerVoucherTopUpRequest: FarmerVoucherTopUpRequest): Observable<VoucherTopupResponse>

    @POST("payments/top-up-promoter-voucher")
    fun promoterVoucherTopUp(@Body promoterVoucherTopUpRequest: PromoterVoucherTopUpRequest): Observable<VoucherTopupResponse>

    @get:GET("commissions/check")
    val checkCommission: Observable<Commission>

    @POST("commissions/request-payment")
    fun requestCommission(): Observable<CommissionRequestResponse>

    @get:GET("lookup/farmer?search")
    val getFarmers: Observable<FarmerList>

    @get:GET("lookup/ward?search")
    val getWards: Observable<WardList>

    @GET("lookup/farm/{farmerId}")
    fun getFarms(@Path("farmerId") farmerId: Int): Observable<FarmList>

    @get:GET("payments/pending-payments")
    val pendingPayments: Observable<PendingPaymentsList>

    @POST("payments/complete-pending-payments")
    fun completePendingPayment(@Body paymentBody: PaymentBody): Observable<PendingPaymentResponse>
}
