package edu.neu.madcourse.jameshardy.finalproject;

import java.util.Calendar;

import edu.neu.madcourse.jameshardy.R;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ActionBarView extends View{
	private static final int ACTION_BUTTON_COUNT = 3;
	private static final int SETTINGS_BUTTON = 0;
	private static final int SERVICE_TOGGLE_BUTTON = 1;
	private static final int EXPORT_BUTTON = 2;
	
	int viewHeight;
	int viewWidth;
	int buttonWidth;
	Paint letterColor;
	Typeface helveticaLight;
	Rect[] buttonRectArray = new Rect[ACTION_BUTTON_COUNT];
	Paint[] buttonStates = new Paint[ACTION_BUTTON_COUNT];
	String[] buttonText = new String[ACTION_BUTTON_COUNT];
	String [] topText = new String[ACTION_BUTTON_COUNT];
	String [] bottomText = new String[ACTION_BUTTON_COUNT];
	Paint defaultState;
	Paint focusedState;
	Paint pressedState;
	Paint buttonBorderColor;
	int moveEventButtonId = -1;
	
	Calendar c;
	long lastUpdateTime = 0;
	private final SoapGUI soapGUI;
	
	public ActionBarView(Context context, AttributeSet attrs) {
		super(context);
		this.soapGUI = (SoapGUI) context;
		
		setProperServiceVariables();
		c = Calendar.getInstance();
	}
	
	@Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(width,heightMeasureSpec);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    viewHeight = h;
	    viewWidth = w;
	    buttonWidth = viewWidth / ACTION_BUTTON_COUNT;
	    super.onSizeChanged(w, h, oldw, oldh);
	    initializeCanvasObjects(soapGUI);
	    letterColor.setTextSize(buttonWidth * .12f);
	    setProperServiceVariables();
    }    
    
    @Override
    protected void onDraw(Canvas canvas){
    	
    	
    	
    	//draw buttons, text on buttons, and line borders between buttons
    	for (int i = 0; i < ACTION_BUTTON_COUNT; i++){
    		
    		//only draw a border between buttons
    		if (i != 0){
				canvas.drawLine(i * buttonWidth,
						viewHeight / 4, 
						i * buttonWidth, 
						3 * viewHeight / 4, 
						buttonBorderColor);
			}
    		
    		if (buttonStates[i] != defaultState){
    			canvas.drawRect(buttonRectArray[i], buttonStates[i]);
    		}
    		
    		canvas.drawText(topText[i],
                    i * buttonWidth + buttonWidth/2,
                    3 * viewHeight / 10,
                    letterColor);
    		
    		canvas.drawText(bottomText[i],
                    i * buttonWidth + buttonWidth/2,
                    7 * viewHeight / 10,
                    letterColor);
    		
    	}
    	
    	//draw line across the top of the view for button border
    	canvas.drawLine(0, 0, viewWidth, 0, buttonBorderColor);
    	
    	//update the service button only maximum of every 30 seconds
    	if (c != null && c.getTimeInMillis() - lastUpdateTime > 30000){
    		setProperServiceVariables();
    		lastUpdateTime = c.getTimeInMillis();
    	}
    	
	    	
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
    	
    	float x = event.getX();
    	
    	int buttonNumber = determineWhichButtonWasPressed(x);
    	//check if the touch occurred outside of the buttons
    	if (buttonNumber < 0 
    			|| buttonNumber > ACTION_BUTTON_COUNT - 1
    			|| x <= 0
    			|| event.getY() <= 0
    			|| event.getY() >= viewHeight){
    		if (moveEventButtonId != -1){
    			buttonStates[moveEventButtonId] = defaultState;
    			invalidate();
    			moveEventButtonId = -1;
    		}
    		return false;
    	}
    	Log.d("actionbarview", "button pressed: #" + buttonNumber);
    	
    	switch (event.getAction()){
    	case MotionEvent.ACTION_OUTSIDE:
    		if (moveEventButtonId != -1){
    			buttonStates[moveEventButtonId] = defaultState;
            	invalidate();
            	moveEventButtonId = -1;
    		}
        	return false; 
        case MotionEvent.ACTION_DOWN:
        	buttonStates[buttonNumber] = focusedState;
        	invalidate();
        	moveEventButtonId = buttonNumber;
        	break;
        case MotionEvent.ACTION_UP:
        	buttonStates[buttonNumber] = pressedState;
        	appropriateButtonAction(buttonNumber);
        	invalidate();
        	createTimerToRemoveAnimationOnButtons(buttonNumber);
        	return false;
        case MotionEvent.ACTION_MOVE:
        	//if button touched before this move is different from current button,
        	//reset last button touched to default and set the new one to focused
        	if (moveEventButtonId != buttonNumber){
        		buttonStates[buttonNumber] = focusedState;
        		if (moveEventButtonId != -1){
        			buttonStates[moveEventButtonId] = defaultState;
        		}
        		invalidate();
        		moveEventButtonId = buttonNumber;
        	}
        	break;
    	}    	
		return true;
    }
    
    /**
     * Returns the button number for the button the user has pressed at the x location
     */
    private int determineWhichButtonWasPressed(float x){
    	return (int) x / buttonWidth;
    }
    
    private void createTimerToRemoveAnimationOnButtons(final int buttonNumber){
        CountDownTimer blockTimer = new CountDownTimer(100, 100){

                @Override
                public void onFinish() {
                	if (this != null){
	                	buttonStates[buttonNumber] = defaultState;
	                	invalidate();
                	}
                }

                @Override
                public void onTick(long millisUntilFinished) {
                        
                }
                
        };
        blockTimer.start();
}
    
    /**
     * sets up all canvas objects (paints, rects, etc)
     */
    private void initializeCanvasObjects(Context context){
    	//typeface for the buttons
		helveticaLight = Typeface.createFromAsset(context.getAssets(), "helvetica_neue_light.ttf");
		letterColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		letterColor.setColor(getResources().getColor(
                R.color.soap_action_button_text));
	    letterColor.setStyle(Style.FILL_AND_STROKE);
	    letterColor.setTextAlign(Paint.Align.CENTER);
	    letterColor.setTypeface(helveticaLight);
	    letterColor.setShadowLayer(3, 2, 2, getResources().getColor(R.color.text_shadow));
	    
	    //paints corresponding to button states: default, focused, pressed
	    defaultState = new Paint();
	    defaultState.setColor(getResources().getColor(
	    		R.color.soap_default_background));
	    
	    focusedState = new Paint();
	    focusedState.setColor(getResources().getColor(
	    		R.color.soap_buttons_pressed));
	    
	    pressedState = new Paint(); 
	    pressedState.setColor(getResources().getColor(
	    		R.color.soap_buttons_pressed));
	    
	    //paint for the button borders
	    buttonBorderColor = new Paint(Paint.ANTI_ALIAS_FLAG);
	    buttonBorderColor.setColor(getResources().getColor(
	    		R.color.soap_button_border));
	    buttonBorderColor.setStyle(Style.STROKE);
	    buttonBorderColor.setStrokeWidth(2);
	    
	    //initialize rects for each button
	    //and the default paint added to the buttonStates
	    for (int i = 0; i < ACTION_BUTTON_COUNT; i++){
	    	Rect buttonRect = new Rect();
	    	buttonRect.left = i * buttonWidth;
    		buttonRect.top = 0;
    		buttonRect.right = i * buttonWidth + buttonWidth;
    		buttonRect.bottom = viewHeight;
	    	buttonRectArray[i] = buttonRect;
	    	
	    	buttonStates[i] = defaultState;
	    	
	    }
	    
	    topText[SETTINGS_BUTTON] = "VIEW";
	    setProperServiceVariables();
	    topText[EXPORT_BUTTON] = "EXPORT";
	    
	    bottomText[SETTINGS_BUTTON] = "SETTINGS";
	    bottomText[SERVICE_TOGGLE_BUTTON] = "MONITORING";
	    bottomText[EXPORT_BUTTON] = "DATA";
	    
    }
    
    private void appropriateButtonAction(int buttonNumber){
    	if (buttonNumber == SETTINGS_BUTTON){
    		soapGUI.openSettingsActivity();
    	}
    	else if (buttonNumber == SERVICE_TOGGLE_BUTTON){
    		switchServiceButton();
    	}
    	else if (buttonNumber == EXPORT_BUTTON){
    		soapGUI.exportData();
    	}
    }
    
    private void switchServiceButton(){
    	if (serviceIsRunning()){
    		topText[SERVICE_TOGGLE_BUTTON] = "START";
    		soapGUI.stopService();
    	}
    	else{
    		topText[SERVICE_TOGGLE_BUTTON] = "STOP";
    		soapGUI.startService();
    	}
    	
    }
    
    /**
	 * checks the list of all running services to see if the service is currently running
	 * More reliable than using spref
	 */
	private boolean serviceIsRunning() {
		if (soapGUI != null){
		    ActivityManager manager = (ActivityManager) soapGUI.getSystemService(Context.ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		        if (TapListenerService.class.getName().equals(service.service.getClassName())) {
		        	Log.d("", "Service is running");
		            return true;
		        }
		    }
		}
		Log.d("", "service not running");
	    return false;
	}
	
	private void setProperServiceVariables(){
		if (serviceIsRunning()){
			topText[SERVICE_TOGGLE_BUTTON] = "STOP";
		}
		else{
			topText[SERVICE_TOGGLE_BUTTON] = "START";
		}
		
	}
    
	
}