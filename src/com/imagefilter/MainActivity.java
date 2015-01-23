package com.imagefilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final int PIC_LOADED=1;
	
	private int windowSize;
	private SharedPreferences prefs;
	private String filterType;
	private Filter filter;
	private ProgressBar image_progress;
	private ImageView picture;
	private Button apply;
	private Button load;
	private Bitmap image;
	private boolean processing;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		processing=false;
		ctx=this;

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
		image=loadImage();
		if (image!=null) {
			picture.setImageBitmap(image);
		}
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
					image=BitmapFactory.decodeStream(imgStream);
					saveImage(image);
					picture.setImageBitmap(image);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Bitmap loadImage() {
		String pic=ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/filtered/filter_image.png";
		return BitmapFactory.decodeFile(pic);
	}
	
	private void saveImage(Bitmap bitmap) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir=new File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/filtered");
			dir.mkdirs();
			String filename="filter_image.png";
			File file=new File(dir,filename);
			if (file.exists()){file.delete();}
			try {
				FileOutputStream os=new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Toast.makeText(ctx, "The image could not be saved", Toast.LENGTH_LONG).show();
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
			saveImage(image);
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
