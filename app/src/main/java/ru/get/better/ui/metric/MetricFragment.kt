package ru.get.better.ui.metric

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.res.ColorStateList
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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.android.synthetic.main.dialog_alert_add_interest.view.*
import kotlinx.android.synthetic.main.dialog_alert_edit_interest.view.*
import kotlinx.android.synthetic.main.dialog_alert_edit_interest.view.icon
import kotlinx.android.synthetic.main.dialog_alert_edit_interest.view.interestDescription
import kotlinx.android.synthetic.main.dialog_alert_edit_interest.view.interestName
import kotlinx.android.synthetic.main.snackbar_success.view.*
import kotlinx.android.synthetic.main.target_metric_wheel.view.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
import ru.get.better.ui.activity.main.ext.SecondaryViews
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.metric.adapter.MetricListAdapter
import ru.get.better.ui.view.CustomWheelPickerView
import ru.get.better.util.ext.getBalloon
import ru.get.better.vm.*
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import java.util.*
import kotlin.collections.ArrayList
import ru.get.better.util.InputFilterMinMax

import android.text.InputFilter

class MetricFragment : BaseFragment<FragmentMetricBinding>(
    R.layout.fragment_metric,
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


        lifecycleScope.launch(Dispatchers.IO) {
            Thread.sleep(500)
        }.invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                if (isAdded) {
                    setupChart()
                    setupLogic()

                    android.os.Handler().postDelayed({
                        EventBus.getDefault().post(ShowAffirmationEvent(increase = true))
                    }, 500)
                }
            }
        }
    }

    private fun setupLogic() {
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
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                }
            }
        })

//        binding.backgroundImage.setOnClickListener {
//            if (interestDetailBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
//                interestDetailBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        }

        EventBus.getDefault().post(ShowRateDialogEvent())
    }

    override fun updateThemeAndLocale() {
        binding.wheelStateLight.text = App.resourcesProvider.getStringLocale(R.string.wheel)
        binding.wheelStateDark.text = App.resourcesProvider.getStringLocale(R.string.wheel)

        binding.listStateLight.text = App.resourcesProvider.getStringLocale(R.string.list)
        binding.listStateDark.text = App.resourcesProvider.getStringLocale(R.string.list)

        binding.currentStateLight.text =
            App.resourcesProvider.getStringLocale(R.string.current_wheel)
        binding.currentStateDark.text =
            App.resourcesProvider.getStringLocale(R.string.current_wheel)

        binding.startStateLight.text = App.resourcesProvider.getStringLocale(R.string.start_wheel)
        binding.startStateDark.text = App.resourcesProvider.getStringLocale(R.string.start_wheel)

        binding.diaryAmountTitle.text = App.resourcesProvider.getStringLocale(R.string.metric_notes)
        binding.averageAmountTitle.text =
            App.resourcesProvider.getStringLocale(R.string.metric_average_point)
        binding.daysAmountTitle.text = App.resourcesProvider.getStringLocale(R.string.metric_days)

        if (::adapter.isInitialized)
            adapter.notifyDataSetChanged()

//        setupChart()
//        setupList()
//        setupMetricControlGroup()

        binding.container.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricBackground
                else R.color.colorLightFragmentMetricBackground
            )
        )

        binding.metricStateControlGroupLight.isVisible = !App.preferences.isDarkTheme
        binding.metricStateControlGroupDark.isVisible = App.preferences.isDarkTheme

        binding.radarStateControlGroupLight.isVisible = !App.preferences.isDarkTheme
        binding.radarStateControlGroupDark.isVisible = App.preferences.isDarkTheme

        binding.wheelStateLight.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricWheelStateText
                else R.color.colorLightFragmentMetricWheelStateText
            )
        )

        binding.wheelStateDark.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricWheelStateText
                else R.color.colorLightFragmentMetricWheelStateText
            )
        )

        binding.listStateLight.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricListStateText
                else R.color.colorLightFragmentMetricListStateText
            )
        )

        binding.listStateDark.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricListStateText
                else R.color.colorLightFragmentMetricListStateText
            )
        )

        binding.currentStateLight.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricCurrentStateText
                else R.color.colorLightFragmentMetricCurrentStateText
            )
        )

        binding.currentStateDark.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricCurrentStateText
                else R.color.colorLightFragmentMetricCurrentStateText
            )
        )

        binding.startStateLight.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricStartStateText
                else R.color.colorLightFragmentMetricStartStateText
            )
        )

        binding.startStateDark.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricStartStateText
                else R.color.colorLightFragmentMetricStartStateText
            )
        )

        binding.list.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricListBackground
                else R.color.colorLightFragmentMetricListBackground
            )
        )

        binding.diaryAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricDiaryAmountText
                else R.color.colorLightFragmentMetricDiaryAmountText
            )
        )

        binding.diaryAmountTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricDiaryAmountTitleText
                else R.color.colorLightFragmentMetricDiaryAmountTitleText
            )
        )

        binding.averageAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricAverageAmountText
                else R.color.colorLightFragmentMetricAverageAmountText
            )
        )

        binding.averageAmountTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricAverageAmountTitleText
                else R.color.colorLightFragmentMetricAverageAmountTitleText
            )
        )

        binding.daysAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricDaysAmountText
                else R.color.colorLightFragmentMetricDaysAmountText
            )
        )

        binding.daysAmountTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricDaysAmountTitleText
                else R.color.colorLightFragmentMetricDaysAmountTitleText
            )
        )

        binding.info.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricInfoTint
                else R.color.colorLightFragmentMetricInfoTint
            )
        )

        binding.backgroundImage.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkBlur
                else R.color.colorLightBlur
            )
        )
    }

    @Subscribe
    fun onShowSpotlightEvent(e: ShowSpotlightEvent) {
        if (e.spotlightType == SpotlightType.MetricWheel) {
            android.os.Handler().postDelayed({
                showWheelSpotlight()
            }, 1500)

        }
    }

    private fun showWheelSpotlight() {
        val wheelTargetLayout =
            layoutInflater.inflate(R.layout.target_metric_wheel, FrameLayout(requireContext()))

        wheelTargetLayout.title.text =
            App.resourcesProvider.getStringLocale(R.string.wheel_spotlight)

        wheelTargetLayout.title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkTargetMetricWheelTitleText
                else R.color.colorLightTargetMetricWheelTitleText
            )
        )
        wheelTargetLayout.icArrow.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkTargetMetricWheelTint
                else R.color.colorLightTargetMetricWheelTint
            )
        )

        val wheelTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.wheelTarget)
            .setShape(Circle(400f))
            .setEffect(
                RippleEffect(
                    100f,
                    200f,
                    ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.colorDarkWheelSpotlightTarget
                        else R.color.colorLightWheelSpotlightTarget
                    )
                )
            )
            .setOverlay(wheelTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(wheelTarget)
            .setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkWheelSpotlightBackground
                    else R.color.colorLightWheelSpotlightBackground
                )
            )
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setContainer(binding.container)
            .build()
        spotlight.start()

        EventBus.getDefault()
            .post(SecondaryViewUpdateStateEvent(newState = SecondaryViews.MetricSpotlight))
        EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(true))

        wheelTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()
            EventBus.getDefault()
                .post(SecondaryViewUpdateStateEvent(newState = SecondaryViews.Empty))

            App.preferences.isMetricWheelSpotlightShown = true
            EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(false))

