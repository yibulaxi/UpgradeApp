package com.velkonost.upgrade.event

import androidx.fragment.app.Fragment

data class InitUserInterestsEvent(val data: Map<String, Float>, val f: Fragment)