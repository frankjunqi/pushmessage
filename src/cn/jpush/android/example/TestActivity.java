package cn.jpush.android.example;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.text.Html;
import android.text.Html.ImageGetter;


public class TestActivity extends Activity {
	ImageGetter imgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			drawable = Drawable.createFromPath(source);
			drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
			return drawable;
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先将参数放入List，再对参数进行URL编码
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("param1", "中国"));
        params.add(new BasicNameValuePair("param2", "value2"));

        //对参数编码
        String param = URLEncodedUtils.format(params, "UTF-8");

        //baseUrl			
        String baseUrl = "http://www.baidu.com";

        //将URL与参数拼接
        HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
        			
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(getMethod); //发起GET请求
            TextView tv = new TextView(this);
            Bundle extra = new Bundle();  
            extra = getIntent().getExtras();  
            //String id =  extra.getString("id"); 
            
            tv.setText(Html.fromHtml("<b>text3:</b><span style='color:red;'>dfff</span>"));
            //tv.setText(EntityUtils.toString(response.getEntity(), "utf-8"));
            
            addContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
           // Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); //获取响应码
            //Log.i(TAG, "result = " + EntityUtils.toString(response.getEntity(), "utf-8"));//获取服务器响应内容
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
       
    }

}
