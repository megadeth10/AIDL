package com.example.aaaa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.preference.Preference;
import android.provider.MediaStore;
import android.util.Log;

public class CalcService extends Service {
	private MediaPlayer mPlayer = null;
	private ArrayList<MusicData> mList = null;
	private Random rand = new Random();
	private boolean isFirst = true;
	private boolean isPause = false;
	private IMediaTimeListener mediaTimeListener;
	
	private CountDownTimer mTimer = null;
	private SharedPreferences mPre = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mPre = getSharedPreferences(CalcService.class.getSimpleName(), MODE_PRIVATE);
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			public void onPrepared(final MediaPlayer mp) {
				if(mTimer != null){
					mTimer.cancel();
				}
				mTimer = new CountDownTimer(mPlayer.getDuration(), 500) {
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						try {
							mediaTimeListener.setTime(mPlayer.getCurrentPosition(), mPlayer.getDuration());
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							Log.i("service" , e.getMessage());
						}
					}
				};
				mp.start();
				mTimer.start();
			}
		});
		
		
		ContentResolver cr = getContentResolver();
		mList = new ArrayList<MusicData>();
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
		String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
		Cursor cur = cr.query(uri, null, selection, null, sortOrder);
		int count = 0;
		isFirst = true;
		isPause = false;

		if (cur != null) {
			count = cur.getCount();

			if (count > 0) {
				while (cur.moveToNext()) {

					String title = cur.getString(cur
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					int id = cur.getInt(cur
							.getColumnIndex(MediaStore.Audio.Media._ID));
					String d = cur.getString(cur
							.getColumnIndex(MediaStore.Audio.Media.DATA));
					// Add code to get more column here
					// Save to your list here
					MusicData data = new MusicData();
					data.setTitle(title);
					data.setId(id);
					data.setData(d);
					mList.add(data);
				}

			}
		}

		cur.close();

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPlayer.release();
		mPlayer = null;
		mTimer = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("service", "CalcService() : " + Process.myPid());
		return mBinder;
	}
	
	private final ICalc.Stub mBinder = new ICalc.Stub() {

		@Override
		public boolean isPrime(int n) throws RemoteException {
			// TODO Auto-generated method stub

			for (int i = 2; i < n; i++)
				if (n % i == 0)
					return false;
			return true;
		}

		@Override
		public int getLCM(int a, int b) throws RemoteException {
			// TODO Auto-generated method stub
			int i;
			for (i = 1;; i++)
				if ((i % a == 0) && (i % b == 0))
					break;
			return i;
		}

		@Override
		public int getTime() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List<MusicData> getList() throws RemoteException {
			// TODO Auto-generated method stub
			return mList;
		}

		@Override
		public void start(String data) throws RemoteException {
			// TODO Auto-generated method stub
			if (isPause) {
				mPlayer.start();
				isPause = false;
				return;
			}

			if (data != null && data.isEmpty()) {
				next();
				return;
			}

			Log.i("service", "start() : " + data);
			if (mPlayer.isPlaying())
				mPlayer.reset();
			
			try {
				mPlayer.setDataSource(data);
				mPlayer.prepareAsync();
				mediaTimeListener.setTime(0, mPlayer.getDuration());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void stop() throws RemoteException {
			// TODO Auto-generated method stub
			mTimer.cancel();
			mPlayer.seekTo(0);

			if(mPlayer.isPlaying())
				mPlayer.reset();
			mediaTimeListener.setTime(0, mPlayer.getDuration());
			isPause = false;
			isFirst = true;
		}

		@Override
		public void next() throws RemoteException {
			// TODO Auto-generated method stub
			start(mList.get(getRandom()).getData());
		}

		@Override
		public void prev() throws RemoteException {
			// TODO Auto-generated method stub
			start(mList.get(getRandom()).getData());
		}

		@Override
		public void pause() throws RemoteException {
			// TODO Auto-generated method stub
			if (!isPause) {
				isPause = true;
				mPlayer.pause();
				mTimer.cancel();
			}
		}

		@Override
		public void setListener(IMediaTimeListener listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			mediaTimeListener = listener;
		}

		@Override
		public void seekto(int mils) throws RemoteException {
			// TODO Auto-generated method stub
			if(mPlayer != null){
				mPlayer.seekTo(mils);
			}
		}

	};

	private int getRandom() {
		return rand.nextInt(((mList.size() - 1) - 0) + 1) + 0;
	}

	private String getFile(int id) {
		String ret = "";
		// ContentResolver cr = getContentResolver();
		// Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		// String selection = MediaStore.Audio.Media._ID + "=?";
		// String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
		// String[] selectionarg = new String[]{String.valueOf(id),};
		// Cursor cur = cr.query(uri, null, selection, selectionarg, sortOrder);
		// int count = 0;
		//
		// if(cur != null)
		// {
		// count = cur.getCount();
		//
		// if(count > 0)
		// {
		// String d =
		// cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
		// // Add code to get more column here
		// // Save to your list here
		// Log.i("service", "getFile() :" + ret);
		// ret = d;
		// }
		// }
		//
		// cur.close();

		if (mList.size() > 0) {

		}

		return ret;
	}
}
