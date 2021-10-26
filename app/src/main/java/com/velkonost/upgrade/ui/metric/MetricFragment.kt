package com.velkonost.upgrade.ui.metric

import android.R.attr
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.databinding.FragmentMetricBinding
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.splash.SplashViewModel
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
import com.velkonost.upgrade.R
import com.velkonost.upgrade.ui.view.RadarMarkerView
import kotlinx.android.synthetic.main.radar_markerview.view.*
import kotlinx.android.synthetic.main.snackbar_success.view.*
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable





class MetricFragment : BaseFragment<SplashViewModel, FragmentMetricBinding>(
    com.velkonost.upgrade.R.layout.fragment_metric,
    SplashViewModel::class,
    Handler::class
) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), com.velkonost.upgrade.R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())

        binding.radarChart.description.isEnabled = false

        binding.radarChart.setExtraOffsets(50f, 50f, 50f, 50f)


        binding.radarChart.webLineWidth = 0.5f
        binding.radarChart.webColor = Color.LTGRAY
        binding.radarChart.webLineWidthInner = 0.5f
        binding.radarChart.webColorInner = Color.LTGRAY
        binding.radarChart.webAlpha = 100

        setData()

        binding.radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        val xAxis: XAxis = binding.radarChart.xAxis

//        xAxis.typeface = tfLight
        xAxis.textSize = 9f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.isEnabled = true
        xAxis.labelCount = 3


        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getFormattedValue(value: Float): String {
                return ""
            }


        }

        xAxis.setDrawLabels(true)
        xAxis.textColor = Color.WHITE

        val yAxis: YAxis = binding.radarChart.yAxis
//        yAxis.typeface = tfLight
        yAxis.setLabelCount(10, true)

        yAxis.textSize = 9f
        yAxis.yOffset = -10f
        yAxis.xOffset = -10f
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 80f
        yAxis.setDrawLabels(false)

        val l: Legend = binding.radarChart.legend
        l.isEnabled = false
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
//        l.setTypeface(tfLight)
        l.xEntrySpace = 7f
        l.yEntrySpace = 5f
        l.textColor = Color.WHITE


    }

    private fun setData() {
        val mul = 80f
        val min = 20f
        val cnt = 8
        val icons: ArrayList<RadarEntry> = ArrayList()
        val entries1: ArrayList<RadarEntry> = ArrayList()
        val entries2: ArrayList<RadarEntry> = ArrayList()

        val val0 = mul + 10
        val en = RadarEntry(val0)
        val dr = context!!.resources.getDrawable(R.drawable.logo)
        val bitmap = (dr as BitmapDrawable).bitmap
// Scale it to 50 x 50
// Scale it to 50 x 50
        val d: Drawable =
            BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 50, 50, true))


        en.icon = d
        icons.add(en)
        icons.add(en)
        icons.add(en)
        icons.add(en)
        icons.add(en)
        icons.add(en)
        icons.add(en)
        icons.add(en)
        val set0 = RadarDataSet(icons, "")

        set0.color = Color.TRANSPARENT
        set0.fillColor = Color.TRANSPARENT
        set0.setDrawFilled(false)
        set0.lineWidth = 0f
        set0.fillAlpha = 255
        set0.isDrawHighlightCircleEnabled = false
        set0.setDrawHighlightIndicators(false)

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until cnt) {
            val val1 = (Math.random() * mul).toFloat() + min

            entries1.add(RadarEntry(val1))
            val val2 = (Math.random() * mul).toFloat() + min
            entries2.add(RadarEntry(val2))
        }
        val set1 = RadarDataSet(entries1, "Last Week")
        set1.color = ContextCompat.getColor(context!!, R.color.colorPurpleDark)
        set1.fillColor = ContextCompat.getColor(context!!, R.color.colorPurple)
        set1.setDrawFilled(true)
        set1.fillAlpha = 180
        set1.lineWidth = 1f
        set1.isDrawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(true)

        val set2 = RadarDataSet(entries2, "This Week")
        set2.color = ContextCompat.getColor(context!!, R.color.colorBlue)
        set2.fillColor = ContextCompat.getColor(context!!, R.color.colorBlueLight)
        set2.setDrawFilled(true)
        set2.fillAlpha = 180
        set2.lineWidth = 1f
        set2.isDrawHighlightCircleEnabled = true
        set2.setDrawHighlightIndicators(true)
        val sets: ArrayList<IRadarDataSet> = ArrayList()
        sets.add(set0)
        sets.add(set1)
        sets.add(set2)
        val data = RadarData(sets)
//        data.setValueTypeface(tfLight)
        data.setValueTextSize(8f)
        data.setDrawValues(true)
        data.setValueTextColor(Color.WHITE)
        binding.radarChart.data = data
        binding.radarChart.invalidate()
    }

    inner class Handler
}