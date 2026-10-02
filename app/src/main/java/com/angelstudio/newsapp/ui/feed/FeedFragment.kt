package com.angelstudio.newsapp.ui.feed

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.databinding.FragmentFeedBinding
import com.angelstudio.newsapp.ui.base.ScopedFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import tyrantgit.explosionfield.ExplosionField


class FeedFragment : ScopedFragment(), DIAware {

    override val di by closestDI()


    private val viewModelFactory: FeedFragmentViewModelFactory by instance()
    private lateinit var viewModel: FeedFragmentViewModel
    private lateinit var topHeadlineAdapter: TopHeadlineAdapter
    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding
        get() = _binding ?: error("FeedFragment binding is only valid between onCreateView and onDestroyView")
    private lateinit var mExplosionField :ExplosionField
    private lateinit var fab: FloatingActionButton
    private lateinit var linearLayout: LinearLayout






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding.mySwiperefresh.setOnRefreshListener {
            refresh()
            binding.mySwiperefresh.setRefreshing(false)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)

        fab = (activity as? AppCompatActivity)!!.findViewById(R.id.floatingActionButton)
        fab.visibility= View.VISIBLE

        linearLayout = (activity as? AppCompatActivity)!!.findViewById(R.id.tvdeleteall)
        linearLayout.visibility=View.GONE


        mExplosionField = ExplosionField.attach2Window(activity as? AppCompatActivity)


        fab.setOnClickListener {
            binding.recyclerView.smoothScrollToPosition(0)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[FeedFragmentViewModel::class.java]
        bindUi()


    }


    private fun bindUi() = viewLifecycleOwner.lifecycleScope.launch {
        val topHeadline =viewModel.topHeadline.await()
        val isConnected =viewModel.isConnected.await()


        isConnected.observe(viewLifecycleOwner, Observer {
            if(it==true){
                var toast: Toast

                binding.progressBar2.apply {

                    toast=Toasty.warning(binding.root.context, getString(R.string.nointernet), Toast.LENGTH_LONG, true)
                    toast.setGravity(Gravity.BOTTOM,0,150)
                    toast.show()
                    visibility=View.GONE
                }
            }


        })

        topHeadline.observe(viewLifecycleOwner, Observer { it ->

            if(it == null || it.isEmpty()) return@Observer
            binding.progressBar2.apply {
                visibility=View.GONE
            }

          binding.recyclerView.apply {
                topHeadlineAdapter = TopHeadlineAdapter(TopHeadlineListener { 
                    url,source ->  //viewModel.onTopHeadlineClicked(url)

                  val actionDetail = FeedFragmentDirections.actionFeedFragmentToDetailFragment(url,source)
                    Navigation.findNavController(binding.root).navigate(actionDetail)
                    viewModel.onDetailNavigated()

                }, ArchiveListener {
                   // viewModel.archive(it)
                    val article =it
                    var toast: Toast
                    val dialogContext = binding.root.context


                    MaterialDialog(dialogContext).show {
                        title(R.string.warning)
                        message(R.string.warningarchive)
                        positiveButton(R.string.yes){
                            mExplosionField.explode(it.view)
                            viewModel.archive(article)
                            toast=Toasty.success(dialogContext,  getString(R.string.archived), Toast.LENGTH_LONG, true)
                            toast.setGravity(Gravity.BOTTOM,0,150)
                            toast.show()
                        }
                        negativeButton(R.string.cancel){
                            it.dismiss()
                        }
                    }

                }, ShareListener {

                    intentShareText(activity!!,getString(R.string.share_message,it.title, it.url ?: "" ))


                },lifecycle,requireContext())
                adapter = topHeadlineAdapter
                topHeadlineAdapter.submitList(it)

            }
        })

    }

    private fun refresh()=launch {
        viewModel.fetchTopHeadline()
    }

    override fun onDestroyView() {
        if (::fab.isInitialized) {
            fab.setOnClickListener(null)
        }
        _binding = null
        super.onDestroyView()
    }

    private fun intentShareText(activity: Activity, text: String) {
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
            .setText(text)
            .setType("text/plain")
            .createChooserIntent()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // If we're on Lollipop, we can open the intent as a document
                    addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                } else {
                    // Else, we will use the old CLEAR_WHEN_TASK_RESET flag
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                }
            }
        activity.startActivity(shareIntent)
    }
}
