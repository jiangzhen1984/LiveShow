package com.v2tech.audio;

import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;

public class AACEncoder {

	private static final int SAMPLE_RATE = 16000;

	private static final int BIT_RATE = 64000;

	private static final int CHANNEL = 1;

	private static final int AAC_HEADER_LENGTH = 7;

	private static final int MAX_INPUT_BUFFER_SIZE = 65536;

	/**
	 * Flag for current state
	 */
	private boolean mIsRecording;

	/**
	 * Lock for start or stop recording thread
	 */
	private Object mLock = new Object();

	/**
	 * AudioRecord for get audio raw data from hardware
	 */
	private AudioRecord mRecorder;

	/**
	 * MediaCodec for encode AAC packet
	 */
	private MediaCodec mEncoder;

	/**
	 * Buffer size for minimal audio buffer size
	 */
	private int mBufferSize;

	/**
	 * record current frame's db
	 */
	private double mDB;

	private RecorderThread workerThread;

	private AACEncoderNotification nofiticationListener;

	/**
	 * 
	 * @param out
	 */
	public AACEncoder(AACEncoderNotification nofiticationListener) {
		this.nofiticationListener = nofiticationListener;
	}

	public AACEncoder() {

	}

	private void initEncoder() {
		mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		if (mBufferSize == AudioRecord.ERROR_BAD_VALUE) {
			throw new RuntimeException(
					" Can not initialze AudioRecord because buffer size is bad value");
		}
		mRecorder = new AudioRecord(AudioSource.MIC, SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				mBufferSize);

		mEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
		MediaFormat format = new MediaFormat();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
			format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, CHANNEL);
			format.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
			format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
			format.setInteger(MediaFormat.KEY_AAC_PROFILE,
					MediaCodecInfo.CodecProfileLevel.AACObjectLC);
			format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,
					MAX_INPUT_BUFFER_SIZE);
			// format.setInteger(MediaFormat.KEY_IS_ADTS, 1);
			format.setInteger(MediaFormat.KEY_CHANNEL_MASK,
					AudioFormat.CHANNEL_IN_MONO);
			mEncoder.configure(format, null, null,
					MediaCodec.CONFIGURE_FLAG_ENCODE);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			throw new RuntimeException(" Doesn't support on lower 4.1 version");
		} else {
			throw new RuntimeException(" Doesn't support on lower 4.1 version");
		}
	}

	public void start() {
		synchronized (mLock) {
			if (mIsRecording) {
				throw new RuntimeException("is recording please stop first");
			}
			mIsRecording = true;
		}
		initEncoder();

		workerThread = new RecorderThread();
		workerThread.start();

	}

	public void stop() {
		synchronized (mLock) {
			if (!mIsRecording) {
				return;
			}
			mIsRecording = false;
		}

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	class RecorderThread extends Thread {

		@Override
		public void run() {
			int read;
			byte[] audioBuffer = new byte[mBufferSize];

			ByteBuffer[] inputBuffers;
			ByteBuffer[] outputBuffers;

			ByteBuffer inputBuffer;
			ByteBuffer outputBuffer;

			MediaCodec.BufferInfo bufferInfo;
			int inputBufferIndex;
			int outputBufferIndex;

			byte[] outData;

			if (nofiticationListener != null) {
				nofiticationListener.onRecordStart();
			}
			mEncoder.start();
			mRecorder.startRecording();
			while (mIsRecording) {
				try {
					read = mRecorder.read(audioBuffer, 0, mBufferSize);
					if (read <= 0) {
						if (nofiticationListener != null) {
							nofiticationListener.onError(new Exception(
									" End of stream"));
						}
						break;
					}
				} catch (Exception e) {
					if (nofiticationListener != null) {
						nofiticationListener.onError(e);
					}
					break;
				}

				saveDB(audioBuffer);
				if (nofiticationListener != null) {
					if (mDB != Double.NaN) {
						nofiticationListener.onDBChanged(mDB);
					}
				}

				inputBuffers = mEncoder.getInputBuffers();
				outputBuffers = mEncoder.getOutputBuffers();
				inputBufferIndex = mEncoder.dequeueInputBuffer(-1);
				if (inputBufferIndex >= 0) {
					inputBuffer = inputBuffers[inputBufferIndex];
					inputBuffer.clear();
					inputBuffer.put(audioBuffer);

					mEncoder.queueInputBuffer(inputBufferIndex, 0,
							audioBuffer.length, 0, 0);
				}

				bufferInfo = new MediaCodec.BufferInfo();
				outputBufferIndex = mEncoder.dequeueOutputBuffer(bufferInfo, 0);

				while (outputBufferIndex >= 0) {
					outputBuffer = outputBuffers[outputBufferIndex];

					outputBuffer.position(bufferInfo.offset);
					outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

					outData = new byte[bufferInfo.size + AAC_HEADER_LENGTH];
					outputBuffer.get(outData, AAC_HEADER_LENGTH, outData.length
							- AAC_HEADER_LENGTH);

					fillAACHeader(outData);

					if (nofiticationListener != null) {
						nofiticationListener.onAACDataOutput(outData,
								outData.length);
					}

					mEncoder.releaseOutputBuffer(outputBufferIndex, false);
					outputBufferIndex = mEncoder.dequeueOutputBuffer(
							bufferInfo, 0);
				}

			}

			mEncoder.stop();
			mRecorder.stop();
			mEncoder.release();
			mRecorder.release();

			if (nofiticationListener != null) {
				nofiticationListener.onRecordFinish();
			}

			synchronized (mLock) {
				mIsRecording = false;
			}

		}

		private void saveDB(byte[] audiobuffer) {
			int amplitude = (audiobuffer[0] & 0xff) << 8 | audiobuffer[1];

			// mDB = 20 * Math.log10((double) Math.abs(amplitude) /65535.0);

			mDB = 20.0 * Math.log10(amplitude) - 20.0 * Math.log10(700);
		}

		private void fillAACHeader(byte[] data) {
			if (data.length < AAC_HEADER_LENGTH) {
				throw new ArrayIndexOutOfBoundsException(" data length "
						+ data.length);
			}

			int profile = MediaCodecInfo.CodecProfileLevel.AACObjectLC; // AAC
																		// LC
			int freqIdx = 8; // 16000KHz

			// fill in ADTS data
			data[0] = (byte) 0xFF;
			data[1] = (byte) 0xF9;
			data[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (CHANNEL >> 2));
			data[3] = (byte) (((CHANNEL) << 6) + (data.length >> 11));
			data[4] = (byte) ((data.length & 0x7FF) >> 3);
			data[5] = (byte) (((data.length & 7) << 5) + 0x1F);
			data[6] = (byte) 0xFC;
		}

	};

	public interface AACEncoderNotification {

		public void onRecordStart();

		public void onRecordFinish();

		public void onError(Throwable e);

		public void onDBChanged(double db);

		public void onAACDataOutput(byte[] data, int len);
	}

}
