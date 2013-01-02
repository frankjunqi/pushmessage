package cn.jpush.android.example;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import com.ycao.message.R;
public class DropDownFreshActivity extends Activity {
	private DropDownToRefreshListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listView = (DropDownToRefreshListView) findViewById(R.id.mylistview);
		
		List<String> list = initData();
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(DropDownFreshActivity.this, android.R.layout.simple_expandable_list_item_1, list);
		listView.setAdapter(adapter);	
		listView.setOnRefreshListener(new DropDownToRefreshListView.OnRefreshListener() {
			
			public void onRefresh() {
				
				//do some thing...
				new Handler().postDelayed(new Runnable() {
					
					public void run() {
						listView.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	private List<String> initData() {
		List<String> list = new ArrayList<String>();
		for(int i = 0; i <= 20; i++){
			list.add(new String("张三"+i));
		}
		return list;
	}

}
