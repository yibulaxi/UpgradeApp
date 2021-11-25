package com.velkonost.upgrade.event

import androidx.fragment.app.Fragment
import com.velkonost.upgrade.model.Interest

data class InitUserInterestsEvent(val data: List<Interest>, val f: Fragment)