package com.velkonost.upgrade.ui.base

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
import com.velkonost.upgrade.BR
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.velkonost.upgrade.R
import com.velkonost.upgrade.glide.GlideApp
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.util.ext.getViewModel
import com.velkonost.upgrade.util.lazyErrorDelegate
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.snackbar_success.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
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
                ContextCompat.getDrawable(requireContext(), R.drawable.container_snackbar)

            snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            snackBar.show()
        }
    }

}