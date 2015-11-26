package com.example.aaaa;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicData implements Parcelable{
	private String title;
	private int id;
	private String data;
	
	public MusicData() {
		// TODO Auto-generated constructor stub
	}
	
	public MusicData(MusicData d) {
		// TODO Auto-generated constructor stub
		setId(d.getId());
		setTitle(d.getTitle());
		setData(d.getData());
	}
	
	public MusicData(Parcel read) {
		// TODO Auto-generated constructor stub
		setTitle(read.readString());
		setId(read.readInt());
		setData(read.readString());
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(getTitle());
		dest.writeInt(getId());
		dest.writeString(getData());
	}
	
	public final static Parcelable.Creator<MusicData> CREATOR = new Creator<MusicData>() {
		@Override
		public MusicData createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new MusicData(source);
		}
		
		@Override
		public MusicData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MusicData[size];
		}
	};
	
}