//            App.preferences.isMetricWheelSpotlightShown = true
//            userSettingsViewModel.updateField(UserSettingsFields.IsMetricWheelSpotlightShown, true)
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

            title.text = App.resourcesProvider.getStringLocale(R.string.add_post)
            amountMax.text = App.resourcesProvider.getStringLocale(R.string.amount_max)

            container.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.background_bottom_sheet_dark
                else R.drawable.background_bottom_sheet_light
            )

            viewHeader.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestTitleText
                    else R.color.colorLightViewDetailInterestTitleText
                )
            )

            amount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestAmountText
                    else R.color.colorLightViewDetailInterestAmountText
                )
            )

            amountMax.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestAmountMaxText
                    else R.color.colorLightViewDetailInterestAmountMaxText
                )
            )

            notesAmount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestNotesAmountText
                    else R.color.colorLightViewDetailInterestNotesAmountText
                )
            )

            startValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestStartValueText
                    else R.color.colorLightViewDetailInterestStartValueText
                )
            )

            currentValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestCurrentValueText
                    else R.color.colorLightViewDetailInterestCurrentValueText
                )
            )

            edit.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestEditBackgroundTint
                    else R.color.colorLightViewDetailInterestEditBackgroundTint
                )
            )

            icEdit.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewDetailInterestEditTint
                    else R.color.colorLightViewDetailInterestEditTint
                )
            )

