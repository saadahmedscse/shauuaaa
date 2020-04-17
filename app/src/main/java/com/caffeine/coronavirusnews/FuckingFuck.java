package com.caffeine.coronavirusnews;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FuckingFuck extends WebViewClient {

    String URL;

    public FuckingFuck(String URL) {
        this.URL = URL;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean valid = true;
        if (Uri.parse(url).getHost().equals(URL)) {
            // This is my web site, so do not override; let my WebView load the page
            valid = false;
        }
        return valid;
    }

}
