package com.angelstudio.newsapp.ui.feed
 import android.os.Bundle
 import android.util.Log
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
 import androidx.navigation.Navigation

import androidx.navigation.fragment.NavHostFragment
 import androidx.navigation.ui.setupWithNavController
 import com.angelstudio.newsapp.R
 import com.angelstudio.newsapp.databinding.FragmentFeedBinding
import com.angelstudio.newsapp.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FeedFragment : ScopedFragment(),KodeinAware {

    override val kodein by closestKodein()


    private val viewModelFactory: FeedFragmentViewModelFactory by instance()
    private lateinit var viewModel: FeedFragmentViewModel
    private lateinit var topHeadlineAdapter: TopHeadlineAdapter
    private lateinit var binding :FragmentFeedBinding
    private lateinit var myView :View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)


        val host: NavHostFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
            ?: return

        // Set up Action Bar
        val navController = host.navController

        // Setup bottom navigation view
        binding.bottomNav.setupWithNavController(navController)



    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = DataBindingUtil.inflate(inflater , R.layout.fragment_feed,container , false)
        myView= binding.root


        return myView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =ViewModelProviders.of(this,viewModelFactory).get(FeedFragmentViewModel::class.java)


        bindUi()
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.Naws_App)
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = getString(R.string.Feed)

        binding.mySwiperefresh.setOnRefreshListener {
            refresh()
            mySwiperefresh.setRefreshing(false)
        }
    }

    private fun bindUi()=launch {
        val topHeadline =viewModel.topHeadline.await()
        val naviagte =viewModel.navigateToDetail.await()

        topHeadline.observe(this@FeedFragment, Observer {
            if(it == null || it.isEmpty()) return@Observer

          binding.recyclerView.apply {
                showShimmerAdapter()
                topHeadlineAdapter = TopHeadlineAdapter(TopHeadlineListener { 
                    url ->  viewModel.onTopHeadlineClicked(url)
                })
                adapter = topHeadlineAdapter
                topHeadlineAdapter.submitList(it)
                hideShimmerAdapter()
            }
        })

        naviagte.observe(this@FeedFragment, Observer {
            if(it == null || it.isEmpty()) return@Observer

            val actionDetail = FeedFragmentDirections.actionFeedFragmentToDetailFragment(it)
            Navigation.findNavController(view!!).navigate(actionDetail)
            viewModel.onDetailNavigated()
        })
    }

    private fun refresh()=launch {
        viewModel.fetchTopHeadline()
    }



}



