package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.adapter.MenuAdapter
import com.mobile.wanda.promoter.model.MenuItem
import com.mobile.wanda.promoter.view.GridItemDecoration
import com.mobile.wanda.promoter.view.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val adapter = MenuAdapter(this, getMenuOptions())
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.addItemDecoration(GridItemDecoration(2, 5, false))
        recycler.adapter = adapter
    }

    /**
     * Create the list of menu options since they are relatively static
     */
    private fun getMenuOptions(): ArrayList<MenuItem> {
        val menu = ArrayList<MenuItem>()
        menu.add(MenuItem(getString(R.string.farmer_reg), R.drawable.ic_farmer))
        menu.add(MenuItem(getString(R.string.farm_audit), R.drawable.ic_fields))
        menu.add(MenuItem(getString(R.string.order_mgmt), R.drawable.ic_order))
        menu.add(MenuItem(getString(R.string.voucher_mgmt), R.drawable.ic_voucher))
        menu.add(MenuItem(getString(R.string.commission_mgmt), R.drawable.ic_commission))

        return menu
    }
}
