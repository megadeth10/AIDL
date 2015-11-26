package com.example.aaaa;
import com.example.aaaa.MusicData;
import com.example.aaaa.IMediaTimeListener;

interface ICalc{
	int getLCM(int a, int b);
	boolean isPrime(int n);
	int getTime();
	List<MusicData> getList();
	void start(String id);
	void stop();
	void next();
	void prev();
	void pause();
	void seekto(int mils);
	void setListener(IMediaTimeListener listener);
}