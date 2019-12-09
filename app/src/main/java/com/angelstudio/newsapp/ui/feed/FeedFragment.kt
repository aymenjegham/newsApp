package com.angelstudio.newsapp.ui.feed
 import android.app.Activity
 import android.content.Intent
 import android.os.Build
 import android.os.Bundle
 import android.util.Log
 import android.view.Gravity
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import android.widget.LinearLayout
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
 import androidx.core.app.ShareCompat
 import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
 import androidx.navigation.Navigation
 import com.afollestad.materialdialogs.MaterialDialog
 import com.angelstudio.newsapp.R
 import com.angelstudio.newsapp.databinding.FragmentFeedBinding

 import com.angelstudio.newsapp.ui.base.ScopedFragment
 import com.google.android.material.floatingactionbutton.FloatingActionButton
 import com.google.android.material.snackbar.Snackbar
 import es.dmoral.toasty.Toasty
 import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
 import tyrantgit.explosionfield.ExplosionField


class FeedFragment : ScopedFragment(),KodeinAware {

    override val kodein by closestKodein()


    private val viewModelFactory: FeedFragmentViewModelFactory by instance()
    private lateinit var viewModel: FeedFragmentViewModel
    private lateinit var topHeadlineAdapter: TopHeadlineAdapter
    private lateinit var binding : FragmentFeedBinding
    private lateinit var myView :View
    private lateinit var mExplosionField :ExplosionField
    private lateinit var fab: FloatingActionButton
    private lateinit var linearLayout: LinearLayout






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = DataBindingUtil.inflate(inflater , R.layout.fragment_feed,container , false)
        myView= binding.root

        bindUi()


        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)

        fab = (activity as? AppCompatActivity)!!.findViewById(R.id.floatingActionButton)
        fab.visibility= View.VISIBLE

        linearLayout = (activity as? AppCompatActivity)!!.findViewById(R.id.tvdeleteall)
        linearLayout.visibility=View.GONE


        mExplosionField = ExplosionField.attach2Window(activity as? AppCompatActivity)


        binding.mySwiperefresh.setOnRefreshListener {
            refresh()
            mySwiperefresh.setRefreshing(false)
        }

        fab.setOnClickListener { v: View? ->
            recycler_view.smoothScrollToPosition(0)
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
                var toast: Toast

                binding.progressBar2.apply {

                    toast=Toasty.warning(view!!.context, getString(R.string.nointernet), Toast.LENGTH_LONG, true)
                    toast.setGravity(Gravity.BOTTOM,0,150)
                    toast.show()
                    visibility=View.GONE
                }
            }


        })

        topHeadline.observe(this@FeedFragment, Observer { it ->

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
                   // viewModel.archive(it)
                    val article =it
                    var toast: Toast


                    MaterialDialog(view!!.context).show {
                        title(R.string.warning)
                        message(R.string.warningarchive)
                        positiveButton(R.string.yes){
                            mExplosionField.explode(it.view)
                            viewModel.archive(article)
                            toast=Toasty.success(view.context,  getString(R.string.archived), Toast.LENGTH_LONG, true)
                            toast.setGravity(Gravity.BOTTOM,0,150)
                            toast.show()
                        }
                        negativeButton(R.string.cancel){
                            it.dismiss()
                        }
                    }

                }, ShareListener {

                    intentShareText(activity!!,getString(R.string.share_message,it.title, it.url ?: "" ))


                },lifecycle,context)
                adapter = topHeadlineAdapter
                topHeadlineAdapter.submitList(it)

            }
        })

    }

    private fun refresh()=launch {
        viewModel.fetchTopHeadline()
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



