package ru.kingmovies.km

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import ru.kingmovies.km.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var savedInstanceState: Bundle? = null
    private val URL = "https://kinmov.ru/"

    private var errorCode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        this.savedInstanceState = savedInstanceState

        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState.getBundle("webViewState")!!)
        }

        with(binding) {
            button.setOnClickListener {
                errorCode = null
                errorWrapper.visibility = View.GONE
                ivLogo.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                webView.visibility = View.GONE
                webView.url?.let { webView.loadUrl(it) }
            }

            webView.loadUrl(URL)
            webView.webViewClient = object : WebViewClient() {


                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    ivLogo.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    if (errorCode == null)webView.visibility = View.VISIBLE
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url: String = request?.url.toString()
                    view?.loadUrl(url)
                    return true
                }

                override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                    webView.loadUrl(url)
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    val host = request?.url?.host
                    println("Произошла ошибка на $host")
                    if (host == "kinmov.ru"){
                        errorWrapper.visibility = View.VISIBLE
                        ivLogo.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        webView.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        binding.webView.saveState(bundle)
        outState.putBundle("webViewState", bundle)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        else
            super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
        binding.webView.pauseTimers()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
        binding.webView.resumeTimers()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }
}