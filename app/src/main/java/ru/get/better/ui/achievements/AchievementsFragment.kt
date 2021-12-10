package ru.get.better.ui.achievements

import ru.get.better.R
import ru.get.better.databinding.FragmentAchievementsBinding
import ru.get.better.ui.base.BaseFragment
import ru.get.better.vm.BaseViewModel

class AchievementsFragment : BaseFragment<BaseViewModel, FragmentAchievementsBinding>(
    R.layout.fragment_achievements,
    BaseViewModel::class,
    Handler::class
) {

    inner class Handler
}