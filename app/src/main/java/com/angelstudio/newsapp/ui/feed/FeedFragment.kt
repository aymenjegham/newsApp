package com.angelstudio.newsapp.ui.feed
 import android.os.Bundle
 import android.util.Log
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
  import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
 import androidx.navigation.Navigation
 import com.angelstudio.newsapp.R
 import com.angelstudio.newsapp.databinding.FragmentFeedBinding

 import com.angelstudio.newsapp.ui.base.ScopedFragment
 import com.google.android.material.snackbar.Snackbar
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
    private lateinit var binding : FragmentFeedBinding
    private lateinit var myView :View



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = DataBindingUtil.inflate(inflater , R.layout.fragment_feed,container , false)
        myView= binding.root

        bindUi()


        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)


        binding.mySwiperefresh.setOnRefreshListener {
            refresh()
            mySwiperefresh.setRefreshing(false)
        }


        return myView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =ViewModelProviders.of(this,viewModelFactory).get(FeedFragmentViewModel::class.java)


    }


    private fun bindUi()=launch {
        val topHeadline =viewModel.topHeadline.await()
        val isConnected =viewModel.isConnected.await()


        isConnected.observe(this@FeedFragment, Observer {
            if(it==true){
                 binding.progressBar2.apply {
                    visibility=View.GONE
                }
            }


        })

        topHeadline.observe(this@FeedFragment, Observer {

            if(it == null || it.isEmpty()) return@Observer
            binding.progressBar2.apply {
                visibility=View.GONE
            }

          binding.recyclerView.apply {
                topHeadlineAdapter = TopHeadlineAdapter(TopHeadlineListener { 
                    url,source ->  //viewModel.onTopHeadlineClicked(url)

                  val actionDetail = FeedFragmentDirections.actionFeedFragmentToDetailFragment(url,source)
                    Navigation.findNavController(view!!).navigate(actionDetail)
                    viewModel.onDetailNavigated()

                }, ArchiveListener {
                    viewModel.archive(it)

                },lifecycle,context)
                adapter = topHeadlineAdapter
                topHeadlineAdapter.submitList(it)

            }
        })

    }

    private fun refresh()=launch {
        viewModel.fetchTopHeadline()
    }

}



