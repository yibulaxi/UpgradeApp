package ru.get.better.ui.faq

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_settings.*
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
            binding.recycler,
            initFaqs()
        )
        binding.recycler.adapter = faqAdapter

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

    inner class Handler {

    }
}