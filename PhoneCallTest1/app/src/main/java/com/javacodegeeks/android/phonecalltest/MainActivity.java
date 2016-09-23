package com.javacodegeeks.android.phonecalltest;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	private Button callBtn;
	private EditText number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		number = (EditText) findViewById(R.id.phone);
		callBtn = (Button) findViewById(R.id.call);

		// add PhoneStateListener for monitoring
		MyPhoneListener phoneListener = new MyPhoneListener(this);
		TelephonyManager telephonyManager = 
			(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// receive notifications of telephony state changes 
		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
				
		callBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String uri = "";
					// set the data
					if("".equals(number.getText().toString())) {
						uri = "tel:#";
					} else {
						uri = "tel:" + number.getText().toString();
					}

					Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
					startActivity(callIntent);
				} catch(Exception e) {
					Toast.makeText(getApplicationContext(),"Your call has failed...",
						Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
	}
	
	private class MyPhoneListener extends PhoneStateListener {
		 
		private boolean onCall = false;
		Context context;
		AudioManager audioManager;
		public MyPhoneListener(Context context) {
			this.context = context;
		}
 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
 
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// phone ringing...
				Toast.makeText(MainActivity.this, incomingNumber + " calls you", 
						Toast.LENGTH_LONG).show();
				break;
			
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// one call exists that is dialing, active, or on hold
				Toast.makeText(MainActivity.this, "on call...", 
						Toast.LENGTH_LONG).show();
				//because user answers the incoming call
				onCall = true;
				try {
					Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
				} catch (InterruptedException e) {
				}

				//Activate loudspeaker
				audioManager = (AudioManager)
						getSystemService(Context.AUDIO_SERVICE);
				audioManager.setMode(AudioManager.MODE_IN_CALL);
				audioManager.setSpeakerphoneOn(true);
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				// in initialization of the class and at the end of phone call 
				
				// detect flag from CALL_STATE_OFFHOOK
				if (onCall == true) {
					Toast.makeText(MainActivity.this, "restart app after call", 
							Toast.LENGTH_LONG).show();
					audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
 
					// restart our application
					Intent restart = getBaseContext().getPackageManager().
						getLaunchIntentForPackage(getBaseContext().getPackageName());
					restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(restart);
 
					onCall = false;
				}
				break;
			default:
				break;
			}
			
		}
	}

}
