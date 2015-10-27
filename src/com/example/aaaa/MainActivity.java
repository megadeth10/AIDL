package com.example.aaaa;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	MenuInflater inflate = getMenuInflater();
    	inflate.inflate(R.menu.options, menu);
    	
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
		case R.id.action_one:{
				TextView tv = (TextView)findViewById(R.id.textview);
				tv.append(getString(R.string.hello_world));
				
				return true;
			}
		case R.id.action_two:{
				TextView tv = (TextView)findViewById(R.id.textview);
				tv.setText(getString(R.string.hello_world));
				return true;
			}
		}
    	
    	return false;
    }
}
