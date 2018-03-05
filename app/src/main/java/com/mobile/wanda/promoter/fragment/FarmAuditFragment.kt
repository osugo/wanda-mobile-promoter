package com.mobile.wanda.promoter.fragment

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Created by kombo on 05/03/2018.
 */
class FarmAuditFragment: Fragment() {

    companion object {
        val ID: String = "id"
        val NAME: String = "name"

        fun newInstance(id: Long, name: String): FarmAuditFragment {
            val bundle = Bundle().apply {
                putLong(ID, id)
                putString(NAME, name)
            }

            val fragment = FarmAuditFragment()
            fragment.arguments = bundle

            return fragment
        }
    }
}