package com.example.skylark;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.EncodingUtils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.WindowManager;

public class MonitorService extends Service{
	private int hour;
	private int min;
	private Timer timer;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timer.cancel();
		Log.v("myservice","stop");
		timer.cancel();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		String blName=""+intent.getStringExtra("blName");
		String snsName=""+intent.getStringExtra("snsName");
		hour=intent.getIntExtra("hour", 0);
		min=intent.getIntExtra("min", 0);
		timer=new Timer();
		final String appNames=getAppNames(blName);
		//showNotification(R.drawable.icon,"name","name1","name2");
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Time t=new Time();
				t.setToNow();
				//((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();
				//showNotification(R.drawable.icon, "name", "name1", ""+t.second);
				//Log.v("mytime", ""+t.second);
				if(timeover())
				{
					success();
					
				}
				if(!monitor(appNames))
				{
					fail(appNames);
				}
			}
		}, 0,1000);
		
	}
	boolean timeover()
	{
		Time t=new Time();
		t.setToNow();
		int nowHour=t.hour;
		int nowMin=t.minute;
		if(nowHour>hour) return true;
		if(nowHour<hour) return false;
		if(nowMin>=min) return true;
		return false;
	}
	String getAppNames(String blName)
	{
		String appNames="";
    	FileInputStream fin;
		try {
			fin = openFileInput(blName+"bl");
			int length = fin.available();   
	        byte [] buffer = new byte[length];   
	        fin.read(buffer);       
	        appNames = ""+EncodingUtils.getString(buffer, "UTF-8");
	        fin.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appNames;
	}
	/*
	 * 用于判断当前Plan的执行情况。
	 */
	boolean monitor(String appNames)
	{
		ActivityManager am=(ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		//List<ActivityManager.RunningAppProcessInfo> runningProcess=am.getRunningAppProcesses();
		List<ActivityManager.RunningTaskInfo> runningProcess=am.getRunningTasks(1);
		String packageName="";
		/*
		for(int i=0;i<runningProcess.size();i++)
		{
			//Log.v("myservice",runningProcess.get(i).processName);
			packageName=runningProcess.get(i).topActivity.getPackageName();
			//Log.v("myservice","+"+this.getPackageManager().getApplicationInfo(processName, 0).loadLabel(this.getP).toString());
			if(appNames.contains(packageName))
			{
				fail(packageName);
				//showNotification(R.drawable.icon, "Skylark", "计划失败！！", "被禁应用："+processName);
				//Toast.makeText(getApplicationContext(), "fail"+" "+processName, Toast.LENGTH_LONG).show();
				//am.killBackgroundProcesses(packageName);
				return false;
			}
		}
		*/
		packageName=runningProcess.get(0).topActivity.getPackageName();
		if(appNames.contains(packageName))
		{
			fail(packageName);
			//showNotification(R.drawable.icon, "Skylark", "计划失败！！", "被禁应用："+processName);
			//Toast.makeText(getApplicationContext(), "fail"+" "+processName, Toast.LENGTH_LONG).show();
			am.killBackgroundProcesses(packageName);
			
			return false;
		}
		//Log.v("myservice",packageName);
		return true;
	}
	
	/*
	 * 用于在用户触犯规则时，杀死进程，当前先实现提醒和阻碍
	 * 若成功则返回真，否则返回假
	 */
	boolean killProcess(String processName)
	{
		
		return true;
	}
	
	/*
	 * 用于在SNS上发布状态，使用的是泽辰提供的API
	 * 若成功则返回真，否则返回假
	 */
	boolean publishAtSNS()
	{
		return true;
	}
	
	void success()
	{
		Log.v("myservice","success");
		timer.cancel();
		stopService(new Intent("com.example.skylark.monitorservice"));
	}
	void fail(String packageName)
	{
		timer.cancel();
		stopService(new Intent("com.example.skylark.monitorservice"));
		Log.v("myservice","fail");
    	PackageManager pm=this.getPackageManager();
    	try {
    		showNotification(R.drawable.icon, "Skylark", "计划失败！！", 
    				"被禁应用："+pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString());
			//appNames[i]=pm.getApplicationInfo(name, 0).loadLabel(pm).toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Intent intent=new Intent(Intent.ACTION_VIEW);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.putExtra("hour", hour);
    	intent.putExtra("min", min);
    	intent.putExtra("appName", packageName);
    	intent.setClass(MonitorService.this, WhenFail.class);
    	startActivity(intent);
    	//new AlertDialog.Builder(MonitorService.this)
	//	.setTitle("删除所选的黑名单")
		//.setMessage("确定删除吗？")
		//.setPositiveButton("是", null)
		//.setNegativeButton("否", null)
		//.show();
    	//showDialog();
	}

	public void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MonitorService.this);
		builder.setTitle("Test!");
		builder.setMessage("Close!");
		builder.setPositiveButton("離開", null);
		builder.setNegativeButton("關掉鈴聲", null);
		AlertDialog alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//
		alert.show();
	}
	
	/*
	 * 显示notification
	 */
    public void showNotification(int icon,String tickerText,String title,String content)
    {
    	NotificationManager nm;
    	nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	Notification notification=new Notification(icon, tickerText, System.currentTimeMillis());
    	notification.defaults=Notification.DEFAULT_ALL;
    	Intent intent= new Intent(MonitorService.this,MainActivity.class);
    	//intent.setAction(Intent.ACTION_MAIN);
    	//intent.addCategory(Intent.CATEGORY_LAUNCHER);
    	PendingIntent pt=PendingIntent.getActivity(MonitorService.this, 0,intent,0);
    	notification.setLatestEventInfo(MonitorService.this, title, content,pt);
    	nm.notify(0,notification);
    	//Toast.makeText(MonitorService.this, "asdf", Toast.LENGTH_LONG).show();
    }
}