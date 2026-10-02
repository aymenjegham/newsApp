package com.angelstudio.newsapp.ui.archive

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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.databinding.ArchiveFragmentBinding
import com.angelstudio.newsapp.ui.base.ScopedFragment
 import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import tyrantgit.explosionfield.ExplosionField


class ArchiveFragment : ScopedFragment(), DIAware {

    override val di by closestDI()

    private val viewModelFactory: ArchiveViewModelFactory by instance()
    private lateinit var viewModel: ArchiveViewModel
    private lateinit var archiveAdapter: ArchiveAdapter
    private var _binding: ArchiveFragmentBinding? = null
    private val binding: ArchiveFragmentBinding
        get() = _binding ?: error("ArchiveFragment binding is only valid between onCreateView and onDestroyView")
    private lateinit var mExplosionField :ExplosionField
    private lateinit var fab: FloatingActionButton
    private lateinit var linearLayout: LinearLayout
    private lateinit var tv :TextView




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ArchiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.archive)

        fab = (activity as? AppCompatActivity)!!.findViewById(R.id.floatingActionButton)
        fab.visibility= View.VISIBLE

        linearLayout = (activity as? AppCompatActivity)!!.findViewById(R.id.tvdeleteall)
        linearLayout.visibility=View.GONE

        tv = (activity as? AppCompatActivity)!!.findViewById(R.id.tvdelet)


        mExplosionField = ExplosionField.attach2Window(activity as? AppCompatActivity)

        fab.setOnClickListener {
            binding.recyclerViewArchive.smoothScrollToPosition(0)
        }

        var toast: Toast
        tv.setOnClickListener { v:View? ->
            val dialogContext = binding.root.context
            MaterialDialog(dialogContext).show {
                title(R.string.warning)
                message(R.string.warning_delete_all)
                positiveButton(R.string.agree){
                    mExplosionField.explode(it.view)
                    viewModel.deleteAll()
                    toast=Toasty.info(dialogContext, getString(R.string.delete_all_success), Toast.LENGTH_LONG, true)
                    toast.setGravity(Gravity.BOTTOM,0,150)
                    toast.show()


                }
                negativeButton(R.string.cancel){
                    it.dismiss()
                }
            }        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[ArchiveViewModel::class.java]
        bindUi()
    }

    private fun bindUi() = viewLifecycleOwner.lifecycleScope.launch {
        val archivedArticles =viewModel.archivedArticles.await()

        archivedArticles.observe(viewLifecycleOwner, Observer {

            if(it.isEmpty()){
                    linearLayout.visibility=View.GONE
                }

            if(!it.isEmpty()){
                linearLayout.visibility=View.VISIBLE
            }

            if(it == null) return@Observer


            binding.recyclerViewArchive.apply {
                archiveAdapter = ArchiveAdapter(ArchiveListener() {
                        url,source ->  //viewModel.onTopHeadlineClicked(url)

                    val actionDetail = ArchiveFragmentDirections.actionArchiveFragmentToDetailFragment(url,source)
                    Navigation.findNavController(binding.root).navigate(actionDetail)


                }, ArchivearchiveListener {
                   // viewModel.desarchive(it)
                    val article =it
                    var toast: Toast
                    val dialogContext = binding.root.context

                    MaterialDialog(dialogContext).show {
                        title(R.string.warning)
                        message(R.string.warningdesarchive)
                        positiveButton(R.string.agree){
                            mExplosionField.explode(it.view)
                            viewModel.desarchive(article)
                            toast=Toasty.info(dialogContext, getString(R.string.desarchived), Toast.LENGTH_LONG, true)
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
                adapter = archiveAdapter
                archiveAdapter.submitList(it)

            }
        })

    }

    override fun onDestroyView() {
        if (::fab.isInitialized) {
            fab.setOnClickListener(null)
        }
        if (::tv.isInitialized) {
            tv.setOnClickListener(null)
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
