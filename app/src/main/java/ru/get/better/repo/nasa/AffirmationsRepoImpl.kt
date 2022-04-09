package ru.get.better.repo.nasa

import io.reactivex.Single
import ru.get.better.model.AffirmationResponse
import ru.get.better.repo.base.AffirmationsRepo
import ru.get.better.rest.AffirmationService
import ru.get.better.util.ext.subscribeIoObserveMain
import javax.inject.Inject

class AffirmationsRepoImpl @Inject constructor(
    private val affirmationService: AffirmationService
): AffirmationsRepo {

    override fun getAffirmations(): Single<List<AffirmationResponse>> =
        affirmationService.getAffirmations().subscribeIoObserveMain()

}