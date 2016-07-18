package com.v2tech.audio;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;

import com.V2.jni.util.V2Log;

public class AACDecoder {
	/**
	 * Current samlpe rate which parse from audio resource
	 */
	private int mSampleRate;

	/**
	 * Current channel configuration e which parse from audio resource<br>
	 * AudioFormat.CHANNEL_OUT_MONO, AudioFormat.CHANNEL_OUT_STEREO;
	 */
	private int mChannel;

	/**
	 * Current Audio Tracker supported minimal buffer size
	 */
	private int mMinBufferSize;

	/**
	 * Audio resource duration which parse from audio resource
	 */
	private long mDurationUS;

	/**
	 * End of audio resource
	 */
	private boolean mEOF;

	private MediaExtractor mExtractor;

	private MediaCodec mDecoder;

	private AudioTrack mAudioTracker;
	
	private DecoderNotification notificationListener;
	
	private DecoderWorkder worker;
	
	private AudioParameter currentPar;


	public AACDecoder() {
		
	}
	
	public AACDecoder(DecoderNotification listener) {
		this.notificationListener = listener;
	}

	private void initDecoder() {
		String mime = "audio/mp4a-latm";
		// Only support first audio format
		MediaFormat format = mExtractor.getTrackFormat(0);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mime = format.getString(MediaFormat.KEY_MIME);
			// Get sample rate
			mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
			// Get channel count
			int channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
			mChannel = channel == 1 ? AudioFormat.CHANNEL_OUT_MONO
					: AudioFormat.CHANNEL_OUT_STEREO;
			// Get duration of audio
			mDurationUS = format.getLong(MediaFormat.KEY_DURATION);

		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			throw new RuntimeException(" Doesn't support on lower 4.1 version");
		} else {
			throw new RuntimeException(" Doesn't support on lower 4.1 version");
		}

		mMinBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannel,
				AudioFormat.ENCODING_PCM_16BIT);
		if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE) {
			throw new RuntimeException(
					" Doesn't support this configuration : [" + mSampleRate
							+ ", " + mChannel + ", "
							+ AudioFormat.ENCODING_PCM_16BIT + "]");
		}

		try {
			mDecoder = MediaCodec.createDecoderByType(mime);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("does not support decoder "+ mime);
		}
		mDecoder.configure(format, null, null, 0);

		mAudioTracker = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate,
				mChannel, AudioFormat.ENCODING_PCM_16BIT, mMinBufferSize,
				AudioTrack.MODE_STREAM);

	}

	public synchronized boolean play(AudioParameter par) {
		if (par == null || par.getPath() == null) {
			throw new NullPointerException(" par is null or path is null");
		}
		this.currentPar = par;
		return play(par.getPath());
	}
	
	
	public synchronized boolean play(String audioPath) {
		boolean ret = true;
		File f = new File(audioPath);
		if (!f.exists()) {
			ret = false;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			ret =  play(fis.getFD());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret =  false;
		} catch (Exception e) {
			e.printStackTrace();
			ret =  false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		V2Log.i("==> decode request ret: " + ret +"  ==>" + audioPath);
		return ret;
	}

	public synchronized boolean play(FileDescriptor fd) {
		if (worker != null && worker.isAlive()) {
			throw new RuntimeException(" Decoder is working on");
		}
		
		worker = new DecoderWorkder();

		try {
			mEOF = false;
			mExtractor = new MediaExtractor();
			mExtractor.setDataSource(fd);
			initDecoder();
			int trackNum = mExtractor.getTrackCount();
			if (trackNum <= 0) {
				V2Log.e("===>  track Num : " + trackNum);
				return false;
			}

			// Always switch to first format
			mExtractor.selectTrack(0);
			
			//start decoder thread
			worker.start();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void stop() {
		mEOF = true;
	}

	public void seek(int sec) {
		if (sec * 1000000 > this.mDurationUS) {
			throw new RuntimeException(" Seconds out of duration"
					+ getDuration());
		}
		mExtractor.seekTo(sec * 1000000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
	}

	public int getDuration() {
		return (int) mDurationUS / 1000000;
	}

	
	
	
	
	public DecoderNotification getNotificationListener() {
		return notificationListener;
	}

	public void setNotificationListener(DecoderNotification notificationListener) {
		this.notificationListener = notificationListener;
	}





	class DecoderWorkder extends Thread {

		@Override
		public void run() {
			if (notificationListener != null) {
				notificationListener.onDecodeStart(currentPar);
			}
			
			mDecoder.start();
			mAudioTracker.play();

			ByteBuffer[] inputBuffers = mDecoder.getInputBuffers();
			ByteBuffer[] outputBuffers = mDecoder.getOutputBuffers();

			int inputBufferIndex = -1;
			int readSize = -1;
			long presentationTimeUs;

			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			
			while (!mEOF) {

				inputBufferIndex = mDecoder.dequeueInputBuffer(1000);
				if (inputBufferIndex >= 0) {
					ByteBuffer buffer = inputBuffers[inputBufferIndex];
					buffer.clear();
					readSize = mExtractor.readSampleData(buffer, 0);
					if (readSize > 0) {
						presentationTimeUs = mExtractor.getSampleTime();
						mDecoder.queueInputBuffer(inputBufferIndex, 0,
								readSize, presentationTimeUs, 0);

					} else {
						mDecoder.queueInputBuffer(inputBufferIndex, 0, 0, 0,
								MediaCodec.BUFFER_FLAG_END_OF_STREAM);
					}
					mExtractor.advance();
				}

				int outputBufferIndex = mDecoder.dequeueOutputBuffer(
						bufferInfo, 1000);
				if (outputBufferIndex >= 0) {
					ByteBuffer buffer = outputBuffers[outputBufferIndex];
					byte[] data = new byte[bufferInfo.size];
					buffer.get(data);
					buffer.clear();
					// Flush audio raw data to hardware
					mAudioTracker.write(data, 0, data.length);

					// outputBuffer is ready to be processed or
					// rendered.
					mDecoder.releaseOutputBuffer(outputBufferIndex, false);

					if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
						mEOF = true;
						if (notificationListener != null) {
							notificationListener.onDecodeFinish(currentPar);
						}
						break;
					}
				} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
					// no needed to handle if API level >= 21 and
					// using getOutputBuffer(int)
					// outputBuffers = mDecoder.getOutputBuffers();
				} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
					// Subsequent data will conform to new format.
					// can ignore if API level >= 21 and using
					// getOutputFormat(outputBufferIndex)
					// format = mDecoder.getOutputFormat();
				}

			}

			mDecoder.stop();
			mAudioTracker.stop();
			mDecoder.release();
			mAudioTracker.release();
			mExtractor.release();
			
			if (notificationListener != null) {
				notificationListener.onDecodeFinish(currentPar);
			}
		}

	}
	
	
	public interface DecoderNotification {
		public void onDecodeFinish(AudioParameter ap);
		public void onDecodeStart(AudioParameter ap);
		public void onDecodeError(Throwable e);
	}
	
	
	public interface AudioParameter {
		public String getPath();
	}
}
