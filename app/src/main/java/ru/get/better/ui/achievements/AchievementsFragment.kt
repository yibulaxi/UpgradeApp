package ru.get.better.ui.achievements

import android.os.Bundle
import android.util.Log
import android.view.View

import ru.get.better.databinding.FragmentAchievementsBinding
import ru.get.better.ui.base.BaseFragment
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope

import androidx.viewpager.widget.PagerAdapter
import com.lriccardo.timelineview.TimelineDecorator
import com.lriccardo.timelineview.TimelineView
import kotlinx.android.synthetic.main.item_achievement.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ru.get.better.R
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.model.Achievement
import ru.get.better.ui.achievements.adapter.AchievementsAdapter
import ru.get.better.util.ext.observeOnView
import ru.get.better.vm.*
import kotlin.math.pow


class AchievementsFragment : BaseFragment<UserAchievementsViewModel, FragmentAchievementsBinding>(
    ru.get.better.R.layout.fragment_achievements,
    UserAchievementsViewModel::class,
    Handler::class
) {

    private val userInterestsViewModel: UserInterestsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            UserInterestsViewModel::class.java
        )
    }

    private var achievements = listOf<Achievement>()
    private val achievementsAdapter = AchievementsAdapter(achievements)

    private var experience = 0

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(ChangeProgressStateEvent(true))


        view!!.post { setupViewPager() }
    }

    override fun onViewModelReady(viewModel: UserAchievementsViewModel) {
        viewModel.userInterestsViewModel = userInterestsViewModel

        observeOnView(viewModel.achievementsLiveData, ::observeAchievements)
        observeOnView(viewModel.lvlLiveData, ::observeLvl)
        observeOnView(viewModel.lvlPercentLiveData, ::observeLvlPercent)
        observeOnView(viewModel.efficiencyLiveData, ::observeEfficiency)

        view!!.post {
            lifecycleScope.launch(Dispatchers.IO) { viewModel.getAchievements() }
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return 2
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                return when (position) {
                    0 -> {
                        val view = layoutInflater.inflate(R.layout.item_task, null)
                        container.addView(view)
                        view
                    }
                    else -> {
                        val view = layoutInflater.inflate(R.layout.item_achievement, null)

                        view.recycler.adapter = achievementsAdapter
                        view.recycler.addItemDecoration(
                            TimelineDecorator(
                                indicatorSize = 24f,
                                lineWidth = 12f,
                                padding = 24f,
                                position = TimelineDecorator.Position.Left,
                                indicatorColor = resources.getColor(R.color.colorTgPrimary),
                                lineColor = resources.getColor(R.color.colorTgPrimary),
                                lineStyle = TimelineView.LineStyle.Normal,
                                indicatorStyle = TimelineView.IndicatorStyle.Checked
                            )
                        )
                        container.addView(view)
                         view
                    }
                }
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }
        }

        binding.ntsCenter.setViewPager(binding.viewPager)
        binding.ntsCenter.post {
            binding.ntsCenter.setTabIndex(2, true)
        }
    }

    private fun observeAchievements(list: List<Achievement>) {
        achievementsAdapter.items = list
        achievementsAdapter.notifyDataSetChanged()

        list.filter { it.isCompleted }.forEach { experience += it.experience }

        EventBus.getDefault().post(ChangeProgressStateEvent(false))
//        initExpChart()
    }

    private fun observeEfficiency(efficiency: Int) {
        binding.efficiencyChart.setProgress(efficiency.toFloat(), true)
    }

    private fun observeLvl(lvl: Int) {
        binding.expChart.setTextFormatter { lvl.toString() }
    }

    private fun observeLvlPercent(lvlPercent: Float) {
        binding.expChart.setProgress(lvlPercent * 100f, true)
    }

    inner class Handler


}