package com.asif.atomicbrowser;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    private WebView brow;
    private Button go, forward, back, refresh, clear, home;
    private EditText url_edit;
    private ProgressBar progress;

    private String homepage = "http://www.google.com";
    private boolean isClearDisabled = false;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        brow = (WebView)findViewById(R.id.wv_brow); //Initiates web view
        brow.setWebViewClient(new ourViewClient());
        brow.getSettings().setJavaScriptEnabled(true); //Enabling JavaScript
        brow.loadUrl(homepage); // Loading home page


        //Setting progress bar
        brow.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                progress.setProgress(newProgress);
                if (newProgress == 100)  progress.setVisibility(View.GONE);
                else                     progress.setVisibility(View.VISIBLE);
            }
        });



        brow.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                url_edit.setText(url);

                if (brow.canGoBack())    enableButton(back);
                else                     disableButton(back);

                if (brow.canGoForward()) enableButton(forward);
                else                     disableButton(forward);

                if (isClearDisabled){
                    enableButton(clear);
                    isClearDisabled = false;
                }

                //Take focus away from url_edit at startup in order to hide keyboard
                findViewById(R.id.addres_bar).requestFocus();

            }
        });



        // Initiating progress bar
        progress = (ProgressBar)findViewById(R.id.progress_bar);

        //Initiating Address Bar
        url_edit = (EditText)findViewById(R.id.edit_url);

        //Initiating buttons
        go       = (Button)findViewById(R.id.go_btn);
        forward  = (Button)findViewById(R.id.fwd_btn);
        back     = (Button)findViewById(R.id.back_btn);
        refresh  = (Button)findViewById(R.id.rfr_btn);
        clear    = (Button)findViewById(R.id.clr_btn);
        home     = (Button)findViewById(R.id.home_btn);

        goOnclickListener(go);
        forwardOnclickListener(forward);
        backOnclickListener(back);
        refreshOnclickListener(refresh);
        clearOnclickListener(clear);
        homeOnclickListener(home);

    }

    //Onclick listener for the "Go" button
    protected void goOnclickListener(Button go){
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

    //Onclick listener for the "Forward" button
    protected void forwardOnclickListener(final Button forward){
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

    //Onclick listener for the "Back" button
    protected void backOnclickListener(final Button back){
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

    //Onclick listener for the "Refresh" button
    protected void refreshOnclickListener(Button refresh){
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                view.startAnimation(buttonClick);
                brow.reload();
            }
        });
    }

    //Onclick listener for the "Clear" button
    protected void clearOnclickListener(final Button clear){
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

    //Onclick listener for the "Home" button
    protected void homeOnclickListener(final Button clear){
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                brow.loadUrl(homepage);
            }
        });
    }


    protected void disableButton(Button button){
        button.setTextColor(Color.parseColor("#d2d4d8"));
    }

    protected void enableButton(Button button){
        button.setTextColor(Color.parseColor("#ffffff"));
    }

}
