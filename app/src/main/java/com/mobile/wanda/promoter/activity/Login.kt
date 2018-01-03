package com.mobile.wanda.promoter.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.requests.LoginCredentials
import com.mobile.wanda.promoter.rest.HeaderlessRestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import com.mobile.wanda.promoter.util.PrefUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.login.*
import org.jetbrains.anko.design.snackbar


/**
 * Created by kombo on 29/11/2017.
 */

/**
 * Handles logging in of the users to the app
 */
class Login : AppCompatActivity(), View.OnClickListener {

    private lateinit var progressDialog: ProgressDialog

    companion object {
        val TAG: String = Login::class.java.simpleName
        val REQUEST_CODE_PERMISSION = 23
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        login.setOnClickListener(this)

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_PERMISSION)
            } else {
                //proceed to login
               login()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun login(){
        Handler().postDelayed({
            showProgressDialog() //indicate to the user that login process has started
            signInUser(username.text.toString(), password.text.toString())
        }, 200)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                login()
            } else {
                snackbar(parentLayout, "Location permission denied.")
                login()
            }
        }
    }

    /**
     * Handles the login button click
     */
    override fun onClick(v: View?) {
        //checks if user is connected to the internet before attempting to log in
        if (NetworkHelper.isOnline(this)) {
            //confirm username field is not empty before proceeding
            if (username.text.isEmpty()) {
                showSnackBar(getString(R.string.enter_username))
                return
            }

            //confirm password field is not empty before proceeding
            if (password.text.isEmpty()) {
                showSnackBar(getString(R.string.enter_password))
                return
            }

            //username and password fields not empty, continue to login
            if (username.text.isNotEmpty() && password.text.isNotEmpty()) {
                showProgressDialog() //indicate to the user that login process has started
                signInUser(username.text.toString(), password.text.toString())
            }

        } else {
            //show a SnackBar alerting the user that there is no internet connection available
            showSnackBar(getString(R.string.network_unavailable))
        }
    }

    /**
     * Show a SnackBar with a defined message to alert them of the current login state
     * @param message Message to be shown to the user
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Log the user into the app
     * @param username User's username entered in the username field
     * @param password User's password entered in the password field
     */
    private fun signInUser(username: String, password: String) {
        //build RestClient here
        HeaderlessRestClient.client.create(RestInterface::class.java)
                .login(LoginCredentials(getString(R.string.grant_type), getString(R.string.client_id), getString(R.string.client_secret),
                        username, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    hideProgressDialog()
                    //save credentials to PreferenceManager and proceed to home
                    PrefUtils.putString(PrefUtils.CREDENTIALS, Gson().toJson(it))

                    Log.e(TAG, "User logged in successfully")

                    startActivity(Intent(this, Home::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                }
                        , {
                    hideProgressDialog()
                    //show user error message
                    showSnackBar(it.localizedMessage)
                    //show user error message
                    Log.e(TAG, it.localizedMessage, it)
                })
    }

    /**
     * Show progress dialog to indicate login process has begun
     */
    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
    }

    /**
     * Hide progress dialog on completion of login action
     */
    private fun hideProgressDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }
}