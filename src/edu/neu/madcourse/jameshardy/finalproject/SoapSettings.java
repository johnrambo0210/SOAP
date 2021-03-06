package edu.neu.madcourse.jameshardy.finalproject;

import java.util.Calendar;

import com.google.gson.Gson;

import edu.neu.madcourse.jameshardy.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SoapSettings extends Activity implements OnClickListener, OnItemSelectedListener{

	private static final String TAG = "SOAP SETTINGS";
	private static final int TO_TIME_DIALOG_ID = 34;
	private static final int FROM_TIME_DIALOG_ID = 76;
	public static final String settingsSharedPrefName = "SoapSettings";
	public static final String settingsPrefDataKey = "settings";
	//auto monitor specific time settings
	Spinner fromDaySpinner;
	Spinner toDaySpinner;
	TextView toDayText;
	TextView fromTimeText;
	Button fromTimeButton;
	TextView toTimeText;
	Button toTimeButton;
	CheckBox autoMonitor;
	TextView autoExportTop;
	TextView autoExportBottom;
	CheckBox autoExport;
	TextView emailLabel;
	EditText defaultEmail;
	
	Typeface helveticaLight;
	String[] items;
	
	SoapSettingsHolder sprefSettingsData;
	
	private static final int toSpinnerId = 10;
	private static final int fromSpinnerId = 20;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soap_settings);
		
		View backButton = findViewById(R.id.soap_settings_back_button);
		backButton.setOnClickListener(this);
		
		View helpButton = findViewById(R.id.soap_settings_help_button);
		helpButton.setOnClickListener(this);
		
		helveticaLight = Typeface.createFromAsset(getAssets(), "helvetica_neue_light.ttf");
		TextView settingsTitle = (TextView) findViewById(R.id.soap_settings_title);
		settingsTitle.setTypeface(helveticaLight);
		
		TextView autoMonitorTopText = (TextView) findViewById(R.id.soap_auto_monitor_text_top);
		autoMonitorTopText.setTypeface(helveticaLight);
		
		TextView autoMonitorBottomText = (TextView) findViewById(R.id.soap_auto_monitor_text_bottom);
		autoMonitorBottomText.setTypeface(helveticaLight);
		
		toDayText = (TextView) findViewById(R.id.soap_to_day_text);
		toDayText.setTypeface(helveticaLight);
		
		fromTimeText = (TextView) findViewById(R.id.soap_from_time_text);
		fromTimeText.setTypeface(helveticaLight);
		
		toTimeText = (TextView) findViewById(R.id.soap_to_time_text);
		toTimeText.setTypeface(helveticaLight);
		
		autoMonitor = (CheckBox) findViewById(R.id.soap_auto_monitor_checkbox);
		
		items = getResources().getStringArray(R.array.days_in_week);
		/*
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.days_in_week, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		
		*/
		ArrayAdapter<String> toAdapter = createCustomAdapater(toSpinnerId);
		ArrayAdapter<String> fromAdapter = createCustomAdapater(fromSpinnerId);
		
		toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
		fromDaySpinner = (Spinner) findViewById(R.id.soap_from_day_spinner);
		fromDaySpinner.setOnItemSelectedListener(this);
		fromDaySpinner.setAdapter(fromAdapter);
		
		toDaySpinner = (Spinner) findViewById(R.id.soap_to_day_spinner);
		toDaySpinner.setOnItemSelectedListener(this);
		toDaySpinner.setAdapter(toAdapter);
		
		
		fromTimeButton = (Button) findViewById(R.id.soap_from_time_picker_button);
		fromTimeButton.setOnClickListener(this);
		fromTimeButton.setTypeface(helveticaLight);
		
		toTimeButton = (Button) findViewById(R.id.soap_to_time_picker_button);
		toTimeButton.setOnClickListener(this);
		toTimeButton.setTypeface(helveticaLight);
		
		autoExportTop = (TextView) findViewById(R.id.soap_auto_export_text_top);
		autoExportTop.setTypeface(helveticaLight);
		
		autoExportBottom = (TextView) findViewById(R.id.soap_auto_export_text_bottom);
		autoExportBottom.setTypeface(helveticaLight);
		
		autoExport = (CheckBox) findViewById(R.id.soap_auto_export_checkbox);
		autoExport.setChecked(true); //TODO temporary
		
		emailLabel = (TextView) findViewById(R.id.soap_export_default_email_text);
		emailLabel.setTypeface(helveticaLight);
		
		defaultEmail = (EditText) findViewById(R.id.soap_export_default_email_edit);
		defaultEmail.setTypeface(helveticaLight);
		defaultEmail.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		        	sprefSettingsData.defaultEmail = defaultEmail.getText().toString();
		        	storeNewSettingsInSpref();
		          return true;
		        }
		        return false;
		    }
		});
		
		sprefSettingsData = getSettingsFromSprefOrCreateNew();
		setViewToSettingsData();
	}
	
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.soap_settings_back_button:
			finish();
			break;
		case R.id.soap_settings_help_button:
			showHelpDialog();
			break;
		case R.id.soap_from_time_picker_button:
			showDialog(FROM_TIME_DIALOG_ID);
			break;
		case R.id.soap_to_time_picker_button:
			showDialog(TO_TIME_DIALOG_ID);
			break;
		}
		
	}

	/**
	 * Called when a spinner item is selected, set new start/end day in spref
	 * add one before saving to spref because of difference in start indexes between
	 * spref and the array storage
	 */
	public void onItemSelected(AdapterView<?> parent, View v, int pos,
			long id) {
		int dayId = (int) id;
		int adapterHashCode = parent.getAdapter().hashCode();
		Log.d(TAG, "hashcode: " + adapterHashCode);
		if (adapterHashCode == toSpinnerId){
			sprefSettingsData.endDay = dayId;
		}
		else if (adapterHashCode == fromSpinnerId){
			Log.d(TAG, "fromdayspinner item id: " + id);
			sprefSettingsData.startDay = dayId;
		}
		storeNewSettingsInSpref();
		setServiceAlarm();
		
	}

	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	public void onAutoMonitorChecked(View view){
		boolean checked = ((CheckBox) view).isChecked();
		    
	    if (checked){
	    	toggleAutoMonitorSettingsVisiblity(View.VISIBLE);
	    	setServiceAlarm();
	    }
	    else{
	    	toggleAutoMonitorSettingsVisiblity(View.INVISIBLE);
	    	autoExport.setChecked(false);
	    	cancelPendingAlarm();
	    }
	    sprefSettingsData.autoMonitor = checked;
	    storeNewSettingsInSpref();
	}
	
	public void onAutoExportChecked(View view){
		boolean checked = ((CheckBox) view).isChecked();
		sprefSettingsData.autoExport = checked;
	    storeNewSettingsInSpref();
	}
	
	/**
	 * Switch the Auto Monitor time settings to visible or invisible
	 * @param visibility should be either VIEW.INVISIBLE or VIEW.VISIBLE
	 */
	private void toggleAutoMonitorSettingsVisiblity(int visibility){
		fromDaySpinner.setVisibility(visibility);
		toDaySpinner.setVisibility(visibility);
		toDayText.setVisibility(visibility);
		fromTimeText.setVisibility(visibility);
		fromTimeButton.setVisibility(visibility);
		toTimeText.setVisibility(visibility);
		toTimeButton.setVisibility(visibility);
	}
	
	/**
	 * creates a custom adapter for the spinners, giving them special font
	 */
	private ArrayAdapter<String> createCustomAdapater(final int id){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				R.layout.spinner_textiew, items) {
			
		     public View getView(int position, View convertView, ViewGroup parent) {
		             TextView v = (TextView) super.getView(position, convertView, parent);
		             v.setTypeface(helveticaLight);
		             v.setText(items[position]);
		             ((TextView) v).setTextColor(Color.BLACK);
		             return v;
		     }
		
		
		     public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
		             TextView v = (TextView) super.getDropDownView(position, convertView, parent);
		
		             v.setTypeface(helveticaLight);
		             v.setText(items[position]);
		
		             return v;
		     }
		     
		     @Override
		     public int hashCode(){
		    	 return id;
		     }
		     
		};
		return adapter;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FROM_TIME_DIALOG_ID:
			return new TimePickerDialog(this, 
                                        fromTimePickerListener, 
                                        sprefSettingsData.startTimeHour, 
                                        sprefSettingsData.startTimeMinute,
                                        false);
		case TO_TIME_DIALOG_ID:
			return new TimePickerDialog(this, 
					                    toTimePickerListener, 
					                    sprefSettingsData.endTimeHour, 
					                    sprefSettingsData.endTimeMinute,
					                    false);
		}
		return null;
	}
 
	private TimePickerDialog.OnTimeSetListener fromTimePickerListener = 
            new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			fromTimeButton.setText(formattedDateString(selectedHour, selectedMinute));
			sprefSettingsData.startTimeHour = selectedHour;
			sprefSettingsData.startTimeMinute = selectedMinute;
			storeNewSettingsInSpref();
			setServiceAlarm();
		}
	};
	
	private TimePickerDialog.OnTimeSetListener toTimePickerListener = 
            new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			toTimeButton.setText(formattedDateString(selectedHour, selectedMinute));
			sprefSettingsData.endTimeHour = selectedHour;
			sprefSettingsData.endTimeMinute = selectedMinute;
			storeNewSettingsInSpref();
			setServiceAlarm();
			}
	};
	
	private String formattedDateString(int selectedHour, int selectedMinute){
		String amPM = "AM";
		if (selectedHour > 12){
			selectedHour = selectedHour - 12;
			amPM = "PM";
		}
		
		String min = "" + selectedMinute;
		if (selectedMinute < 10){
			min = "0" + selectedMinute;
		}
			
		return selectedHour + ":" + min + " " + amPM;
	}
	
	private SoapSettingsHolder getSettingsFromSprefOrCreateNew(){
		SharedPreferences spref = getSharedPreferences(settingsSharedPrefName, 0);
		String previousSettings = spref.getString(settingsPrefDataKey, "");
		if (previousSettings != null && previousSettings != ""){
			//previous settings exist, so load them and set them on view
			Gson gson = new Gson();
			return gson.fromJson(previousSettings, SoapSettingsHolder.class);
		}
		else{
			return new SoapSettingsHolder();
		}
	}
	
	/**
	 * Sets the view based on previous settings 
	 * or default settings if no previous settings exist
	 */
	private void setViewToSettingsData(){
		autoMonitor.setChecked(sprefSettingsData.autoMonitor);
		String fromTimeString = formattedDateString(
				sprefSettingsData.startTimeHour,
				sprefSettingsData.startTimeMinute);
		String toTimeString = formattedDateString(
				sprefSettingsData.endTimeHour, 
				sprefSettingsData.endTimeMinute);
		
		fromTimeButton.setText(fromTimeString);
		toTimeButton.setText(toTimeString);
		
		fromDaySpinner.setSelection(sprefSettingsData.startDay);
		toDaySpinner.setSelection(sprefSettingsData.endDay);
		
		autoExport.setChecked(sprefSettingsData.autoExport);
		defaultEmail.setText(sprefSettingsData.defaultEmail);
		
		int visibility = View.VISIBLE;
		if (!sprefSettingsData.autoMonitor){
			visibility = View.INVISIBLE;
		}
		toggleAutoMonitorSettingsVisiblity(visibility);
	}
	
	private void storeNewSettingsInSpref(){
		SharedPreferences spref = getSharedPreferences(settingsSharedPrefName, 0);
		Editor e = spref.edit();
		Gson gson = new Gson();
		
		String json = gson.toJson(sprefSettingsData);
		e.putString(settingsPrefDataKey, json);
		e.commit();
	}
	
	/**
	 * Get service automatic start time from settings
	 * set to this, or if none set use default (9:00AM)
	 */
	private void setServiceAlarm(){
		Calendar c = Calendar.getInstance();
		int curTimeMinutes = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
		int targetMinutes = sprefSettingsData.startTimeHour * 60 + sprefSettingsData.startTimeMinute;
		int timeTillNextOccurance; //in minutes
		if (targetMinutes > curTimeMinutes){
			timeTillNextOccurance = targetMinutes - curTimeMinutes;
		}
		else{
			//remainder of current day plus time to start it at next day
			timeTillNextOccurance = 24 * 60 - curTimeMinutes + targetMinutes;
		}		
		long triggerServiceInMillis = c.getTimeInMillis() + timeTillNextOccurance * 60 * 1000;
		
		Intent intent = new Intent(this, TapListenerService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Log.d(TAG, "scheduling service to start in: " + timeTillNextOccurance / 60 + " hours plus " + timeTillNextOccurance%60 + " minutes");
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, triggerServiceInMillis, 24 * 60 * 60 * 1000, pintent); 
	}
	
	/**
	 * cancels alarm manager, called if user unchecks automatic monitoring
	 */
	private void cancelPendingAlarm(){
		Intent intent = new Intent(this, TapListenerService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
	}
	
	private void showHelpDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
			alertDialogBuilder.setTitle("Help");
 
			alertDialogBuilder
				.setMessage(getResources().getString(R.string.soap_settings_help_text))
				.setPositiveButton("Thanks for the help",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				  });
 
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}
	
}