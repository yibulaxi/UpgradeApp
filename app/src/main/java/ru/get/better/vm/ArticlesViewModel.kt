package ru.get.better.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.model.Article
import ru.get.better.model.ArticleState
import ru.get.better.model.ArticleType
import ru.get.better.model.DiaryNote
import ru.get.better.util.ext.mutableLiveDataOf
import ru.get.better.vm.BaseViewModel
import ru.get.better.vm.UserInterestsViewModel
import javax.inject.Inject

class ArticlesViewModel @Inject constructor(
) : BaseViewModel() {

    lateinit var currentArticlesLiveData: MutableLiveData<List<Article>>

    init {
        EventBus.getDefault().register(this)
        currentArticlesLiveData = mutableLiveDataOf(emptyList())
    }

     fun getArticles(type: ArticleType) {
         viewModelScope.launch(Dispatchers.IO) {
             currentArticlesLiveData.postValue(App.articlesDatabase.articlesDao().getAllByType(type.flag))
         }
     }

    fun updateReadStateArticle(
        articleId: Int,
        newState: ArticleState,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val article = App.articlesDatabase.articlesDao().getById(articleId)
            article.flag = newState.flag

            App.articlesDatabase.articlesDao().update(article)
        }.invokeOnCompletion { onComplete.invoke() }
    }

}