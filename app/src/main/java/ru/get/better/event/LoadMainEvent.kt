package ru.get.better.event

import androidx.fragment.app.Fragment

data class LoadMainEvent(val isAuthSuccess: Boolean, val f: Fragment)