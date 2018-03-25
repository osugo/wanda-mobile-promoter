package com.mobile.wanda.promoter.rest

import com.mobile.wanda.promoter.model.orders.Order
import com.mobile.wanda.promoter.model.orders.PendingOrder
import com.mobile.wanda.promoter.model.orders.ProductResults
import com.mobile.wanda.promoter.model.requests.*
import com.mobile.wanda.promoter.model.responses.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by kombo on 23/11/2017.
 */

interface RestInterface {

    @GET("vouchers/balance")
    fun voucherBalance(): Observable<VoucherBalance>

    @POST("/wanda-mobile/oauth/token")
    fun login(@Body loginCredentials: LoginCredentials): Observable<AuthCredentials>

    @POST("farmers/register")
    fun registerFarmer(@Body registrationDetails: FarmerRegistrationDetails): Observable<FarmerRegistrationResponse>

    @POST("farmers/add-farm")
    fun addFarm(@Body auditDetails: FarmAuditDetails): Observable<FarmAuditResponse>

    @GET("products/categories?")
    fun getProductCategories(): Observable<ProductResults>

    @GET("products/types?category_id=")
    fun searchProducts(@Query("category_id") categoryId: Long): Observable<ProductResults>

    @GET("products/variations?search={searchTerm}&category_id={categoryId}") //TODO I need sample result data to create return type
    fun getProductVariations(@Path("searchTerm") searchTerm: String, @Path("category_id") categoryId: Int): Observable<ProductResults>

    @POST("farmers/orders/create")
    fun placeOrder(@Body order: Order): Observable<PendingOrder>

    @POST("payments/pay-for-order")
    fun payOrder(@Body orderPayment: OrderPayment): Observable<PaymentResponse>

    @POST("payments/top-up-farmer-voucher")
    fun farmerVoucherTopUp(@Body farmerVoucherTopUpRequest: FarmerVoucherTopUpRequest): Observable<VoucherTopupResponse>

    @POST("payments/top-up-promoter-voucher")
    fun promoterVoucherTopUp(@Body promoterVoucherTopUpRequest: PromoterVoucherTopUpRequest): Observable<VoucherTopupResponse>

    @GET("commissions/check")
    fun checkCommission(): Observable<Commission>

    @POST("commissions/request-payment")
    fun requestCommission(): Observable<CommissionRequestResponse>

    @GET("lookup/farmer?search")
    fun getFarmers(): Observable<FarmerList>

    @GET("lookup/ward?search")
    fun getWards(): Observable<WardList>
}
