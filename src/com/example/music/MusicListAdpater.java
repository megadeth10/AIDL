package com.example.music;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aaaa.MusicData;
import com.example.aaaa.R;

public class MusicListAdpater extends BaseAdapter {
	private List<MusicData> mList = null;
	private LayoutInflater mInflater = null;
	
	public MusicListAdpater(Context ctx) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(ctx);
		mList = new ArrayList<MusicData>();
	}
	
	public void setList(List<MusicData> list){
		mList = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public MusicData getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.layout_list_item, null);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder == null){
			holder = new ViewHolder();
			holder.view = (TextView) convertView.findViewById(R.id.Title);
		}
		
		MusicData data = getItem(position);
		
		holder.view.setText(data.getTitle());
		holder.data = data;
		convertView.setTag(holder);
		
		return convertView;
	}
	
	public class ViewHolder{
		TextView view;
		MusicData data;
	}

}
