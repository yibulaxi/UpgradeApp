package ru.get.better.repo.base

import io.reactivex.Single
import ru.get.better.model.AffirmationResponse

interface AffirmationsRepo {

    fun getAffirmations(): Single<List<AffirmationResponse>>
}