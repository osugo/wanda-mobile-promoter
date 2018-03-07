package com.mobile.wanda.promoter.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.wanda.promoter.R

/**
 * Created by kombo on 07/03/2018.
 */
class VoucherTopUpFragment: Fragment() {

    companion object {
        private val USER_ID: String = "userId"

        fun newInstance(userId: String) : VoucherTopUpFragment {
            val bundle = Bundle().apply {
                putString(USER_ID, userId)
            }

            val fragment = VoucherTopUpFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.voucher_top_up, container, false)

        return view
    }
}