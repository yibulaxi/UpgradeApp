package ru.get.better.ui.faq

import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentFaqBinding
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.faq.adapter.FaqAdapter
import ru.get.better.vm.BaseViewModel

class FaqFragment : BaseFragment<BaseViewModel, FragmentFaqBinding>(
    ru.get.better.R.layout.fragment_faq,
    BaseViewModel::class,
    Handler::class
) {

    private lateinit var faqAdapter: FaqAdapter

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        faqAdapter = FaqAdapter(
            requireContext(),
            binding.recycler,
            initFaqs()
        )
        binding.recycler.adapter = faqAdapter

    }

    override fun updateThemeAndLocale() {

        binding.faqContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryBackground
            else R.color.colorLightFragmentDiaryBackground
        ))

        if (::faqAdapter.isInitialized)
            faqAdapter.notifyDataSetChanged()
    }

    private fun initFaqs(): List<Pair<String, String>> {
        val faqs = arrayListOf<Pair<String, String>>()

        val titles = resources.getStringArray(R.array.faq_title)
        val texts = resources.getStringArray(R.array.faq_text)

        titles.forEachIndexed { index, title ->
            faqs.add(
                Pair(
                    title,
                    texts[index]
                )
            )
        }

        return faqs
    }

    inner class Handler
}