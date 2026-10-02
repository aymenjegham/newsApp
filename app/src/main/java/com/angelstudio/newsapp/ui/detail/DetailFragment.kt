package com.angelstudio.newsapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.databinding.FragmentDetailBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.dmoral.toasty.Toasty


class DetailFragment : Fragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel
    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding ?: error("DetailFragment binding is only valid between onCreateView and onDestroyView")
    private lateinit var fab: FloatingActionButton
    private lateinit var linearLayout: LinearLayout





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val safeArgs = arguments?.let { DetailFragmentArgs.fromBundle(it) }
        val url = safeArgs?.urlArg
        val source = safeArgs?.source

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = source

        fab = (activity as? AppCompatActivity)!!.findViewById(R.id.floatingActionButton)
        fab.visibility= View.VISIBLE

        linearLayout = (activity as? AppCompatActivity)!!.findViewById(R.id.tvdeleteall)
        linearLayout.visibility=View.GONE

        fab.setOnClickListener { v: View? ->
            binding.scrollviewdetail.scrollTo(0,0)
        }


        val webView = binding.webview
        val progressBar = binding.progressBar
        fun loadArticleUrl(target: WebView, articleUrl: String?) {
            if (articleUrl != null && URLUtil.isValidUrl(articleUrl)) {
                target.loadUrl(articleUrl)
            } else {
                progressBar.visibility = View.GONE
                Toasty.error(target.context, "Invalid article URL", Toast.LENGTH_LONG, true).show()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressBar.progress = progress

            }
        }


        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, _url: String?): Boolean {
                loadArticleUrl(view ?: webView, _url)
                return true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
             }
        }
        loadArticleUrl(webView, url)


    }

    override fun onDestroyView() {
        if (::fab.isInitialized) {
            fab.setOnClickListener(null)
        }
        _binding?.webview?.apply {
            stopLoading()
            webChromeClient = null
            webViewClient = WebViewClient()
        }
        _binding = null
        super.onDestroyView()
    }

}
