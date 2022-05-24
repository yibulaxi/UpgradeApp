package ru.get.better.ui.achievements

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lriccardo.timelineview.TimelineDecorator
import com.lriccardo.timelineview.TimelineView
import kotlinx.android.synthetic.main.item_achievement.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentAchievementsBinding
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.model.Achievement
import ru.get.better.ui.achievements.adapter.AchievementsAdapter
import ru.get.better.ui.base.BaseFragment
import ru.get.better.vm.UserAchievementsViewModel


class AchievementsFragment : BaseFragment<FragmentAchievementsBinding>(
    ru.get.better.R.layout.fragment_achievements,
    Handler::class
) {

    private val userAchievementsViewModel: UserAchievementsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            UserAchievementsViewModel::class.java
        )
    }

    private var achievements = listOf<Achievement>()
    private val achievementsAdapter = AchievementsAdapter(achievements)

    private var experience = 0

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(ChangeProgressStateEvent(true))

        lifecycleScope.launch(Dispatchers.IO) {
            Thread.sleep(500)
        }.invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                userAchievementsViewModel.getAchievements()
            }
        }
    }

    override fun onViewModelReady() {
        userAchievementsViewModel.achievementsLiveData.observe(this) { observeAchievements(it) }
        userAchievementsViewModel.lvlLiveData.observe(this) { observeLvl(it) }
        userAchievementsViewModel.lvlPercentLiveData.observe(this) { observeLvlPercent(it) }
        userAchievementsViewModel.efficiencyLiveData.observe(this) { observeEfficiency(it) }
    }

    override fun updateThemeAndLocale() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.lvlTitle.text =
                App.resourcesProvider.getStringLocale(R.string.level, App.preferences.locale)
            binding.efficiencyTitle.text =
                App.resourcesProvider.getStringLocale(R.string.efficiency, App.preferences.locale)

            achievementsAdapter.notifyDataSetChanged()
            setupViewPager()

            binding.achievementsContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricBackground
                    else R.color.colorLightFragmentMetricBackground
                )
            )

            binding.expChart.backgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsExpChartBackground
                    else R.color.colorLightFragmentAchievementsExpChartBackground
                )
            )

            binding.lvlTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsExpChartTitleText
                    else R.color.colorLightFragmentAchievementsExpChartTitleText
                )
            )

            binding.efficiencyChart.backgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsEfficiencyChartBackground
                    else R.color.colorLightFragmentAchievementsEfficiencyChartBackground
                )
            )
            binding.efficiencyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsEfficiencyChartTitleText
                    else R.color.colorLightFragmentAchievementsEfficiencyChartTitleText
                )
            )

            binding.viewPagerFrame.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsViewPagerBackground
                    else R.color.colorLightFragmentAchievementsViewPagerBackground
                )
            )

            binding.viewPagerView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsViewSeparatorBackground
                    else R.color.colorLightFragmentAchievementsViewSeparatorBackground
                )
            )

            binding.ntsCenter.activeColor = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsNtsCenterActive
                else R.color.colorLightFragmentAchievementsNtsCenterActive
            )

            binding.ntsCenter.inactiveColor = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsNtsCenterInactive
                else R.color.colorLightFragmentAchievementsNtsCenterInactive
            )

            binding.ntsCenter.stripColor = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAchievementsNtsCenter
                else R.color.colorLightFragmentAchievementsNtsCenter
            )
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

                        view.taskTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.next_update)
                        view.taskTitle.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (App.preferences.isDarkTheme) R.color.colorDarkItemTaskText
                                else R.color.colorLightItemTaskText
                            )
                        )

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
                                indicatorColor = resources.getColor(
                                    if (App.preferences.isDarkTheme) R.color.colorDarkAchievementsIndicator
                                    else R.color.colorLightAchievementsIndicator
                                ),
                                lineColor = resources.getColor(
                                    if (App.preferences.isDarkTheme) R.color.colorDarkAchievementsLine
                                    else R.color.colorLightAchievementsLine
                                ),
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

        binding.viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) App.analyticsEventsManager.achievementsAchievementsTapped()
                else App.analyticsEventsManager.achievementsTasksTapped()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
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