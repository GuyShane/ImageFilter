package com.imagefilter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	private int windowSize;
	private SharedPreferences prefs;
	private String filterType;
	private Filter filter;
	private ProgressBar image_progress;
	private ImageView picture;
	private Button apply;
	private Bitmap filtered;
	private boolean processing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		processing=false;

		apply=(Button) findViewById(R.id.button_apply);
		image_progress=(ProgressBar) findViewById(R.id.progress_image);
		picture=(ImageView) findViewById(R.id.image_view);
		
		apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//need to be able to load new images
				if (!processing) {
					doFilter();
				}
			}
		});
	}
	
	private void doFilter() {
		AsyncFilter task=new AsyncFilter();
		task.execute();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		prefs=this.getSharedPreferences("prefs", MODE_PRIVATE);
		filterType=prefs.getString("filterType", "median");
		if (filterType.equals("median")) {
			filter=new MedianFilter();
		}
		else if (filterType.equals("mean")) {
			filter=new MeanFilter();
		}
	}
	
	private class AsyncFilter extends AsyncTask<Void,Void,Void> {
		@Override
		protected void onPreExecute() {
			processing=true;
			image_progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			windowSize=prefs.getInt("windowSize", 3);
			picture.buildDrawingCache();
			Bitmap image=picture.getDrawingCache();
			filtered=filter.applyFilter(image, windowSize);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			processing=false;
			image_progress.setVisibility(View.GONE);
			picture.setImageBitmap(filtered);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i=new Intent(MainActivity.this,SettingsActivity.class);
			startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
