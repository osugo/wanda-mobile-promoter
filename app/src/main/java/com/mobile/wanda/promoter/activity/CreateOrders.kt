package com.mobile.wanda.promoter.activity

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.FarmersList
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

/**
 * Created by kombo on 08/03/2018.
 */
class CreateOrders : BaseActivity(), FarmersList.SelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null && !isFinishing) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, FarmersList())
                    .commitAllowingStateLoss()
        }
    }

    override fun onFarmerSelected(id: Long, name: String) {
        startActivity(intentFor<ProductsList>("farmerId" to id, "farmerName" to name).clearTop())
    }
}