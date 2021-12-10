package ru.get.better.event

import androidx.fragment.app.Fragment
import ru.get.better.model.Interest

data class InitUserInterestsEvent(val data: List<Interest>, val f: Fragment)