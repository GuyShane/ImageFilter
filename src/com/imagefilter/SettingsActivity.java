package com.imagefilter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {
	
	private SharedPreferences prefs;
	private int windowSize;
	private int barProgress;
	private String filterType;
	private SeekBar filter_slide;
	private TextView text_filter_size;
	private ToggleButton median;
	private ToggleButton mean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		prefs=this.getSharedPreferences("prefs", MODE_PRIVATE);
		
		median=(ToggleButton) findViewById(R.id.button_median);
		mean=(ToggleButton) findViewById(R.id.button_mean);
		
		filter_slide=(SeekBar) findViewById(R.id.seek_filter_size);
		text_filter_size=(TextView) findViewById(R.id.text_filter_size);
		
		setup();
		
		median.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				toggleMean(isChecked);
			}
		});
		
		mean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				toggleMedian(isChecked);
			}
		});
		
		filter_slide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					updateSlider(progress);
				}
			}
		});
		
	}
	
	private void updateSlider(int progress) {
		windowSize=(2*progress+3);
		text_filter_size.setText("Filter size: "+windowSize);
		putInPrefs("windowSize", windowSize);
	}
	
	private void putInPrefs(String key, int value) {
		SharedPreferences.Editor editor=prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	private void putInPrefs(String key, String value) {
		SharedPreferences.Editor editor=prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private void setup() {
		windowSize=prefs.getInt("windowSize", 3);
		filterType=prefs.getString("filterType", "median");
		if (filterType.equals("median")){
			//toggleMedian(false);
			buttonSetup(median, mean);
		}
		else if (filterType.equals("mean")){
			//toggleMean(false);
			buttonSetup(mean, median);
		}
		barProgress=(windowSize-3)/2;
		filter_slide.setProgress(barProgress);
		text_filter_size.setText("Filter size: "+windowSize);
	}
	
	private void buttonSetup(CompoundButton on, CompoundButton off) {
		on.setChecked(true);
		off.setChecked(false);
		on.setAlpha(1.0f);
		off.setAlpha(0.2f);
	}
	
	private void toggleMean(boolean isChecked) {
		mean.setChecked(!isChecked);
		if (isChecked) {
			mean.setAlpha(0.2f);
			putInPrefs("filterType", "median");
		}
		else {
			mean.setAlpha(1.0f);
			putInPrefs("filterType", "mean");
		}
	}
	
	private void toggleMedian(boolean isChecked) {
		median.setChecked(!isChecked);
		if (isChecked) {
			median.setAlpha(0.2f);
			putInPrefs("filterType", "mean");
		}
		else {
			median.setAlpha(1.0f);
			putInPrefs("filterType", "median");
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		prefs=this.getSharedPreferences("prefs", MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_main) {
			Intent i=new Intent(SettingsActivity.this,MainActivity.class);
			startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
