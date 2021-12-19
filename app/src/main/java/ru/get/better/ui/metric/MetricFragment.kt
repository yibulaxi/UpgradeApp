package ru.get.better.ui.metric

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
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
import com.github.mikephil.charting.listener.PieRadarChartTouchListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.skydoves.balloon.*
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.Circle
import kotlinx.android.synthetic.main.dialog_alert_edit_interest.view.*
import kotlinx.android.synthetic.main.radar_markerview.view.*
import kotlinx.android.synthetic.main.snackbar_success.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentMetricBinding
import ru.get.better.event.*
import ru.get.better.model.AllLogo
import ru.get.better.model.EmptyInterest
import ru.get.better.model.Interest
import ru.get.better.model.UserCustomInterest
import ru.get.better.navigation.Navigator
import ru.get.better.rest.UserSettingsFields
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.metric.adapter.MetricListAdapter
import ru.get.better.ui.view.CustomWheelPickerView
import ru.get.better.util.ext.getBalloon
import ru.get.better.vm.BaseViewModel
import ru.get.better.vm.UserDiaryViewModel
import ru.get.better.vm.UserInterestsViewModel
import ru.get.better.vm.UserSettingsViewModel
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import java.util.*
import kotlin.collections.ArrayList


class MetricFragment : BaseFragment<BaseViewModel, FragmentMetricBinding>(
    R.layout.fragment_metric,
    BaseViewModel::class,
    Handler::class
) {

    private val userInterestsViewModel: UserInterestsViewModel by lazy {
        ViewModelProviders.of(
            requireActivity()
        ).get(UserInterestsViewModel::class.java)
    }

    private val userDiaryViewModel: UserDiaryViewModel by lazy {
        ViewModelProviders.of(
            requireActivity()
        ).get(UserDiaryViewModel::class.java)
    }

    private val userSettingsViewModel: UserSettingsViewModel by lazy {
        ViewModelProviders.of(
            requireActivity()
        ).get(UserSettingsViewModel::class.java)
    }

    private lateinit var adapter: MetricListAdapter

    private val interestDetailBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.interestDetailBottomSheet.bottomSheetContainer)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        EventBus.getDefault().post(ChangeProgressStateEvent(true))
        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))

        setupChart()
        setupList()
        setupMetricControlGroup()

        interestDetailBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.backgroundImage.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                }
            }
        })

    }

    @Subscribe
    fun onShowSpotlightEvent(e: ShowSpotlightEvent) {
        if (e.spotlightType == SpotlightType.MetricWheel)
            showWheelSpotlight()
    }

    private fun showWheelSpotlight() {
        val wheelTargetLayout =
            layoutInflater.inflate(R.layout.target_metric_wheel, FrameLayout(requireContext()))
        val wheelTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.wheelTarget)
            .setShape(Circle(400f))
            .setEffect(
                RippleEffect(
                    100f,
                    200f,
                    ContextCompat.getColor(requireContext(), R.color.colorTgPrimary)
                )
            )
            .setOverlay(wheelTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(wheelTarget)
            .setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorTgPrimaryDark
                )
            )
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setContainer(binding.container)
            .build()
        spotlight.start()
        EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(true))

        wheelTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()

            App.preferences.isMetricWheelSpotlightShown = true
            EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(false))
            userSettingsViewModel.updateField(UserSettingsFields.IsMetricWheelSpotlightShown, true)
        }
    }

    @Subscribe
    fun onBackPressedEvent(e: BackPressedEvent) {
        if (interestDetailBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            interestDetailBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @Subscribe
    fun onShowDetailInterest(e: ShowDetailInterest) {
        setupDetailInterestBottomSheet(e.interest)
        interestDetailBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @Subscribe
    fun onShowAddInterestDialogEvent(e: ShowAddInterestDialogEvent) {
        showAddInterestDialog()
    }

    private fun setupDetailInterestBottomSheet(interest: Interest) {
        with(binding.interestDetailBottomSheet) {
            userDiaryViewModel.getNotes().observe(this@MetricFragment) { notes ->
                title.text = interest.name ?: getString(interest.nameRes!!)
                amount.text =
                    String.format("%.2f", interest.currentValue)
                        .replace(".", ",")

                amountMax.isVisible = interest.currentValue == 10f

                notesAmount.isVisible =
                    notes.any { it.interest!!.interestId == interest.id }
                notesAmount.text =
                    "Написано постов - " +
                            notes.filter { it.interest!!.interestId == interest.id }.size

                startValue.text =
                    "Начальное значение - " + String.format("%.2f", interest.startValue)
                        .replace(".", ",")
                currentValue.text =
                    "Текущее значение - " + String.format("%.2f", interest.currentValue)
                        .replace(".", ",")

                edit.setOnClickListener {
                    interestDetailBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    showEditInterestDialog(interest)
                }
            }
        }
    }

    private fun showAddInterestDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_alert_add_interest, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
        alertDialogBuilder.setPositiveButton("Создать", null)
        alertDialogBuilder.setNegativeButton("Отменить", null)

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.setTitle("Создание сферы")

        alertDialog.setCancelable(false)

        val iconValues = mutableListOf<CustomWheelPickerView.Item>()
        AllLogo().logoList.forEach {
            iconValues.add(
                CustomWheelPickerView.Item(
                    id = it.id,
                    icon = ContextCompat.getDrawable(
                        requireContext(),
                        AllLogo().getLogoById(it.id)
                    )
                )
            )
        }
        view.icon.getRecycler().setItemViewCacheSize(AllLogo().logoList.size)
        view.icon.adapter.values = iconValues

        view.icon.adapter.notifyDataSetChanged()

        view.icon.isHapticFeedbackEnabled = true

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                when {
                    view.interestName.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent("Введите название"))
                    view.interestDescription.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent("Введите описание"))
                    else -> {
                        val interest = UserCustomInterest(
                            id = System.currentTimeMillis().toString() + UUID.randomUUID()
                                .toString(),
                            name = view.interestName.text.toString(),
                            description = view.interestDescription.text.toString(),
                            startValue = 5f,
                            currentValue = 5f,
                            logoId = iconValues[view.icon.selectedIndex].id,
                            dateLastUpdate = System.currentTimeMillis().toString()
                        )

                        userInterestsViewModel.addInterest(interest)

                        adapter.addInterest(interest)

                        alertDialog.dismiss()
                    }
                }
            }

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun showEditInterestDialog(interest: Interest) {
        val view: View = layoutInflater.inflate(R.layout.dialog_alert_edit_interest, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
        alertDialogBuilder.setPositiveButton("Сохранить", null)
        alertDialogBuilder.setNegativeButton("Удалить", null)

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.setTitle("Редактирование сферы")
        alertDialog.setCancelable(true)

        view.interestName.setText(interest.name ?: getString(interest.nameRes!!))
        view.interestDescription.setText(
            interest.description ?: getString(interest.descriptionRes!!)
        )

        val iconValues = arrayListOf(
            CustomWheelPickerView.Item(
                id = interest.logoId!!,
                icon = ContextCompat.getDrawable(
                    requireContext(),
                    AllLogo().getLogoById(interest.logoId!!)
                )
            )
        )
        AllLogo().logoList.filter { it.id != interest.logoId }.forEach {
            iconValues.add(
                CustomWheelPickerView.Item(
                    id = it.id,
                    icon = ContextCompat.getDrawable(
                        requireContext(),
                        AllLogo().getLogoById(it.id)
                    )
                )
            )
        }

        view.icon.getRecycler().setItemViewCacheSize(AllLogo().logoList.size)
        view.icon.adapter.values = iconValues

        view.icon.adapter.notifyDataSetChanged()

        view.icon.isHapticFeedbackEnabled = true

        view.icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
            override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                interest.logoId = iconValues[index].id
            }
        })

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                when {
                    view.interestName.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent("Введите название"))
                    view.interestDescription.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent("Введите описание"))
                    else -> {
                        interest.name = view.interestName.text.toString()
                        interest.description = view.interestDescription.text.toString()

                        userInterestsViewModel.updateInterest(interest) {
                            EventBus.getDefault().post(ShowSuccessEvent("Сохранено!"))
                        }

                        adapter.updateInterestById(interest)

                        alertDialog.dismiss()
                    }
                }
            }

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                userInterestsViewModel.deleteInterest(interest) {
                    EventBus.getDefault().post(ShowSuccessEvent("Удалено!"))
                }

                adapter.deleteInterestById(interest.id)

                alertDialog.dismiss()
            }
        }

        alertDialog.setView(view)
        alertDialog.show()
    }

    private var needUpdateScreen: Boolean = false

    @Subscribe
    fun onUpdateMetricsEvent(e: UpdateMetricsEvent) {
        setAverageAmount()

        needUpdateScreen = true
        updateMetric()
    }

    private fun updateMetric() {
        if (binding.list.alpha == 0f && needUpdateScreen)
            Navigator.refresh(this@MetricFragment)
    }

    private fun setAverageAmount() {
        binding.averageAmount.text =
            if (userInterestsViewModel.getInterests().size != 0)
                String.format("%.1f", (userInterestsViewModel.calculateCurrentValueAverage()))
                    .replace(".", ",")
            else "0"
    }

    private fun setupList() {
        val list = userInterestsViewModel.getInterests().toMutableList()
        list.add(EmptyInterest())
        adapter = MetricListAdapter(context!!, list)
        binding.recycler.adapter = adapter

        setAverageAmount()

        userDiaryViewModel.getNotes().observe(this@MetricFragment) { notes ->
            binding.diaryAmount.text = notes.size.toString()
        }


        userSettingsViewModel.getUserSettingsById(
            App.preferences.uid!!
        ).observe(this@MetricFragment) {
            binding.daysAmount.text =
                ((System.currentTimeMillis() - (it?.dateRegistration
                    ?: "0").toLong()) / 1000 / 60 / 60 / 24).toInt()
                    .toString()
        }

        binding.list.animate()
            .translationY(binding.list.height.toFloat())
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })

        binding.list.post {
            EventBus.getDefault().post(ChangeProgressStateEvent(false))
        }
    }

    private fun setupMetricControlGroup() {
        with(binding) {
            wheelState.textSize = 12f
            listState.textSize = 12f

            metricStateControlGroup.setOnSelectedOptionChangeCallback {
                wheelState.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 0) R.color.colorTgWhite else R.color.colorTgText
                    )
                )

                listState.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 1) R.color.colorTgWhite else R.color.colorTgText
                    )
                )

                changeMetricListVisibility(it == 1)
            }
        }
    }

    private fun changeMetricListVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.list.visibility = View.VISIBLE
            binding.list.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                    }
                })
        } else {
            binding.list.animate()
                .translationY(binding.list.height.toFloat())
                .alpha(0.0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.list.visibility = View.GONE
                        updateMetric()
                    }
                })
        }
    }

    private fun setupRadarControlGroup() {
        binding.currentState.textSize = 12f
        binding.startState.textSize = 12f

        binding.radarStateControlGroup.setOnSelectedOptionChangeCallback {
            binding.currentState.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 0) R.color.colorTgWhite else R.color.colorTgText
                )
            )

            binding.startState.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 1) R.color.colorTgWhite else R.color.colorTgText
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
        binding.radarChart.webColor = ContextCompat.getColor(requireContext(), R.color.colorTgText)
        binding.radarChart.webLineWidthInner = 0.5f
        binding.radarChart.webColorInner =
            ContextCompat.getColor(requireContext(), R.color.colorTgText)
        binding.radarChart.webAlpha = 100

        setChartData()

        binding.radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        setChartAxis()
        setupRadarControlGroup()

        binding.radarChart.onTouchListener =
            object : PieRadarChartTouchListener(binding.radarChart) {
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {

                    binding.metricStateControlGroup.setSelectedIndex(1, true)

                    binding.wheelState.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorTgText
                        )
                    )

                    binding.listState.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorTgWhite
                        )
                    )


                    changeMetricListVisibility(true)
                    return super.onSingleTapConfirmed(e)
                }
            }

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

        val interests = userInterestsViewModel.getInterests()

        for (i in 0 until interests.size) {
            val val0 = 12f
            val radarEntryIcon = RadarEntry(val0)

            val bMap = BitmapFactory.decodeResource(resources, interests[i].getLogo())
            val bMapScaled = Bitmap.createScaledBitmap(bMap, 60, 60, true)
//
            radarEntryIcon.icon = BitmapDrawable(resources, bMapScaled)
            icons.add(radarEntryIcon)

            val val1 = interests[i].currentValue
            entries1.add(RadarEntry(val1!!))

            val val2 = interests[i].startValue
            entries2.add(RadarEntry(val2!!))
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
        rdsCurrent.color = ContextCompat.getColor(context!!, R.color.colorBlue)
        rdsCurrent.fillColor = ContextCompat.getColor(context!!, R.color.colorBlueLight)
        rdsCurrent.setDrawFilled(true)
        rdsCurrent.fillAlpha = 180
        rdsCurrent.lineWidth = 1f
        rdsCurrent.valueTextColor = ContextCompat.getColor(context!!, R.color.colorTgWhite)
        rdsCurrent.isDrawHighlightCircleEnabled = false
        rdsCurrent.setDrawHighlightIndicators(false)

        val rdsDefault = RadarDataSet(entries2, "Default")
        rdsDefault.color = ContextCompat.getColor(context!!, R.color.colorPurpleDark)
        rdsDefault.fillColor = ContextCompat.getColor(context!!, R.color.colorPurple)
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
        data.setValueTextSize(10f)
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