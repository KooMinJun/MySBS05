package my.kmucs.com.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText et_userId, et_userPw;
    Button btnSave;
    String uId, uPw;
    LoadJsp task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_userId = (EditText)findViewById(R.id.ed_userId);
        et_userPw = (EditText)findViewById(R.id.ed_userPw);
        btnSave = (Button)findViewById(R.id.btnSave);

        //btn 클릭시
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uId = et_userId.getText().toString();
                uPw = et_userPw.getText().toString();

                task = new LoadJsp();
                task.execute(); //실행되라!

            }
        });//oncreate end
    }


    //http에 접속?!
    class LoadJsp extends AsyncTask<Void, String, Void>{

        @Override
        protected Void doInBackground(Void... param) {
            try{
                HttpClient client = new DefaultHttpClient(); //httpclient쓰려면 gradle에 라이브러리 추가해줘야함.
                String loadUrl = "http://192.168.27.6:8080/getData.jsp"; //본인의 jsp정보

                HttpPost post = new HttpPost(loadUrl);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(); //값을 넘기기 위해 쓰는 것.

                //params에 값 담기
                params.add(new BasicNameValuePair("userId", uId)); //userId에 uId를 준다.
                params.add(new BasicNameValuePair("userPw", uPw)); //userPw에 uPw를 준다.

                //인코더시키기
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);//params를 utf-8로 인코딩시켜서 보낼것이다.
                post.setEntity(ent); //post방식으로한다

                HttpResponse responsePost = client.execute(post); //위에담겨있는것을 포스트방식으로 실행해라.
                HttpEntity resEntity = responsePost.getEntity();
                if(resEntity != null){
                    Log.d("Response", EntityUtils.toString(resEntity)); //응답했을때 동작하는것.
                }


            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }//class end
}
