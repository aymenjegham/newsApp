package com.angelstudio.newsapp.ui.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.angelstudio.newsapp.databinding.FragmentDetailBinding
import kotlinx.android.synthetic.main.fragment_detail.*
import android.webkit.WebChromeClient
import android.widget.LinearLayout
import com.angelstudio.newsapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.archive_fragment.*


class DetailFragment : Fragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel
    private lateinit var binding : FragmentDetailBinding
    private lateinit var myView :View
    private lateinit var fab: FloatingActionButton
    private lateinit var linearLayout: LinearLayout





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       // return inflater.inflate(R.layout.fragment_detail, container, false)

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_detail,container , false)
        myView= binding.root

        return myView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val safeArgs = arguments?.let { DetailFragmentArgs.fromBundle(it) }
        val url = safeArgs?.urlArg
        val source = safeArgs?.source

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = source

        fab = (activity as? AppCompatActivity)!!.findViewById(R.id.floatingActionButton)
        fab.visibility= View.VISIBLE

        linearLayout = (activity as? AppCompatActivity)!!.findViewById(R.id.tvdeleteall)
        linearLayout.visibility=View.GONE

        fab.setOnClickListener { v: View? ->
            scrollviewdetail.scrollTo(0,0)
        }


        webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                binding.progressBar.setProgress(progress);

            }
        })


        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, _url: String?): Boolean {
                view?.loadUrl(_url)
                return true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.setVisibility(View.GONE);
             }
        }
        webview.loadUrl(url)


    }


}
