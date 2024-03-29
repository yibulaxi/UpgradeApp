package ru.get.better.ui.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
import ru.get.better.BR
import ru.get.better.event.UpdateThemeEvent
import ru.get.better.glide.GlideApp
import ru.get.better.util.ext.getViewModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseActivity<V : ViewModel, B : ViewDataBinding>(
    private val layoutResourceId: Int?,
    private val viewModelClass: KClass<V>? = null,
    private val handler: KClass<*>? = null
) : DaggerAppCompatActivity(), Toolbar.OnMenuItemClickListener {

    //    protected val nullableBinding: B? get() = _binding
    var binding: B
        get() = _binding!!
        set(value) {
            _binding = value
        }

    val glideRequestManager by lazy { GlideApp.with(this) }

    //    protected val nullableViewModel: V? get() = _viewModel
    protected var viewModel: V
        get() = _viewModel!!
        set(value) {
            _viewModel = value
        }

    private var _binding: B? = null
    private var _viewModel: V? = null
    var savedInstanceState: Bundle? = null
    private var isBindingInitialized = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

//    var toolbarTextColor: Int = 0

//    private var isDoubleClicked: Boolean = false

    /**
     * Overriding classes should use onLayoutReady or onViewModelReady instead of onCreate to ensure app is fully initialized
     */
    final override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Timber.e("onCreate")

        savedInstanceState?.clear()
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, layoutResourceId!!)
        this.isBindingInitialized = false
        this.savedInstanceState = savedInstanceState

        onLayoutReady(savedInstanceState)
        proceedWithOnCreate(savedInstanceState)
    }

    open fun updateThemeAndLocale() {}

    @Subscribe
    fun onUpdateThemeEvent(e: UpdateThemeEvent) {
        updateThemeAndLocale()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    private fun proceedWithOnCreate(savedInstanceState: Bundle?) {
        synchronized(this) {
            Timber.e("proceedWithOnCreate")
            this.savedInstanceState = null

            if (!isBindingInitialized) {
                Timber.e("initializing binding")
                isBindingInitialized = true

                if (viewModelClass != null) {
                    _viewModel = getViewModel(viewModelFactory, viewModelClass.java)
                    binding.setVariable(BR.viewModel, viewModel)
                }

                if (handler != null) {
                    try {
                        binding.setVariable(BR.handler, handler.java.newInstance())
                    } catch (ex: InstantiationException) {
                        binding.setVariable(
                            BR.handler,
                            handler.java.getDeclaredConstructor(this.javaClass).newInstance(this)
                        )
                    }
                }

                if (viewModelClass != null) {
                    binding.lifecycleOwner = this
                }

                _viewModel?.let { onViewModelReady(it) }
            }
        }
    }

    protected open fun onLayoutReady(savedInstanceState: Bundle?) {
        updateThemeAndLocale()
    }

    protected open fun onViewModelReady(viewModel: V) {}

    override fun onStart() {
        Timber.e("onStart")
        super.onStart()

        try {
            EventBus.getDefault().register(this)
        } catch (e: EventBusException) {
            Timber.i(e)
        }
    }

    override fun onStop() {
        Timber.e("onStop")
        super.onStop()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        Timber.e("onDestroy")
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
