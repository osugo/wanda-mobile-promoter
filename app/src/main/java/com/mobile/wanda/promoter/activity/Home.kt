package com.mobile.wanda.promoter.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.adapter.MenuAdapter
import com.mobile.wanda.promoter.model.MenuItem
import com.mobile.wanda.promoter.service.BackgroundDataLoaderService
import com.mobile.wanda.promoter.view.GridItemDecoration
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity

class Home : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val adapter = MenuAdapter(getMenuOptions(), object : MenuAdapter.ClickListener {
            override fun onMenuItemClicked(position: Int) {
                navigate(position)
            }
        })
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.addItemDecoration(GridItemDecoration(2, 5, false))
        recycler.adapter = adapter

        loadWards()
    }

    /**
     * Fetch wards in the background to be used later for lookups
     */
    private fun loadWards() {
        startService(Intent(this, BackgroundDataLoaderService::class.java).setAction(BackgroundDataLoaderService.GET_WARDS))
    }

    /**
     * Create the list of menu options since they are relatively static
     */
    private fun getMenuOptions(): ArrayList<MenuItem> {
        val menu = ArrayList<MenuItem>()
        menu.add(MenuItem(getString(R.string.farmer_reg), R.drawable.ic_farmer))
        menu.add(MenuItem(getString(R.string.add_farm), R.drawable.ic_fields))
        menu.add(MenuItem(getString(R.string.farm_visit), R.drawable.ic_farm))
        menu.add(MenuItem(getString(R.string.order_mgmt), R.drawable.ic_order))
        menu.add(MenuItem(getString(R.string.farmer_voucher), R.drawable.ic_voucher))
        menu.add(MenuItem(getString(R.string.promoter_voucher), R.drawable.ic_voucher))
        menu.add(MenuItem(getString(R.string.commission_mgmt), R.drawable.ic_commission))

        return menu
    }

    /**
     * Load necessary activity depending on user choice
     */
    private fun navigate(position: Int) {
        when (position) {
            0 -> startActivity<FarmerRegistration>()
            1 -> startActivity<FarmCreation>()
            2 -> startActivity<FarmAudit>()
            3 -> startActivity<OrderManagement>()
            4 -> startActivity<FarmerVoucherTopup>()
            5 -> startActivity<PromoterVoucher>()
            6 -> startActivity<Commissions>()
        }
    }
}
