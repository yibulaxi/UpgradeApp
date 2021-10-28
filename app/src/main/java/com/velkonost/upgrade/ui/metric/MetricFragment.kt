package com.velkonost.upgrade.ui.metric

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.databinding.FragmentMetricBinding
import com.velkonost.upgrade.ui.base.BaseFragment
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.animation.Easing
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.components.*

import com.github.mikephil.charting.data.RadarData

import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet

import com.github.mikephil.charting.data.RadarDataSet

import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

import com.velkonost.upgrade.ui.view.RadarMarkerView
import kotlinx.android.synthetic.main.radar_markerview.view.*
import kotlinx.android.synthetic.main.snackbar_success.view.*
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.velkonost.upgrade.ui.HomeViewModel

import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import com.velkonost.upgrade.R
import com.velkonost.upgrade.event.UpdateMetricsEvent
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.metric.adapter.MetricListAdapter
import org.greenrobot.eventbus.Subscribe

import com.skydoves.balloon.*
import com.skydoves.balloon.BalloonSizeSpec.WRAP

import com.velkonost.upgrade.di.AppModule_ContextFactory.context


class MetricFragment : BaseFragment<HomeViewModel, FragmentMetricBinding>(
    com.velkonost.upgrade.R.layout.fragment_metric,
    HomeViewModel::class,
    Handler::class
) {

    private lateinit var adapter: MetricListAdapter

    val balloon: Balloon by lazy {
        Balloon.Builder(context!!)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setTextGravity(Gravity.START)
            .setPadding(10)
            .setWidth(WRAP)
            .setHeight(WRAP)
            .setTextSize(15f)
            .setCornerRadius(4f)
            .setAlpha(0.9f)
            .setText(getString(R.string.metric_info))
            .setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
            .setTextIsHtml(true)
            .setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorBlueLight))
            .setBalloonAnimation(BalloonAnimation.FADE)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), com.velkonost.upgrade.R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())

        binding.viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)

        setupChart()
        setupList()
        setupMetricControlGroup()
    }

    @Subscribe
    fun onUpdateMetricsEvent(e: UpdateMetricsEvent) {
//        if (isAdded) {
            Navigator.refresh(this@MetricFragment)
//        }
    }

    private fun setupList() {
        adapter = MetricListAdapter(context!!, binding.viewModel!!.getCurrentInterests())
        binding.recycler.adapter = adapter

        var average = 0f

        for (interest in binding.viewModel!!.getCurrentInterests()) {
            average += interest.selectedValue
        }

        binding.averageAmount.text = (average / binding.viewModel!!.getCurrentInterests().size)
            .toString().replace(".", ",")

        binding.diaryAmount.text = binding.viewModel!!.getDiary().notes.size.toString()
    }

    private fun setupMetricControlGroup() {
        binding.wheelState.textSize = 12f
        binding.listState.textSize = 12f

        binding.metricStateControlGroup.setOnSelectedOptionChangeCallback {
            binding.wheelState.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 0) R.color.colorWhite else R.color.colorText
                )
            )
            binding.listState.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 1) R.color.colorWhite else R.color.colorText
                )
            )

            binding.list.isVisible = it == 1

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
            override fun getFormattedValue(value: Float): String { return "" }
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
            balloon.showAlignBottom(binding.info)
        }
    }
}