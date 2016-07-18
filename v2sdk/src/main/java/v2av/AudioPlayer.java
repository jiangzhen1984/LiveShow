package v2av;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class AudioPlayer 
{
	private Context _context;
	
	private MediaPlayer mediaPlayer =  new MediaPlayer();

	@SuppressWarnings("unused")
	private int PlayFile(String filePath)
	{
		mediaPlayer.reset();
		try
		{
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new PreparedListener());
			mediaPlayer.setOnCompletionListener(new CompletionListener());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	@SuppressWarnings("unused")
	private int StopPlay()
	{
		mediaPlayer.stop();

		return 0;
	}
	
	private final class PreparedListener implements OnPreparedListener {  
        @Override  
        public void onPrepared(MediaPlayer mp) {  
        	mp.start();    //��ʼ����
        }  
    }
	
	private final class CompletionListener implements OnCompletionListener {  

		@Override
		public void onCompletion(MediaPlayer mp) {
			mp.stop();
			
			// notify app to do something
		}  
    }
}
