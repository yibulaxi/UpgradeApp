package ru.get.better.repo.nasa

import io.reactivex.Single
import ru.get.better.model.NasaImg
import ru.get.better.repo.base.NasaRepo
import ru.get.better.rest.NasaService
import ru.get.better.util.ext.subscribeIoObserveMain
import javax.inject.Inject

class NasaRepoImpl @Inject constructor(
    private val nasaService: NasaService
): NasaRepo {

    override fun getSpaceImg(): Single<NasaImg> =
        nasaService.getSpaceImg().subscribeIoObserveMain()

}