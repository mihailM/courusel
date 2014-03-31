package com.example.courusel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {
	CustomScrollView scrollView;
	boolean isInit = true;
	EditText text;
	static ImageView image;
	private static Activity currentAct;
	private static View view;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (EditText)findViewById(R.id.index);
		scrollView = (CustomScrollView)findViewById(R.id.horizontal_scroll_view);
		scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				scrollView.customInit();
			}
		});
		currentAct = this;
		image = (ImageView)findViewById(R.id.imageV);
		view = (View)findViewById(R.id.main_view);
	}
	
	public static Activity getActivity(){
		return currentAct;
	}
	
	public static  void scrollTO(View view){
		if (view!=null)
		{
			image.getLayoutParams().width = 50;
			image.getLayoutParams().height = 50; 
			image.requestLayout();
			view.requestLayout();
		}
	}
	
	public static void ReqUpdate(){
		view.requestLayout();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
