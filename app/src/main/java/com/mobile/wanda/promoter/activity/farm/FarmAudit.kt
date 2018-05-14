package com.mobile.wanda.promoter.activity.farm

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.activity.BaseActivity
import com.mobile.wanda.promoter.fragment.FarmersList
import com.mobile.wanda.promoter.fragment.FarmsList
import org.jetbrains.anko.intentFor

/**
 * Created by kombo on 02/05/2018.
 */
class FarmAudit : BaseActivity(), FarmersList.SelectionListener, FarmsList.SelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.contentFrame, FarmersList()).commitAllowingStateLoss()
        }
    }

    override fun onFarmerSelected(id: Long, name: String) {
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, FarmsList.newInstance(id.toInt())).commitAllowingStateLoss()
    }

    override fun onFarmSelected(id: Int?) {
        id?.let { startActivity(intentFor<FarmVisit>(FarmVisit.FARM_ID to id)) }
    }
}