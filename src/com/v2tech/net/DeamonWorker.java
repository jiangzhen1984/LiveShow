package com.v2tech.net;


import android.util.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class DeamonWorker implements Runnable, NetAPI {
	
	NioEventLoopGroup group;
	Bootstrap strap;
	private State st;
	private Thread deamon;
	private Object stLock;
	private Object trLock;
	private int port;
	private String host;
	
	public DeamonWorker() {
		stLock = new Object();
		trLock = new Object();
		st = State.NONE;
		group = new NioEventLoopGroup();
		strap = new Bootstrap();
	}

	
	
	
	
	
	@Override
	public void run() {
		if (st != State.INITIALIZED)  {
			//FIXME notify error
			synchronized(stLock) {
				st = State.STOPPED;
			}
			return;
		}
		synchronized(stLock) {
			st = State.RUNNING;
		}
		
		try {
			Channel ch = strap.connect(host, port).sync().channel();
			while(st == State.RUNNING) {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized(stLock) {
				st = State.STOPPED;
				group.shutdownGracefully();
			}
		}
	}

	@Override
	public boolean connect(String host, int port) {
		if (st == State.RUNNING || (deamon != null && deamon.isAlive())) {
			throw new RuntimeException("Current worker is still running, please stop first");
		}
		
		synchronized(stLock) {
			if (st == State.NONE){
				strap.group(group)
		         .channel(NioSocketChannel.class).handler(new LocalChannel());
				st = State.INITIALIZED;
			}
			this.port = port;
			this.host = host;
	
			deamon = new Thread(this);
			deamon.start();
			while (st != State.RUNNING && st != State.STOPPED) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return st == State.RUNNING;
	}

	@Override
	public ResponsePacket request(RequestPacket packet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void requestAsync(RequestPacket packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		synchronized(stLock) {
			st = State.REQUEST_STOP;
		}
		synchronized(trLock) {
			trLock.notifyAll();
		}
		
	}
	
	
	enum State {
		NONE,
		INITIALIZED,
		RUNNING,
		REQUEST_STOP,
		STOPPED;
	}
	
	

	class LocalChannel extends ChannelInitializer<SocketChannel> {
		private  final StringDecoder DECODER = new StringDecoder();
	    private  final StringEncoder ENCODER = new StringEncoder();
	    
	
	
		public LocalChannel() {
		}
	
		@Override
		public void initChannel(SocketChannel ch) {
			ChannelPipeline pipeline = ch.pipeline();
	
	
	        // Add the text line codec combination first,
	        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
	        pipeline.addLast(DECODER);
	        pipeline.addLast(ENCODER);
	       // pipeline.addLast("ping", new IdleStateHandler(0, WRITE_WAIT_SECONDS, 0,TimeUnit.SECONDS));
	        // and then business logic.
	        pipeline.addLast(new ReaderChannel());
	
		}

	}
	
	
	
	class ReaderChannel extends SimpleChannelInboundHandler<String> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, String msg)
				throws Exception {
			Log.i("ReaderChannel", "111===>" + msg);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
		
	}

}
