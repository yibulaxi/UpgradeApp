package ru.get.better.ui.base

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.snackbar_success.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.BR
import ru.get.better.R
import ru.get.better.event.UpdateThemeEvent
import ru.get.better.glide.GlideApp
import ru.get.better.navigation.Navigator
import ru.get.better.ui.settings.SettingsFragment
import ru.get.better.util.ext.getViewModel
import ru.get.better.util.lazyErrorDelegate
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseFragment<T : ViewModel, B : ViewDataBinding>(
    private val layoutResourceId: Int,
    private val viewModelClass: KClass<T>? = null,
    private val handler: KClass<*>? = null,
    private val isSharedViewModel: Boolean = false
) : DaggerFragment(), Toolbar.OnMenuItemClickListener {

    protected lateinit var drawerArrowDrawable: DrawerArrowDrawable
    protected lateinit var binding: B

    private var viewModel: T
        get() = _viewModel!!
        set(value) {
            _viewModel = value
        }

    private var _viewModel: T? = null
    private var pendingAuthAction: (() -> Unit)? = null

    protected val glideRequestManager by lazy { GlideApp.with(this) }

    private var backPressedCallback: OnBackPressedCallback? = null

    protected val errorDelegate by lazyErrorDelegate { requireContext() }

    private var isDoubleClicked: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var rootView: View

    override fun onStart() {
        registerBackPressedCallback()
        super.onStart()
        try {
            EventBus.getDefault().register(this)
        } catch (e: EventBusException) {
            Timber.i(e)
        }
        Timber.d("${this@BaseFragment.javaClass.name}::OnStart")
    }

    open fun updateThemeAndLocale(
        withAnimation: Boolean = false,
        withTextAnimation: Boolean = false
    ) {

    }

    open fun updateThemeAndLocale() {

    }

    @Subscribe
    fun onUpdateThemeEvent(e: UpdateThemeEvent) {
        if (this is SettingsFragment) {
            updateThemeAndLocale(
                e.withAnimation,
                e.withTextAnimation
            )
        } else updateThemeAndLocale()

    }

    @Subscribe
    fun onEvent(event: Any) {
        //Dummy event subscription to prevent exceptions
    }

    override fun onStop() {
        unregisterBackPressedCallback()
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        Timber.d("${this@BaseFragment.javaClass.name}::OnStop")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)

        if (viewModelClass != null) {
            _viewModel = if (isSharedViewModel) {
                requireActivity().getViewModel(
                    viewModelFactory,
                    viewModelClass.java
                )
            } else {
                getViewModel(
                    viewModelFactory,
                    viewModelClass.java
                )
            }

            binding.setVariable(BR.viewModel, viewModel)
        }

        if (handler != null) {
            try {
                binding.setVariable(BR.handler, handler.java.newInstance())
            } catch (ex: java.lang.InstantiationException) {
                binding.setVariable(
                    BR.handler,
                    handler.java.getDeclaredConstructor(this.javaClass).newInstance(this)
                )
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        registerBackPressedCallback()

        onLayoutReady(savedInstanceState)

        _viewModel?.let { onViewModelReady(it) }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            // TODO
        }
        return false
    }

    protected open fun onLayoutReady(savedInstanceState: Bundle?) {
        updateThemeAndLocale()
        updateThemeAndLocale(withAnimation = false, withTextAnimation = false)
        // Empty for optional override
    }

    protected open fun onViewModelReady(viewModel: T) {
        // Empty for optional override
    }

    // optional override
    protected open fun onBackPressed() {
        if (!Navigator.goBack(this@BaseFragment)) {
            requireActivity().finish()
        }
/*        else {
            Log.d("TAG", "onBackPressed else: ")

            if (isDoubleClicked) {
                requireActivity().finish()
            }

            isDoubleClicked = true
            showSnackBar(getString(R.string.message_back_double_click))
            Handler(Looper.getMainLooper()).postDelayed({ isDoubleClicked = false }, 2000)
        }*/
    }

    private fun registerBackPressedCallback() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback!!
        )
    }

    private fun unregisterBackPressedCallback() {
        backPressedCallback?.isEnabled = false
    }

    fun showSnackBar(msg: String) {
        context?.let {
            val objLayoutInflater: LayoutInflater =
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val snackView: View = objLayoutInflater.inflate(R.layout.snackbar_success, null)

            snackView.content.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.container_snackbar_dark
                else R.drawable.container_snackbar_light
            )
            snackView.textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkSnackbarSuccessText
                    else R.color.colorLightSnackbarSuccessText
                )
            )
            snackView.imageView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.logo))
            showSnackBarView(msg, snackView)
        }
    }

    private fun showSnackBarView(msg: String, snackView: View) {
        this.let {
            val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
            val layout = snackBar.view as Snackbar.SnackbarLayout

            val view = snackBar.view

            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.setPadding(0, 60, 0, 0)
            snackView.layoutParams = params


            val textView: TextView = snackView.findViewById(R.id.textView) as TextView

            textView.text = msg

            layout.removeAllViews()
            layout.addView(snackView, 0)

            snackBar.view.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    if (App.Companion.preferences.isDarkTheme) R.drawable.container_snackbar_dark
                    else R.drawable.container_snackbar_light
                )

            snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            snackBar.show()
        }
    }

}