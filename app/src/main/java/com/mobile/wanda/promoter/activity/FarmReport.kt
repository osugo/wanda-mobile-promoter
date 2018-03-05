package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.FarmAuditFragment
import com.mobile.wanda.promoter.fragment.FarmersList

/**
 * Created by kombo on 05/03/2018.
 */
class FarmReport: AppCompatActivity(), FarmersList.SelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, FarmersList())
                    .commitAllowingStateLoss()
        }
    }

    override fun onFarmerSelected(id: Long, name: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, FarmAuditFragment.newInstance(id, name))
                .commitAllowingStateLoss()
    }
}