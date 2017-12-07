package com.mobile.wanda.promoter.event

/**
 * Created by kombo on 07/12/2017.
 */

/**
 * Used by the EventBus to send selected menu option event to the relevant subscriber
 */
data class MenuSelectionEvent(val selectedMenu: String)