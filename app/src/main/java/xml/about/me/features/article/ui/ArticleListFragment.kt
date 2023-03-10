package xml.about.me.features.article.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import xml.about.me.R
import xml.about.me.core.util.*
import xml.about.me.core.util.ui.BaseFragment
import xml.about.me.databinding.FragmentArticleListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleListFragment : BaseFragment<FragmentArticleListBinding>() {

    private val viewModel by viewModels<ArticleListViewModel>()

    private lateinit var articleListAdapter: ArticleListAdapter

    private lateinit var scrollListener: EndlessRecyclerOnScrollListener

    override val layoutId: Int
        get() = R.layout.fragment_article_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initValue()
        initView()
        initObservation()
    }

    private fun initValue() {}

    private fun initView() {

        binding.apply {
            networkCallback = object : NetworkCallback {
                override fun refresh() {
                    viewModel.refresh()
                }
            }
        }

        initRecyclerView()
    }

    private fun initObservation() {

        viewModel.networkViewState.observe(viewLifecycleOwner) {

            binding.networkViewState = it

            if (it.showProgressMore)
                showLoadingMore()

            if (it.showSuccess)
                hideLoading()

            if (it.showError)
                hideLoading()
        }

        viewModel.articleList.observe(viewLifecycleOwner) {
            articleListAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {

        articleListAdapter = ArticleListAdapter {
            mainHelper.navigate(
                ArticleListFragmentDirections.actionArticleListFragmentToArticleFragment(   it.id)
            )
        }

        scrollListener = object : EndlessRecyclerOnScrollListener() {

            override fun onLoadMore(current_page: Int) {
                viewModel.getNextPage()
            }

            override fun onScrollTop(isVisible: Boolean) {}
        }

        binding.rvArticleList.apply {
            addOnScrollListener(scrollListener)
            disableAnimationChanges()
            addVerticalDividerSpacing4x(requireContext())
            adapter = articleListAdapter
        }
    }

    private fun showLoadingMore() {
        articleListAdapter.setLoadingState(true)
    }

    private fun hideLoading() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            if (viewModel.networkViewState.value?.showProgressMore != true &&
                viewModel.currentPage != 1) {
                articleListAdapter.setLoadingState(false)
            }
        }
    }

    override fun onScrollToTop() {
        binding.apply {
            rvArticleList.scrollToTop()
        }
    }
}
