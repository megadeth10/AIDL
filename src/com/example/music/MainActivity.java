package com.example.music;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.aaaa.ICalc;
import com.example.aaaa.IMediaTimeListener;
import com.example.aaaa.MusicData;
import com.example.aaaa.R;
import com.example.music.MusicListAdpater.ViewHolder;

public class MainActivity extends Activity implements OnClickListener, 
						OnItemClickListener{
	boolean isTouched = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.Call_Service).setOnClickListener(this);
        ((ListView)findViewById(R.id.List)).setAdapter(new MusicListAdpater(this));
        ((ListView)findViewById(R.id.List)).setOnItemClickListener(this);
        findViewById(R.id.Next).setOnClickListener(this);
        findViewById(R.id.Preview).setOnClickListener(this);
        findViewById(R.id.StartAndPause).setOnClickListener(this);
        findViewById(R.id.Stop).setOnClickListener(this);
        SeekBar sbar = (SeekBar) findViewById(R.id.Music_bar);
        sbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if(mICalc != null){
					try {
						mICalc.seekto(seekBar.getProgress());
						isTouched = false;
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						Log.i("service" , e.getMessage());
					}
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isTouched = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
			}
		});
        ((TextView)findViewById(R.id.Time_View)).setText("00:00");
        bindService(new Intent("com.example.aaaa.CalcService"), 
        		mSerConnection, Context.BIND_AUTO_CREATE);
//        startService(new Intent("itmir.tistory.examplewindowview.AlwaysTopServiceTouch"));
        
    }
    private ICalc mICalc = null;
	private ServiceConnection mSerConnection =  new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			try {
				mICalc.stop();
				mICalc.setListener(null);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.i("service" , "onServiceDisconnected() : " + e.getMessage());
			}
			mICalc = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i("service" , "onServiceConnected() : " + android.os.Process.myPid());
			setConnect(service);
		}
	};
	
	private void setConnect(IBinder service){
		try {
			mICalc = ICalc.Stub.asInterface(service);
			mICalc.setListener(mediaTimeListener);
			Toast.makeText(getApplicationContext(), "Bind", Toast.LENGTH_LONG).show();
			setList();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.e("ERROR", e.getMessage());
		}

	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	unbindService(mSerConnection);
    	super.onDestroy();
    }
    
    protected void setList() {
		// TODO Auto-generated method stub
    	MusicListAdpater adapter = (MusicListAdpater) ((ListView)findViewById(R.id.List)).getAdapter();
    	
    	try {
			adapter.setList(mICalc.getList());
			adapter.notifyDataSetChanged();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.e("ERROR", e.getMessage());
		}
	}

	@Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	finish();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try {
			switch(v.getId()){
				case R.id.Call_Service:
//					if(mICalc != null)
//						setList();
//					checkData();
					setContentView(R.layout.activity_sub);
					break;
				case R.id.Next:
					mICalc.next();
					break;
				case R.id.Preview:
					mICalc.prev();
					break;
				case R.id.Stop:
					((ToggleButton)findViewById(R.id.StartAndPause)).setChecked(true);
					mICalc.stop();
					break;
				case R.id.StartAndPause:
					if(((ToggleButton)v).isChecked()){//start
						mICalc.pause();
					}else{
						mICalc.start("");
					}
					break;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.i("service" , e.getMessage());
		}
	}
	
	private void checkData(){
		MusicListAdpater adapter = (MusicListAdpater) ((ListView)findViewById(R.id.List)).getAdapter();
		MusicData data = adapter.getItem(0);
		
		int id = data.getId();
		
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media._ID + " =?";
		String sordOrder = MediaStore.Audio.Media._ID + " ASC";
		Cursor c = getContentResolver().query(uri, 
				null, selection, new String[]{String.valueOf(id),}, sordOrder);
		
		if(c != null){
			if(c.getCount() > 0){
				while(c.moveToNext()){
					Log.i("service", "checkData() find music : " + 
				c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)));
				}
			}else{
				Log.i("service", "checkData() not found.");
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		MusicListAdpater.ViewHolder obj = (ViewHolder) view.getTag();
		if(obj != null){
			MusicData data = (MusicData) obj.data;
			
			try {
				mICalc.start(data.getData());
				((ToggleButton)findViewById(R.id.StartAndPause)).setChecked(false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.i("service" , e.getMessage());
			}
		}
	}
	
	IMediaTimeListener mediaTimeListener = new IMediaTimeListener.Stub() {
		
		@Override
		public void setTime(int miles, int total) throws RemoteException {
			// TODO Auto-generated method stub
			Log.i("service" , "setTime: " + miles + " total : " + total);
			if(!isTouched){
				Date date = new Date();
				date.setTime(miles);
				SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.time_init));
				
				((TextView)findViewById(R.id.Time_View)).setText(sdf.format(date));
				SeekBar sbar = (SeekBar) findViewById(R.id.Music_bar);
				sbar.setMax(total);
				sbar.setProgress(miles);
			}
		}
	};
}
