package com.example.courusel;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CustomScrollView extends HorizontalScrollView implements
		OnTouchListener, OnGestureListener {

	float lastX;
	ImageView imageDef;
	float defaultSize = -1;
	float currentSize;
	private GestureDetector gestureDetector;
	ViewGroup group;
	HorizontalScrollView scrollView;
	int centerOfScroll;
	ArrayList<View> imagesArray;
	int maxScroll = -1;
	int defCount;
	ArrayList<Integer> positionOfItems;
	Runnable run;
	Handler handler;
	int moveX;
	int selectedItemIndex = -1;

	public CustomScrollView(Context ctx, AttributeSet attr, int defStyle) {
		super(ctx, attr, defStyle);
		init(ctx);
	}

	public CustomScrollView(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		init(ctx);
	}

	public CustomScrollView(Context ctx) {
		super(ctx);
		init(ctx);
	}

	public void moveToItem(int index) {
		if (index >= 0 && (index + 1) <= defCount) {
			scrollTo(positionOfItems.get(index), 0);
		} else {
			// TODO
		}
	}

	private int getXforItem(int index) {
		float count = defCount % 2;
		index++;
		int position = -1;
		if (count == 0) {

			if (index <= defCount / 2) {
				position = centerOfScroll - imageDef.getWidth() / 2
						- (imageDef.getWidth() * (defCount / 2 - index));
			} else {
				position = centerOfScroll + imageDef.getWidth() / 2
						+ (imageDef.getWidth() * (index - 1 - defCount / 2));
			}
		} else {
			if (index == defCount / 2 + 1) {
				position = centerOfScroll;
			} else if (index < defCount / 2) {
				position = centerOfScroll
						- (imageDef.getWidth() * (defCount / 2 - (index - 1)));
			} else {
				position = centerOfScroll
						+ (imageDef.getWidth() * ((index - 1) - defCount / 2));
			}
		}
		return position;
	}

	public void customInit() {
		if (imageDef == null) {
			handler = new Handler();
			try {
				centerOfScroll = getWidth() / 2;
				group = (ViewGroup) findViewById(R.id.parents);
				defCount = group.getChildCount();
				imagesArray = new ArrayList<View>();
				for (int i = 0; i < group.getChildCount(); i++) {
					ViewGroup relative = (ViewGroup) group.getChildAt(i);
					View image = (View) relative.getChildAt(0);
					imagesArray.add(image);
					String name = "image" +(i+1);
					Bitmap bm = BitmapFactory.decodeResource(getResources(), getContext().getResources().getIdentifier(name, "drawable", getContext().getPackageName()));
					((ImageView)image).setImageBitmap(RoundedImageView.getCroppedBitmap(bm, image.getWidth()));
				}
				float distance = centerOfScroll - getLeft();
				float count = (float) distance
						/ imagesArray.get(0).getLayoutParams().width;
				count += 0.5;
				imageDef = (ImageView)imagesArray.get(0);
				defaultSize = imageDef.getHeight();
				int steps = Math.round(count);
				for (int i = 0; i < steps; i++) {
					View view = new View(getContext());
					view.setLayoutParams(new LayoutParams(imageDef.getLayoutParams().width,imageDef.getLayoutParams().height));
					group.addView(view, 0);
					view = new View(getContext());
					view.setLayoutParams(new LayoutParams(imageDef.getLayoutParams().width,imageDef.getLayoutParams().height));
					group.addView(view);
				}
				Log.i("Allok", "exc");
			} catch (Exception e) {
				Log.i("Exception", "exc");
			}
		}
		maxScroll = group.getWidth() - getWidth();
		centerOfScroll = maxScroll / 2;
		positionOfItems = null;
		positionOfItems = new ArrayList<Integer>();
		for (int i = 0; i < defCount; i++) {
			positionOfItems.add(getXforItem(i));
		}
	}
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		setSizes();
	}
	private void init(Context ctx) {
		this.setOnTouchListener(this);
		gestureDetector = new GestureDetector(ctx, this);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			setPosition();

		}
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		setPosition();
		setSizes();
	}

	private void setPosition() {
		if (run != null && handler != null) {
			handler.removeCallbacks(run);
		}
		if (positionOfItems != null) {
			if (getScrollX() < positionOfItems.get(0)) {
				run = new Runnable() {
					@Override
					public void run() {
						selectedItemIndex = 0;
						scrollTo(positionOfItems.get(selectedItemIndex), 0);
					}
				};
			} else if (getScrollX() > positionOfItems.get(positionOfItems
					.size() - 1)) {
				run = new Runnable() {
					@Override
					public void run() {
						if (positionOfItems.size() > 1) {
							selectedItemIndex = positionOfItems.size() -1;
							scrollTo(positionOfItems.get(selectedItemIndex),0);
						} else {
							selectedItemIndex = 0;
							scrollTo(positionOfItems.get(selectedItemIndex), 0);
						}
					}
				};
			} else {
				for (int i = 0; i < positionOfItems.size() - 1; i++) {
					if (i > 0) {

						if (positionOfItems.get(i - 1) < getScrollX()&& positionOfItems.get(i) > getScrollX()) {
							int dist1 = getScrollX()- positionOfItems.get(i - 1);
							int dist2 = positionOfItems.get(i) - getScrollX();
							if (dist1 > dist2) {
								selectedItemIndex = i;
								moveX = positionOfItems.get(selectedItemIndex);
							} else {
								selectedItemIndex = i-1;
								moveX = positionOfItems.get(selectedItemIndex);
							}
							run = new Runnable() {
								@Override
								public void run() {
									scrollTo(moveX, 0);
								}
							};
							break;
						}
					}
				}
			}
		}
		handler.postDelayed(run, 100);
	}
	
	

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		setPosition();
		setSizes();
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		Log.i("In move", "x = " + getScrollX());
		setSizes();
		return false;
	}
	
	private void setSizes(){
		if (imagesArray!=null)
		{
			for (int i = 0; i < imagesArray.size(); i++) {
				final View image = imagesArray.get(i);
				int distance = getScrollX() - positionOfItems.get(i);
				distance = Math.abs(distance);
				int smallSize = (int) defaultSize; 
				int size = 0;
				int adder = 0;
				if (distance>defaultSize)
				{
					image.getLayoutParams().height = (int)(defaultSize * (float)0.76);
					
				}  else if (distance<defaultSize){
				
					adder =  (int) ((defaultSize *0.24)/100 * distance*100/defaultSize);
					image.getLayoutParams().height = smallSize - adder;
				}
				image.invalidate();
				image.requestLayout();
				if (MainActivity.getActivity()!=null)
				{
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MainActivity.ReqUpdate();
						}
					});
				}
			}
		}
		
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
