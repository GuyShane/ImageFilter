package com.imagefilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	private static final int PIC_LOADED=1;
	
	private int windowSize;
	private int max_height;
	private int max_width;
	private SharedPreferences prefs;
	private String filterType;
	private Filter filter;
	private ProgressBar image_progress;
	private ImageView picture;
	private Button apply;
	private Button load;
	private Bitmap image;
	private boolean processing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		processing=false;

		apply=(Button) findViewById(R.id.button_apply);
		load=(Button) findViewById(R.id.button_load_img);
		image_progress=(ProgressBar) findViewById(R.id.progress_image);
		picture=(ImageView) findViewById(R.id.image_view);
		
		apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!processing) {
					doFilter();
				}
			}
		});
		
		load.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent pic=new Intent(Intent.ACTION_PICK);
				pic.setType("image/*");
				startActivityForResult(pic, PIC_LOADED);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case PIC_LOADED:
			if (resultCode==RESULT_OK) {
				Uri img=data.getData();
				try {
					InputStream imgStream=getContentResolver().openInputStream(img);
					image=BitmapFactory.decodeStream(imgStream, null, null);
					picture.setImageBitmap(image);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
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
			picture.buildDrawingCache(false);
			image=picture.getDrawingCache();
			image=filter.applyFilter(image, windowSize);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			processing=false;
			image_progress.setVisibility(View.GONE);
			picture.setImageBitmap(image);
			picture.destroyDrawingCache();
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
