package com.mobile.wanda.promoter.rest

import com.mobile.wanda.promoter.model.Order
import com.mobile.wanda.promoter.model.requests.*
import com.mobile.wanda.promoter.model.responses.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET("products/categories?search={searchTerm}")
    fun searchProductCategories(@Path("searchTerm") searchTerm: String): Observable<ProductResults>

    @GET("products/products?search={searchTerm}&category_id={categoryId}") //TODO I need sample result data to create return type
    fun searchProducts(@Path("searchTerm") searchTerm: String, @Path("category_id") categoryId: Int): Observable<ProductResults>

    @GET("products/variations?search={searchTerm}&category_id={categoryId}") //TODO I need sample result data to create return type
    fun getProductVariations(@Path("searchTerm") searchTerm: String, @Path("category_id") categoryId: Int): Observable<ProductResults>

    @POST("farmers/orders/create")  //TODO this returns a 404. Need to check on this
    fun placeOrder(@Body order: Order): Completable

    @POST("payments/pay-for-order")
    fun payOrder(@Body orderPayment: OrderPayment): Observable<PaymentResponse>

    @POST("payments/top-up-voucher")
    fun voucherTopUp(@Body voucherTopUpRequest: VoucherTopUpRequest): Observable<VoucherTopupResponse>

    @GET("commissions/check")
    fun checkCommission(): Observable<Commission>

    @POST("commissions/request-payment")
    fun requestCommission(): Observable<CommissionRequestResponse>

    @GET("lookup/farmer?search")
    fun getFarmers(): Observable<FarmerList>

    @GET("lookup/ward?search")
    fun getWards(): Observable<WardList>
}
