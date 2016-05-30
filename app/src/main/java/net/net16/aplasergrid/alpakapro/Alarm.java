package net.net16.aplasergrid.alpakapro;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by David on 5/30/2016.
 */
public class Alarm {
    private static Context context;
    private static MediaPlayer mp;

    private static Alarm ourInstance = new Alarm(context);


    public static Alarm getInstance() {
        return ourInstance;
    }

    public Alarm(Context context) {
        this.context = context;
    }

    public void playSound(){
        if(mp == null){
            mp = MediaPlayer.create(context, R.raw.alarm);
            mp.setVolume(100,100 );
            mp.setLooping(true);
            mp.start();
        }
    }

    public void stopSound(){

        if(mp != null){
            if(mp.isPlaying()){
                mp.stop();
                mp.reset();
                mp.release();
            }

            mp = null;
        }
    }
}
