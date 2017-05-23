package com.asif.atomicbrowser;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

/* ================================================================== */

public class MainActivity extends AppCompatActivity {


    private WebView brow;
    private Button go, forward, back, refresh, clear, home;
    private EditText url_edit;
    private ProgressBar progress;
    private String homepage = "http://www.google.com";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    private boolean isClearDisabled = false;
    final   Context context = this;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brow = (WebView)findViewById(R.id.wv_brow);  // Initiates web view
        viewSettings(brow);                          // Settings to improve WebView performance
        brow.loadUrl(homepage);                      // Loading home page

        // Initiating progress spinner
        progress = (ProgressBar)findViewById(R.id.progress_bar);
        initiateProgressSpinner(brow);

        // update buttons
        buttonUpdate(brow);

        // Initiating Address Bar
        url_edit = (EditText)findViewById(R.id.edit_url);

        // Initiating buttons
        go       = (Button)findViewById(R.id.go_btn);
        forward  = (Button)findViewById(R.id.fwd_btn);
        back     = (Button)findViewById(R.id.back_btn);
        refresh  = (Button)findViewById(R.id.rfr_btn);
        clear    = (Button)findViewById(R.id.clr_btn);
        home     = (Button)findViewById(R.id.home_btn);

        goOnClickListener(go);
        forwardOnClickListener(forward);
        backOnClickListener(back);
        refreshOnClickListener(refresh);
        clearOnClickListener(clear);
        homeOnClickListener(home);

    }



    /* ================================================================== */


    //Onclick listener for the "Go" button
    protected void goOnClickListener(Button go){
        go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String url = url_edit.getText().toString();

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    if (url.contains(".com"))
                        url = "http://" + url;
                    else
                        url = "http://google.com/search?q=" + url;
                }

                view.startAnimation(buttonClick);

                //hide keyboard after pressing Go
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(url_edit.getWindowToken(), 0);

                brow.loadUrl(url);
            }
        });
    }


    /* ================================================================== */

    //Onclick listener for the "Forward" button
    protected void forwardOnClickListener(final Button forward){
        forward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                view.startAnimation(buttonClick);
                if (brow.canGoForward()) {
                    brow.goForward();
                }
            }
        });
    }

    /* ================================================================== */

    //Onclick listener for the "Back" button
    protected void backOnClickListener(final Button back){
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                view.startAnimation(buttonClick);
                if (brow.canGoBack()) {
                    brow.goBack();
                }
            }
        });
    }

    /* ================================================================== */

    //Onclick listener for the "Refresh" button
    protected void refreshOnClickListener(Button refresh){
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                view.startAnimation(buttonClick);
                brow.reload();
            }
        });
    }

    /* ================================================================== */

    //Onclick listener for the "Clear" button
    protected void clearOnClickListener(final Button clear){
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                view.startAnimation(buttonClick);

                //Confirm deleting history
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to clear browsing history?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        brow.clearHistory();
                        brow.clearCache(true);
                        disableButton(clear);
                        disableButton(forward);
                        disableButton(back);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });

                builder.create().show();
                isClearDisabled = true;

            }
        });
    }


    /* ================================================================== */


    //Onclick listener for the "Home" button
    protected void homeOnClickListener(final Button clear){
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                brow.loadUrl(homepage);
            }
        });
    }


    /* ================================================================== */

    protected void disableButton(Button button){
        button.setTextColor(Color.parseColor("#d2d4d8"));
    }

    protected void enableButton(Button button){
        button.setTextColor(Color.parseColor("#ffffff"));
    }


    /* ================================================================== */


    // Settings to improve web view performance
    protected void viewSettings(WebView v){

        @SuppressWarnings("deprecation")
        WebSettings settings = v.getSettings();
        settings.setJavaScriptEnabled(true); //Enabling JavaScript
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDomStorageEnabled(true);
        v.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        v.setScrollbarFadingEnabled(true);

        /*
        if Android version is newer than Kitkat, set web view layer to hardware
        */
        if (Build.VERSION.SDK_INT >= 19)
            brow.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            brow.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /* ================================================================== */

    protected void initiateProgressSpinner(WebView v){
        v.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                progress.setProgress(newProgress);

                if (newProgress == 100)
                    progress.setVisibility(View.GONE);
                else
                    progress.setVisibility(View.VISIBLE);
            }
        });
    }


    /* ================================================================== */


    // updating button visibility on page load
    protected void buttonUpdate(final WebView v){
        v.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                url_edit.setText(url);

                if (v.canGoBack())
                    enableButton(back);
                else
                    disableButton(back);

                if (v.canGoForward())
                    enableButton(forward);
                else
                    disableButton(forward);

                if (isClearDisabled){
                    enableButton(clear);
                    isClearDisabled = false;
                }

                // Take focus away from url_edit at startup to hide keyboard
                findViewById(R.id.addres_bar).requestFocus();

            }
        });
    }
}
