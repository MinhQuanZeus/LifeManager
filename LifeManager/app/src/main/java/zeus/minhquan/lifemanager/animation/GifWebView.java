package zeus.minhquan.lifemanager.animation;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by EDGY on 5/4/2017.
 */

public class GifWebView extends WebView {

    public GifWebView(Context context, String path) {
        super(context);

        loadUrl(path);
    }
}