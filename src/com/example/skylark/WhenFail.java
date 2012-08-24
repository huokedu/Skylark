package com.example.skylark;

import java.util.HashMap;

import com.example.ui.MyApplication;
import com.umeng.api.common.SnsParams;
import com.umeng.api.exp.UMSNSException;
import com.umeng.api.sns.UMSnsService;
import com.umeng.api.sns.UMSnsService.SHARE_TO;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WhenFail extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whenfail);
		setTitle("计划失败！");
		TextView text=(TextView)findViewById(R.id.messageWhenFail);
		Button button=(Button)findViewById(R.id.ok);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();
				try {
					publishInSNS();
				} catch (UMSNSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		text.setText(getHowLong());
		
	}
	public String getHowLong()
	{
		String ans="";
		Time t=new Time();
		t.setToNow();
		int hour=getIntent().getIntExtra("hour", 0)-t.hour;
		int min=getIntent().getIntExtra("min", 0)-t.minute;
		if(min<0)
		{
			min+=60;
			hour--;
		}
		if(hour>0)
		{
			ans=ans+hour+"小时";
		}
		if(min>0)
		{
			ans=ans+min+"分钟";
		}
		ans="计划失败，距完成计划还有"+ans;
		return ans;
	}
	public void publishInSNS() throws UMSNSException
	{
		final String snsName=getIntent().getStringExtra("snsName");
		if(snsName.equals(""))
		{
			return;
		}
		HashMap<String, SHARE_TO> Name=new HashMap<String, UMSnsService.SHARE_TO>();
		Name.put("renren", SHARE_TO.RENR);
		Name.put("tencent", SHARE_TO.TENC);
		Name.put("sina",SHARE_TO.SINA);
		if(!snsName.equals("") && !UMSnsService.isAuthorized(MyApplication.getInstance(), Name.get(snsName)))
		{
			UMSnsService.OauthCallbackListener listener = new UMSnsService.OauthCallbackListener(){
		        public void onComplete(Bundle value, SHARE_TO platform) {
		        	publish(snsName);
		        }
		        public void onError(UMSNSException e, SHARE_TO platform) {
		        	Toast.makeText(WhenFail.this, "对不起，绑定失败，请检查网络设置", Toast.LENGTH_LONG).show();
		        	//
		        }
			};
			
			Log.v("my","fore");
			if(snsName.equals("renren"))
			{
				//Toast.makeText(context, "asdf", Toast.LENGTH_LONG).show();
				UMSnsService.oauthRenr(this, listener);
				Log.v("my","renren");
			}
			if(snsName.equals("tencent"))
			{
				UMSnsService.oauthTenc(this, listener);
			}
			if(snsName.equals("sina"))
			{
				UMSnsService.oauthSina(this, listener);
			}
			return ;
		}
		publish(snsName);
		
	//	UMSnsService.share(this, getHowLong());
		/*
	    UMSnsService.OauthCallbackListener listener = new UMSnsService.OauthCallbackListener(){
	        public void onComplete(Bundle value, SHARE_TO platform) {
	        	//Toast.
	        }
	        public void onError(UMSNSException e, SHARE_TO platform) {
	   
	        }
		};
		UMSnsService.oauthSina(this, listener);
*/
	}
	public void publish(String snsName)
	{
		if(snsName.equals("renren"))
		{
			UMSnsService.shareToRenr(this, getHowLong(), null);
		}
		if(snsName.equals("tencent"))
		{
			UMSnsService.shareToTenc(this, getHowLong(), null);
		}
		if(snsName.equals("sina"))
		{
			UMSnsService.shareToSina(this, getHowLong(), null);
		}
	}
}
