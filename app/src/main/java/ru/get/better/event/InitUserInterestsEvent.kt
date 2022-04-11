package ru.get.better.event

import ru.get.better.model.Interest

data class InitUserInterestsEvent(val data: List<Interest>)