//            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
//                val notes = userDiaryViewModel.getNotes()
//
//                title.text = interest.name
//                amount.text =
//                    String.format("%.2f", interest.currentValue)
//                        .replace(".", ",")
//
//                amountMax.isVisible = interest.currentValue == 10f
//
//                notesAmount.isVisible =
//                    notes.any { it.interest!!.interestId == interest.id }
//                notesAmount.text =
//                    getString(R.string.wrote_notes) +
//                            notes.filter { it.interest!!.interestId == interest.id }.size
//
//                startValue.text =
//                    getString(R.string.start_value) + " " + String.format(
//                        "%.2f",
//                        interest.startValue
//                    )
//                        .replace(".", ",")
//                currentValue.text =
//                    getString(R.string.current_value) + " " + String.format(
//                        "%.2f",
//                        interest.currentValue
//                    )
//                        .replace(".", ",")
//
//                edit.setOnClickListener {
//                    interestDetailBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                    showEditInterestDialog(interest)
//                }
//            }

            userDiaryViewModel.allNotesLiveData.observe(this@MetricFragment) { notes ->
                title.text = interest.name
                amount.text =
                    String.format("%.2f", interest.currentValue)
                        .replace(".", ",")

                amountMax.isVisible = interest.currentValue == 10f

                notesAmount.isVisible =
                    notes.any { it.interest!!.interestId == interest.id }
                notesAmount.text =
                    getString(R.string.wrote_notes) +
                            notes.filter { it.interest!!.interestId == interest.id }.size

                startValue.text =
                    getString(R.string.start_value) + " " + String.format(
                        "%.2f",
                        interest.startValue
                    )
                        .replace(".", ",")
                currentValue.text =
                    getString(R.string.current_value) + " " + String.format(
                        "%.2f",
                        interest.currentValue
                    )
                        .replace(".", ",")

                edit.setOnClickListener {
                    interestDetailBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    showEditInterestDialog(interest)
                }
            }
        }
    }

    private fun showAddInterestDialog() {
        val view: View = layoutInflater.inflate(
            R.layout.dialog_alert_add_interest,
            null
        )
        val alertDialogBuilder = AlertDialog.Builder(
            requireContext(),
            if (App.preferences.isDarkTheme) R.style.DialogThemeDark
            else R.style.DialogThemeLight
        )
        alertDialogBuilder.setPositiveButton(getString(R.string.create), null)
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), null)

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.setTitle(App.resourcesProvider.getStringLocale(R.string.create_interest))

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

        view.interestName.hint = App.resourcesProvider.getStringLocale(
            R.string.add_interest_name,
            App.preferences.locale
        )
        view.interestDescription.hint = App.resourcesProvider.getStringLocale(
            R.string.add_interest_description,
            App.preferences.locale
        )
        view.interestInitialValue.hint = App.resourcesProvider.getStringLocale(
            R.string.add_interest_initial_value
        )

        view.addInterestContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestBackground
                else R.color.colorLightDialogAlertAddInterestBackground
            )
        )

        view.interestName.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        view.interestName.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestNameText
                else R.color.colorLightDialogAlertAddInterestNameText
            )
        )

        view.interestInitialValue.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )
        view.interestInitialValue.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestNameHint
                else R.color.colorLightDialogAlertAddInterestNameHint
            )
        )

        view.interestInitialValue.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestDescriptionText
                else R.color.colorLightDialogAlertAddInterestDescriptionText
            )
        )

        view.interestName.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestNameHint
                else R.color.colorLightDialogAlertAddInterestNameHint
            )
        )

        view.interestDescription.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        view.interestDescription.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestDescriptionText
                else R.color.colorLightDialogAlertAddInterestDescriptionText
            )
        )

        view.interestDescription.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestDescriptionHint
                else R.color.colorLightDialogAlertAddInterestDescriptionHint
            )
        )

        view.icon.getRecycler().setItemViewCacheSize(AllLogo().logoList.size)
        view.icon.adapter.values = iconValues

        view.icon.adapter.notifyDataSetChanged()

        view.icon.isHapticFeedbackEnabled = true

        view.interestInitialValue.filters = arrayOf<InputFilter>(InputFilterMinMax("0.0", "10.0"))

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                when {
                    view.interestName.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent(getString(R.string.entry_title_interest)))
                    view.interestDescription.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent(getString(R.string.entry_description_interest)))
                    else -> {
                        val interest = UserCustomInterest(
                            id = System.currentTimeMillis().toString() + UUID.randomUUID()
                                .toString(),
                            name = view.interestName.text.toString(),
                            description = view.interestDescription.text.toString(),
                            startValue = view.interestInitialValue.text.toString().toFloat(),
                            currentValue = view.interestInitialValue.text.toString().toFloat(),
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
        val view: View = layoutInflater.inflate(
            R.layout.dialog_alert_edit_interest,
            null
        )
        val alertDialogBuilder = AlertDialog.Builder(
            requireContext(),
            if (App.preferences.isDarkTheme) R.style.DialogThemeDark
            else R.style.DialogThemeLight
        )
        alertDialogBuilder.setPositiveButton(getString(R.string.save), null)
        alertDialogBuilder.setNegativeButton(getString(R.string.delete), null)

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.setTitle(getString(R.string.edit_interest))
        alertDialog.setCancelable(true)

        view.editInterestContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertEditInterestBackground
                else R.color.colorLightDialogAlertEditInterestBackground
            )
        )

        view.interestName.hint = App.resourcesProvider.getStringLocale(
            R.string.add_interest_name,
            App.preferences.locale
        )
        view.interestDescription.hint = App.resourcesProvider.getStringLocale(
            R.string.add_interest_description,
            App.preferences.locale
        )

        view.interestName.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        view.interestDescription.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        view.interestName.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertEditInterestNameText
                else R.color.colorLightDialogAlertEditInterestNameText
            )
        )

        view.interestName.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertEditInterestNameHint
                else R.color.colorLightDialogAlertEditInterestNameHint
            )
        )

        view.interestDescription.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertEditInterestDescriptionText
                else R.color.colorLightDialogAlertEditInterestDescriptionText
            )
        )

        view.interestDescription.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertEditInterestDescriptionHint
                else R.color.colorLightDialogAlertEditInterestDescriptionHint
            )
        )

        view.interestName.setText(interest.name)
        view.interestDescription.setText(
            interest.description
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
                        .post(ShowFailEvent(getString(R.string.entry_title_interest)))
                    view.interestDescription.text.isNullOrEmpty() -> EventBus.getDefault()
                        .post(ShowFailEvent(getString(R.string.entry_description_interest)))
                    else -> {
                        interest.name = view.interestName.text.toString()
                        interest.description = view.interestDescription.text.toString()

                        userInterestsViewModel.updateInterest(interest) {
                            EventBus.getDefault().post(ShowSuccessEvent(getString(R.string.saved)))
                        }

                        adapter.updateInterestById(interest)

                        alertDialog.dismiss()
                    }
                }
            }

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                userInterestsViewModel.deleteInterest(interest) {
                    EventBus.getDefault().post(ShowSuccessEvent(getString(R.string.deleted)))
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
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            binding.averageAmount.text =
                if (userInterestsViewModel.getInterestsByUserId().isNotEmpty())
                    String.format("%.1f", (userInterestsViewModel.calculateCurrentValueAverage()))
                        .replace(".", ",")
                else "0"
        }

    }

    private fun setupList() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val list = userInterestsViewModel.getInterestsByUserId().toInterestsList()
            list.add(EmptyInterest())
            adapter = MetricListAdapter(requireContext(), list)
            binding.recycler.adapter = adapter
        }

        setAverageAmount()

        userDiaryViewModel.allNotesLiveData.observe(this@MetricFragment) { notes ->
            binding.diaryAmount.text = notes.size.toString()
        }

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            userSettingsViewModel
                .getUserSettingsById(App.preferences.uid!!)?.let {
                    binding.daysAmount.text =
                        ((System.currentTimeMillis() - (it.dateRegistration
                            ?: "0").toLong()) / 1000 / 60 / 60 / 24).toInt()
                            .toString()
                }
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
            wheelStateLight.textSize = 12f
            wheelStateDark.textSize = 12f
            listStateLight.textSize = 12f
            listStateDark.textSize = 12f

            metricStateControlGroupLight.setOnSelectedOptionChangeCallback {
                wheelStateLight.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 0) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                            else R.color.colorLightMetricControlGroupActiveText
                        } else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                            else R.color.colorLightMetricControlGroupInactiveText
                        }
                    )
                )

                listStateLight.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 1) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                            else R.color.colorLightMetricControlGroupActiveText
                        } else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                            else R.color.colorLightMetricControlGroupInactiveText
                        }
                    )
                )

                changeMetricListVisibility(it == 1)
            }

            metricStateControlGroupDark.setOnSelectedOptionChangeCallback {
                wheelStateDark.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 0) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                            else R.color.colorLightMetricControlGroupActiveText
                        } else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                            else R.color.colorLightMetricControlGroupInactiveText
                        }
                    )
                )

                listStateDark.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (it == 1) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                            else R.color.colorLightMetricControlGroupActiveText
                        } else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                            else R.color.colorLightMetricControlGroupInactiveText
                        }
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
        binding.currentStateLight.textSize = 12f
        binding.currentStateDark.textSize = 12f
        binding.startStateLight.textSize = 12f
        binding.startStateDark.textSize = 12f

        binding.radarStateControlGroupLight.setOnSelectedOptionChangeCallback {
            binding.currentStateLight.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 0) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupActiveText
                        else R.color.colorLightRadarControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupInactiveText
                        else R.color.colorLightRadarControlGroupInactiveText
                    }
                )
            )

            binding.startStateLight.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 1) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupActiveText
                        else R.color.colorLightRadarControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupInactiveText
                        else R.color.colorLightRadarControlGroupInactiveText
                    }
                )
            )

            binding.radarChart.data.dataSets[2].isVisible = it == 1
            binding.radarChart.invalidate()
        }

        binding.radarStateControlGroupDark.setOnSelectedOptionChangeCallback {
            binding.currentStateDark.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 0) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupActiveText
                        else R.color.colorLightRadarControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupInactiveText
                        else R.color.colorLightRadarControlGroupInactiveText
                    }
                )
            )

            binding.startStateDark.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it == 1) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupActiveText
                        else R.color.colorLightRadarControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkRadarControlGroupInactiveText
                        else R.color.colorLightRadarControlGroupInactiveText
                    }
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
        binding.radarChart.webColor = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkChartWeb
            else R.color.colorLightChartWeb
        )
        binding.radarChart.webLineWidthInner = 0.5f
        binding.radarChart.webColorInner =
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkChartWebInner
                else R.color.colorLightChartWebInner
            )
        binding.radarChart.webAlpha = 100

        setChartData()

        binding.radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        setChartAxis()
        setupRadarControlGroup()

        binding.radarChart.onTouchListener =
            object : PieRadarChartTouchListener(binding.radarChart) {
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {

                    binding.metricStateControlGroupLight.setSelectedIndex(1, true)
                    binding.metricStateControlGroupDark.setSelectedIndex(1, true)

                    binding.wheelStateLight.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                            else R.color.colorLightMetricControlGroupInactiveText
                        )
                    )

                    binding.wheelStateDark.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                            else R.color.colorLightMetricControlGroupInactiveText
                        )
                    )

                    binding.listStateLight.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                            else R.color.colorLightMetricControlGroupActiveText
                        )
                    )

                    binding.listStateDark.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                            else R.color.colorLightMetricControlGroupActiveText
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

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val icons: ArrayList<RadarEntry> = ArrayList()
            val entries1: ArrayList<RadarEntry> = ArrayList()
            val entries2: ArrayList<RadarEntry> = ArrayList()

            val interests = userInterestsViewModel.getInterestsByUserId()

            for (i in interests.indices) {
                if (isAdded) {
                    val val0 = 12f
                    val radarEntryIcon = RadarEntry(val0)

                    val bMap = BitmapFactory.decodeResource(
                        requireActivity().resources,
                        AllLogo().getLogoById(interests[i].logoId.toString())
                    )
                    val bMapScaled = Bitmap.createScaledBitmap(bMap, 60, 60, true)

                    radarEntryIcon.icon = BitmapDrawable(requireActivity().resources, bMapScaled)
                    icons.add(radarEntryIcon)

                    val val1 = interests[i].currentValue
                    entries1.add(RadarEntry(val1!!))

                    val val2 = interests[i].startValue
                    entries2.add(RadarEntry(val2!!))
                }
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
            rdsCurrent.color = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkRdsCurrent
                else R.color.colorLightRdsCurrent
            )
            rdsCurrent.fillColor = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkRdsCurrentFill
                else R.color.colorLightRdsCurrentFill
            )
            rdsCurrent.setDrawFilled(true)
            rdsCurrent.fillAlpha = 180
            rdsCurrent.lineWidth = 1f
            rdsCurrent.valueTextColor = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkRdsCurrentValueText
                else R.color.colorLightRdsCurrentValueText
            )
            rdsCurrent.isDrawHighlightCircleEnabled = false
            rdsCurrent.setDrawHighlightIndicators(false)

            val rdsDefault = RadarDataSet(entries2, "Default")
            rdsDefault.color = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkRdsDefault
                else R.color.colorLightRdsDefault
            )
            rdsDefault.fillColor = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkRdsDefaultFill
                else R.color.colorLightRdsDefaultFill
            )
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

            binding.radarChart.post {
                binding.radarChart.data = data
                binding.radarChart.invalidate()
            }
        }
    }

    inner class Handler {
        fun onInfoClicked(v: View) {
            getBalloon(getString(R.string.metric_info))
                .showAlignBottom(binding.info)
        }

        fun onAffirmationClicked(v: View) {
            EventBus.getDefault().post(ShowAffirmationEvent())
        }
    }
}