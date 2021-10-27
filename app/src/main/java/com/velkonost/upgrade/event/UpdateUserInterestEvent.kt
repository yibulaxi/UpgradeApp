package com.velkonost.upgrade.event

import com.velkonost.upgrade.model.Interest

data class UpdateUserInterestEvent(val interest: Interest, val amount: Double)