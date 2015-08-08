package com.v2tech.widget;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.chunk.VideoFormatSelectorUtil;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import com.v2tech.view.VideoOpt;
import com.v2tech.view.VideoState;
import com.v2tech.vo.Live;

public class VideoShowFragment extends Fragment implements ExoPlayer.Listener,
		HlsSampleSource.EventListener, VideoOpt {

	private static final boolean DEBUG = false;
	private static final String TAG = "VideoShowFragment";

	private int mIndex;
	private PlayerControl playerControl;
	private ExoPlayer player;
	private SurfaceView mSurfaceView;
	private TrackRenderer videoRender;
	private boolean surfacePushed;
	private HandlerThread handlerThread;
	private Handler localHandler;
	private Live live;
	private VideoState videoState = VideoState.UNINIT;
	private VideoFragmentStateListener mStateListener;
	
	
	public interface VideoFragmentStateListener {
		public void onInited();
		public void onUnInited();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		handlerThread = new HandlerThread("");
		handlerThread.start();
		while (!handlerThread.isAlive()) {
			try {
				wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		localHandler = new Handler(handlerThread.getLooper());
		
		player = ExoPlayer.Factory.newInstance(2, 1000, 5000);
		player.addListener(this);
		playerControl = new PlayerControl(player);
		

		videoState = VideoState.IDLE;
		if (DEBUG) {
			V2Log.i(TAG, this+"  created");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = new RelativeLayout(getActivity());
		mSurfaceView = new SurfaceView(getActivity());
		mSurfaceView.setZOrderMediaOverlay(true);
		// mSurfaceView.setZOrderOnTop(true);
		mSurfaceView.getHolder().addCallback(mHolderCallback);
		rl.addView(mSurfaceView, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		rl.setBackgroundColor(Color.WHITE);
		return rl;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (playerControl.isPlaying()) {
			player.stop();
		}
		player.release();
		player = null;
		handlerThread.quit();
		videoState = VideoState.RELEASE;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (player == null) {
			return;
		}
		if (!isVisibleToUser) {
			if (playerControl.isPlaying()) {
				playerControl.pause();
			}
		} else {
			playerControl.start();

		}
	}

	public void play(Live live) {
		if (this.isDetached() || !this.isAdded()) {
			V2Log.w(TAG, "This fragment is detached!  " + this);
			return;
		}
		this.live = live;
		if (DEBUG) {
			V2Log.i(TAG, this+" "+ live+"  start to play");
		}
		
//		if (!surfacePushed) {
//			V2Log.w(TAG, msg);
//			return;
//		}
//		
		
		if (playerControl.isPlaying()) {
			player.setRendererEnabled(0, false);
			player.setRendererEnabled(1, false);
			player.stop();
			player.seekTo(0);
		}
		new HlsRendererBuilder(getActivity(), Util.getUserAgent(getActivity(),
				""), live.getUrl(), new AudioCapabilities(
				new int[] { AudioFormat.ENCODING_PCM_16BIT },
				AudioFormat.CHANNEL_IN_MONO)).buildRenderers();
		videoState = VideoState.PREPARED;
		
	}

	public void pause() {
		if (this.isDetached()) {
			V2Log.e("This fragment is detached!  " + this);
			return;
		}
		if (VideoState.PLAYING == videoState) {
			playerControl.pause();
		}
		videoState = VideoState.PAUSE;
		if (DEBUG) {
			V2Log.i(TAG, this+" "+ live+"  paused");
		}
	}

	public void resume() {
		if (this.isDetached()) {
			V2Log.e("This fragment is detached!  " + this);
			return;
		}
		if (live == null) {
			return;
		}
		switch (videoState) {
		case BUFFERING:
		case IDLE:
			break;
		case PAUSE:
			this.playerControl.start();
			break;
		case PLAYING:
			break;
		case PREPARED:
			break;
		case RELEASE:
			break;
		case STOP:
			break;
		case UNINIT:
			break;
		default:
			break;

		}
		videoState = VideoState.PLAYING;
		if (DEBUG) {
			V2Log.i(TAG, this+" "+ live+"  playing");
		}
	}

	@Override
	public void stop() {
		if (this.player != null) {
			this.player.stop();
			this.player.seekTo(0);
		}
		videoState = VideoState.STOP;
	}

	@Override
	public boolean isPlaying() {
		if (DEBUG) {
			V2Log.i(TAG, "current state:"+videoState);
		}
		return VideoState.PLAYING == videoState;
	}

	@Override
	public boolean isPause() {
		return VideoState.PAUSE == videoState;
	}

	@Override
	public Live getCurrentLive() {
		return live;
	}
	
	
	public void setIndex(int index) {
		this.mIndex = index;
	}
	
	
	


	public void setStateListener(VideoFragmentStateListener stateListener) {
		this.mStateListener = stateListener;
	}

	private void drawFirstBlankFrame(Canvas c) {
		//int[]  carray = new int[]{Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.RED, Color.WHITE};
		if (player != null && playerControl.isPlaying()) {
			return;
		}
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(60);
		tmp.drawText(mIndex + "", width / 2, height / 2, p);
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
	}

	private Surface surface;
	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Canvas c = holder.lockCanvas();
			V2Log.e("======w:" + c.getWidth()+"   h:"+ c.getHeight());
			drawFirstBlankFrame(c);
			holder.unlockCanvasAndPost(c);
			surface = holder.getSurface();
			if (player != null && videoRender != null) {
				player.blockingSendMessage(videoRender,
						MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
				surfacePushed = true;
				if (live != null) {
					play(live);
				}
			}
			if (mStateListener != null) {
				mStateListener.onInited();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			surface = holder.getSurface();
			if (player != null && videoRender != null) {
				player.blockingSendMessage(videoRender,
						MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
				surfacePushed = true;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (player != null) {
				if (playerControl.isPlaying()) {
					playerControl.pause();
					player.setRendererEnabled(0, false);
					player.setRendererEnabled(1, false);
					player.stop();
					player.seekTo(0);
				}
				
				if (videoRender != null) {
					player.blockingSendMessage(videoRender,
							MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, null);
				}
			}
			surface = null;
			surfacePushed = false;
		}

	};

	@Override
	public void onPlayWhenReadyCommitted() {

	}

	@Override
	public void onPlayerError(ExoPlaybackException error) {
		videoState = VideoState.STOP;
	}

	@Override
	public void onPlayerStateChanged(boolean changed, int state) {
		if (state == ExoPlayer.STATE_ENDED) {
			V2Log.d("play ended)" + live);
			player.setRendererEnabled(0, false);
			player.setRendererEnabled(1, false);
			player.stop();
			player.seekTo(0);
			live = null;
			videoState = VideoState.STOP;
		}
	}

	public void onLoadStarted(int sourceId, long length, int type, int trigger,
			Format format, int mediaStartTimeMs, int mediaEndTimeMs) {
		Log.w(TAG, "onLoadStarted : "+ this +" source : " + sourceId);
	};

	public void onLoadCompleted(int sourceId, long bytesLoaded, int type,
			int trigger, Format format, int mediaStartTimeMs,
			int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
		Log.w(TAG, "onLoadCompleted: "+ this +" source : " + sourceId);
	}

	public void onLoadCanceled(int sourceId, long bytesLoaded) {
		Log.w(TAG, "onLoadCanceled: "+ this +" source : " + sourceId);
	}

	public void onLoadError(int sourceId, IOException e) {
		Log.w(TAG, "onLoadError: "+ this +" source : " + sourceId);
	}

	public void onUpstreamDiscarded(int sourceId, int mediaStartTimeMs,
			int mediaEndTimeMs) {
		Log.w(TAG, "onUpstreamDiscarded: "+ this +" source : " + sourceId);
	}

	public void onDownstreamFormatChanged(int sourceId, Format format,
			int trigger, int mediaTimeMs) {
		Log.w(TAG, "onDownstreamFormatChanged: "+ this +" source : " + sourceId);
	}

	public void startAnimation(Animation a) {
		mSurfaceView.startAnimation(a);
	}

	public class HlsRendererBuilder implements ManifestCallback<HlsPlaylist> {

		private static final int REQUESTED_BUFFER_SIZE = 18 * 1024 * 1024;
		private static final long REQUESTED_BUFFER_DURATION_MS = 40000;

		private final Context context;
		private final String userAgent;
		private final String url;
		private final AudioCapabilities audioCapabilities;

		public HlsRendererBuilder(Context context, String userAgent,
				String url, AudioCapabilities audioCapabilities) {
			this.context = context;
			this.userAgent = userAgent;
			this.url = url;
			this.audioCapabilities = audioCapabilities;
		}

		public void buildRenderers() {
			HlsPlaylistParser parser = new HlsPlaylistParser();
			ManifestFetcher<HlsPlaylist> playlistFetcher = new ManifestFetcher<HlsPlaylist>(
					url, new DefaultUriDataSource(context, userAgent), parser);
			playlistFetcher.singleLoad(handlerThread.getLooper(), this);
		}

		@Override
		public void onSingleManifestError(IOException e) {
			e.printStackTrace();
		}

		@Override
		public void onSingleManifest(HlsPlaylist manifest) {
			DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

			int[] variantIndices = null;
			if (manifest instanceof HlsMasterPlaylist) {
				HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
				try {
					variantIndices = VideoFormatSelectorUtil
							.selectVideoFormatsForDefaultDisplay(context,
									masterPlaylist.variants, null, false);
				} catch (DecoderQueryException e) {
					e.printStackTrace();
					return;
				}
			}

			DataSource dataSource = new DefaultUriDataSource(context,
					bandwidthMeter, userAgent);
			HlsChunkSource chunkSource = new HlsChunkSource(dataSource, url,
					manifest, bandwidthMeter, variantIndices,
					HlsChunkSource.ADAPTIVE_MODE_SPLICE, audioCapabilities);
			HlsSampleSource sampleSource = new HlsSampleSource(chunkSource,
					true, 3, REQUESTED_BUFFER_SIZE,
					REQUESTED_BUFFER_DURATION_MS, localHandler,
					VideoShowFragment.this, 1);
			MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(
					sampleSource, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT,
					5000, localHandler, null, 50);
			MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
					sampleSource);

			TrackRenderer[] renderers = new TrackRenderer[2];
			renderers[0] = videoRenderer;
			renderers[1] = audioRenderer;

			videoRender = renderers[0];

			if (!surfacePushed) {
				player.blockingSendMessage(videoRender,
						MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
			}

			player.setRendererEnabled(0, true);
			player.setRendererEnabled(1, true);
			player.prepare(renderers);
			player.setPlayWhenReady(true);
			// FIXE should move this state to callback function
			videoState = VideoState.PLAYING;
		}

	}

}
