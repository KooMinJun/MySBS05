package my.kmucs.com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;


//웹에서 안드로이드 건드린것
public class MainActivity extends Activity {
    WebView webView;
    Intent i;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.webview);

        loadWeb("http://192.168.27.6:8080/practice.jsp");
        i = new Intent(this, NewintentActivity.class);
    }

    private void loadWeb(String url){
        webView = (WebView)findViewById(R.id.webview);
        webView.clearCache(true); //cache 다 버린다

        //javascript 돌아가게 설정만해놓는데 이것만있으면안됨, 아래줄도필요함
        webView.getSettings().setJavaScriptEnabled(true);

        //android에서 javascript언어가 돌아야하는데 그 규약을 sbsac라는 규약으로 정해줌
        //AndroidHandler안에 javascript에서 쓰려고했던 sbsac.setMsg함수가 들어가있따.
        webView.addJavascriptInterface(new AndroidHandler(), "sbsac"); //javascript와 통신하기위해 만들어져있는 함수

        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl(url);
        webView.setHorizontalScrollBarEnabled(false); //스크롤바 삭제
        webView.setVerticalScrollBarEnabled(false); //스크롤바 삭제

    }

    public void confirmMsg(){
        //alert dialog 창 만들기
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("종료하시겠습니까?").setCancelable(false).setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }


    public class AndroidHandler{
        @JavascriptInterface
        public void setMsg(final String args){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("SBSWEB", "setMSG("+ args +")");
                    String msg = args;
                    if(args.equalsIgnoreCase("exit")){
                        //// TODO: 2016-11-26  
                        confirmMsg();
                    }
                }
            });
        }

        @JavascriptInterface
        public void toastShort(final String msg){
            Log.d("SBSWEB", msg);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void newIntent(){
            Log.d("SBSWEB", "new intent");
            startActivity(i);
        }
    }
}
