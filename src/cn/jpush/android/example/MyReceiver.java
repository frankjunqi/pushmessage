package cn.jpush.android.example;



import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";
	public  final static String SER_KEY = "com.yue.pushmessage";  
    public  final static String PAR_KEY = "com.yue.pushmessage";  
	public void Alert(Context context,String msg){
		Toast toast = Toast.makeText(context,msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout tsv = (LinearLayout) toast.getView();
        ImageView iv = new ImageView(context);
        iv.setImageResource(R.drawable.ic_dialog_alert);
        tsv.addView(iv,0);
        toast.show();
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
       // abortBroadcast(); 
		//Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	Bundle bundle1 = new Bundle();
           
    		String cmd =  bundle.getString("cn.jpush.android.MESSAGE");
			 Person mPerson = new Person();  
			 mPerson.setMethod("cmd");  
			 mPerson.setArgs(cmd);  
			 bundle1.putSerializable(SER_KEY,mPerson);
        	Intent i = new Intent(context, MainActivity.class);
        	i.putExtras(bundle1);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	SharedPreferences preferences =  context.getSharedPreferences("mypush", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("lastCmd",cmd);
			editor.commit();
			
        	context.startActivity(i);
        	
        	
     

        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
            
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
        	//打开自定义的Activity
            Bundle bundle1 = new Bundle();
            StringBuilder sb = new StringBuilder();
    		for (String key : bundle.keySet()) {
    			sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
    		}
    		String id = "";
    		try{
    			JSONObject jsonObject = new JSONObject(bundle.getString("cn.jpush.android.EXTRA")); 
    			id = jsonObject.getString("id");
    			
    		} catch (JSONException ex) { 
    			
    			
    		}
    		
    		sb.append("\n"+id);
            bundle1.putString("content", sb.toString());
            bundle1.putString("id", id);
            Person mPerson = new Person();  
            mPerson.setMethod("id");  
            mPerson.setArgs(id);  
            bundle1.putSerializable(SER_KEY,mPerson);
        	Intent i = new Intent(context, MainActivity.class);
        	
        	i.putExtras(bundle1);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	SharedPreferences preferences =  context.getSharedPreferences("mypush", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("lastId",id);
			editor.commit();
			
        	context.startActivity(i);
 	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
	
	// 打印所有的 intent extra 数据
	public static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
		}
		return sb.toString();
	}
	
	
    
     
}
