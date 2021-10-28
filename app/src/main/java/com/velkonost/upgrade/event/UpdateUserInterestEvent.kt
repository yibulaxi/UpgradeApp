package com.velkonost.upgrade.event

import com.velkonost.upgrade.model.Interest

data class UpdateUserInterestEvent(val interestId: String, val amount: Float)