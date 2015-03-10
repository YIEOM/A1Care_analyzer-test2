package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.R;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.view.WindowManager;

public class SoundModel {

	private Activity activity;
	private Context context;
	private AudioManager audioManager;
	
	public SoundModel(Activity activity, Context context) {
		
		this.activity = activity;
		this.context = context;
		audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public int getBarGauageImage(int volume) {
		
		int barGaugeImage;
		
		switch(volume) {
		
		case 0	:
			barGaugeImage = 0;
			break;
		
		case 3	:
			barGaugeImage = R.drawable.sound_bar_gauge_blue_1;
			break;

		case 6	:
			barGaugeImage = R.drawable.sound_bar_gauge_blue_2;
			break;
			
		case 9	:
			barGaugeImage = R.drawable.sound_bar_gauge_blue_3;
			break;
			
		case 12	:
			barGaugeImage = R.drawable.sound_bar_gauge_blue_4;
			break;
		
		case 15	:
			barGaugeImage = R.drawable.sound_bar_gauge_blue_5;
			break;
			
		default		:
			barGaugeImage = R.drawable.sound_bar_gauge_blue_3;
			break;
		}
		
		return barGaugeImage;
	}
	
	public int downSoundVolume(int volume) {
		
		if(volume != 0) volume -= 3;
		
		return volume;
	}
	
	public int upSoundVolume(int volume) {
		
		if(volume != 15) volume += 3;
				
		return volume;
	}
	
	public int getSoundVolume() {
		
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void setSoundVolume(int volume) {
	
		try {
			
			final int mWin;
			
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
			
			SoundPool mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
			mWin = mPool.load(context, R.raw.win, 1);
			
			mPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			      public void onLoadComplete(SoundPool mPool, int sampleId, int status) {

			  		mPool.play(mWin, 1, 1, 0, 0, 1); // playing sound
			      }
			});
			
		} catch(Exception e) {
			
		}
	}
}
