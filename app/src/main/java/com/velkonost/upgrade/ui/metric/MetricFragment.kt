package com.velkonost.upgrade.ui.metric

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.skydoves.balloon.*
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentMetricBinding
import com.velkonost.upgrade.event.ChangeNavViewVisibilityEvent
import com.velkonost.upgrade.event.ShowDetailInterest
import com.velkonost.upgrade.event.UpdateMetricsEvent
import com.velkonost.upgrade.model.Interest
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.metric.adapter.MetricListAdapter
import com.velkonost.upgrade.util.ext.getBalloon
import kotlinx.android.synthetic.main.radar_markerview.view.*
import kotlinx.android.synthetic.main.snackbar_success.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MetricFragment : BaseFragment<HomeViewModel, FragmentMetricBinding>(
    R.layout.fragment_metric,
    HomeViewModel::class,
    Handler::class
) {

    private lateinit var adapter: MetricListAdapter

    private val interestDetailBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.interestDetailBottomSheet.bottomSheetContainer)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        binding.viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)

        setupChart()
        setupList()
        setupMetricControlGroup()

        interestDetailBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.backgroundImage.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
                }
            }
        })

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
    }

    @Subscribe
    fun onShowDetailInterest(e: ShowDetailInterest) {
        setupDetailInterestBottomSheet(e.interest)
        interestDetailBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupDetailInterestBottomSheet(interest: Interest) {
        with(binding.interestDetailBottomSheet) {
            title.text = getString(interest.nameRes)
            amount.text = interest.selectedValue.toString()

            amountMax.isVisible = interest.selectedValue == 10f

            notesAmount.isVisible =
                binding.viewModel!!.getNotesByInterestId(interest.id.toString()).size != 0
            notesAmount.text =
                "Написано постов - " + binding.viewModel!!.getNotesByInterestId(interest.id.toString()).size

            startValue.text =
                "Начальное значение - " + binding.viewModel!!.getStartInterestByInterestId(interest.id.toString())?.selectedValue
            currentValue.text = "Текущее значение - " + interest.selectedValue
        }
    }

    @Subscribe
    fun onUpdateMetricsEvent(e: UpdateMetricsEvent) {
        Navigator.refresh(this@MetricFragment)
    }

    private fun setupList() {
        adapter = MetricListAdapter(context!!, binding.viewModel!!.getCurrentInterests())
        binding.recycler.adapter = adapter

        var average = 0f

        for (interest in binding.viewModel!!.getCurrentInterests()) {
            average += interest.selectedValue
        }

        binding.averageAmount.text = String.format("%.1f", (average / 8))
            .replace(".", ",")

        binding.diaryAmount.text = binding.viewModel!!.diary.notes.size.toString()
        binding.daysAmount.text =
            ((System.currentTimeMillis() - binding.viewModel!!.userSettings.reg_time!!) / 1000 / 60 / 60 / 24).toInt()
                .toString()

        binding.list.animate()
            .translationY(binding.list.height.toFloat())
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })
    }

    private fun setupMetricControlGroup() {
        with(binding) {
            wheelState.textSize = 12f
            listState.textSize = 12f

            metricStateControlGroup.setOnSelectedOptionChangeCallback {
                wheelState.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 0) R.color.colorWhite else R.color.colorText
                    )
                )

                listState.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 1) R.color.colorWhite else R.color.colorText
                    )
                )

                if (it == 1) {
                    list.visibility = View.VISIBLE
                    list.animate()
                        .translationY(0f)
                        .alpha(1.0f)
                        .setDuration(500)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)

                            }
                        })
                } else {
                    list.animate()
                        .translationY(binding.list.height.toFloat())
                        .alpha(0.0f)
                        .setDuration(500)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                binding.list.visibility = View.GONE
                            }
                        })
                }
            }
        }
    }

    private fun setupRadarControlGroup() {
        binding.currentState.textSize = 12f
        binding.startState.textSize = 12f

        binding.radarStateControlGroup.setOnSelectedOptionChangeCallback {
            binding.currentState.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 0) R.color.colorWhite else R.color.colorText
                )
            )

            binding.startState.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 1) R.color.colorWhite else R.color.colorText
                )
            )

            binding.radarChart.data.dataSets[2].isVisible = it == 1
            binding.radarChart.invalidate()
        }
    }

    private fun setupChart() {
        binding.radarChart.description.isEnabled = false

        binding.radarChart.setExtraOffsets(50f, 50f, 50f, 50f)

        binding.radarChart.webLineWidth = 0.5f
        binding.radarChart.webColor = Color.LTGRAY
        binding.radarChart.webLineWidthInner = 0.5f
        binding.radarChart.webColorInner = Color.LTGRAY
        binding.radarChart.webAlpha = 100

        setChartData()

        binding.radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        setChartAxis()
        setupRadarControlGroup()
    }

    private fun setChartAxis() {
        val xAxis: XAxis = binding.radarChart.xAxis

        xAxis.textSize = 9f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.isEnabled = false
        xAxis.labelCount = 3

        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return ""
            }
        }

        xAxis.setDrawLabels(false)
        xAxis.textColor = Color.WHITE

        val yAxis: YAxis = binding.radarChart.yAxis
        yAxis.setLabelCount(10, true)

        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 10f
        yAxis.setDrawLabels(false)

        val l: Legend = binding.radarChart.legend
        l.isEnabled = false
    }

    private fun setChartData() {

        val icons: ArrayList<RadarEntry> = ArrayList()
        val entries1: ArrayList<RadarEntry> = ArrayList()
        val entries2: ArrayList<RadarEntry> = ArrayList()

        val currentInterests = binding.viewModel!!.getCurrentInterests()
        val startInterests = binding.viewModel!!.getStartInterests()

        for (i in 0 until currentInterests.size) {
            val val0 = 12f
            val radarEntryIcon = RadarEntry(val0)

            val bMap = BitmapFactory.decodeResource(resources, currentInterests[i].logo)
            val bMapScaled = Bitmap.createScaledBitmap(bMap, 60, 60, true)

            radarEntryIcon.icon = BitmapDrawable(resources, bMapScaled)
            icons.add(radarEntryIcon)

            val val1 = currentInterests[i].selectedValue
            entries1.add(RadarEntry(val1))

            val val2 = startInterests[i].selectedValue
            entries2.add(RadarEntry(val2))
        }

        val set0 = RadarDataSet(icons, "")

        set0.color = Color.TRANSPARENT
        set0.fillColor = Color.TRANSPARENT
        set0.setDrawFilled(false)
        set0.lineWidth = 0f
        set0.fillAlpha = 255
        set0.valueTextColor = Color.TRANSPARENT
        set0.isDrawHighlightCircleEnabled = false
        set0.setDrawHighlightIndicators(false)

        val rdsCurrent = RadarDataSet(entries1, "Current")
        rdsCurrent.color = ContextCompat.getColor(context!!, R.color.colorPurpleDark)
        rdsCurrent.fillColor = ContextCompat.getColor(context!!, R.color.colorPurple)
        rdsCurrent.setDrawFilled(true)
        rdsCurrent.fillAlpha = 180
        rdsCurrent.lineWidth = 1f
        rdsCurrent.valueTextColor = ContextCompat.getColor(context!!, R.color.colorText)
        rdsCurrent.isDrawHighlightCircleEnabled = false
        rdsCurrent.setDrawHighlightIndicators(false)

        val rdsDefault = RadarDataSet(entries2, "Default")
        rdsDefault.color = ContextCompat.getColor(context!!, R.color.colorBlue)
        rdsDefault.fillColor = ContextCompat.getColor(context!!, R.color.colorBlueLight)
        rdsDefault.setDrawFilled(true)
        rdsDefault.fillAlpha = 180
        rdsDefault.lineWidth = 1f
        rdsDefault.valueTextColor = Color.TRANSPARENT
        rdsDefault.isDrawHighlightCircleEnabled = false
        rdsDefault.setDrawHighlightIndicators(false)
        rdsDefault.isVisible = false
        val sets: ArrayList<IRadarDataSet> = ArrayList()
        sets.add(set0)
        sets.add(rdsCurrent)
        sets.add(rdsDefault)
        val data = RadarData(sets)
        data.setValueTextSize(8f)
        data.setDrawValues(true)

        binding.radarChart.data = data
        binding.radarChart.invalidate()
    }

    inner class Handler {
        fun onInfoClicked(v: View) {
            getBalloon(getString(R.string.metric_info)).showAlignBottom(binding.info)
        }
    }
}