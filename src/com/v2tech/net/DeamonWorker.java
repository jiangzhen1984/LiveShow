package com.v2tech.net;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import android.util.Log;

import com.V2.jni.util.V2Log;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.net.pkt.Transformer;


public class DeamonWorker implements Runnable, NetConnector {
	
	NioEventLoopGroup group;
	Bootstrap strap;
	private WorkerState st;
	private ConnectionState cs;
	private Thread deamon;
	private Object stLock;
	private Object trLock;
	private Object csLock;
	private int port;
	private String host;
	
	private Transformer<Packet, String> packetTransform;
	
	private Queue<LocalBind> pending;
	private Queue<LocalBind> waiting;
	
	private static DeamonWorker instance;
	
	private DeamonWorker() {
		stLock = new Object();
		trLock = new Object();
		csLock = new Object();
		st = WorkerState.NONE;
		cs = ConnectionState.IDLE;
		group = new NioEventLoopGroup();
		strap = new Bootstrap();
		pending = new PriorityBlockingQueue<LocalBind>();
		waiting = new PriorityBlockingQueue<LocalBind>();
	}

	
	public static synchronized DeamonWorker getInstance() {
		if (instance == null) {
			instance = new DeamonWorker();
		}
		return instance;
	}
	
	
	
	@Override
	public void run() {
		updateWorkerState(WorkerState.RUNNING);
		Channel ch = null;
		try {
			updateConnectionState(ConnectionState.CONNECTING);
			ch = strap.connect(host, port).sync().channel();
			if (!ch.isOpen()) {
				updateConnectionState(ConnectionState.ERROR);
				updateWorkerState(WorkerState.STOPPED);
				return;
			}
			
			while(st == WorkerState.RUNNING) {
				
				LocalBind lb = null;
				while ((lb = pending.poll()) != null) {
					String  data = packetTransform.serialize(lb.req);
					Log.e("DeamonWorker", "==>" + data);
					ChannelFuture cf = ch.writeAndFlush(data);
					cf.sync();
					synchronized(lb) {
						//FIXME if lb timeout
						lb.sendflag = true;
						lb.sendtime = System.currentTimeMillis();
					}
					waiting.offer(lb);
				}
				
				synchronized(trLock) {
					trLock.wait();
				}
			}
			
		} catch (Exception e) {
			//FIXME add recovery policy
			V2Log.e(e.getMessage());
			Log.e("DeamonWorker"," error :",e);
			e.printStackTrace();
			if (ch != null && !ch.isOpen()) {
				updateConnectionState(ConnectionState.ERROR);
			}
		} finally {
			if (st == WorkerState.REQUEST_STOP) {
				updateConnectionState(ConnectionState.DISCONNECTED);
			}
			updateWorkerState(WorkerState.STOPPED);
			group.shutdownGracefully();
		}
	}

	@Override
	public boolean connect(String host, int port) {
		if (cs == ConnectionState.CONNECTED) {
			return true;
		}
		boolean did = false;
		while (cs == ConnectionState.CONNECTING) {
			did = true;
			try {
				wait(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (did) {
			return cs == ConnectionState.CONNECTED;
		}
		
		if (st == WorkerState.RUNNING || (deamon != null && deamon.isAlive())) {
			throw new RuntimeException("Current worker is still running, please stop first");
		}
		
		this.port = port;
		this.host = host;
		
		startWorker();
		
		return cs == ConnectionState.CONNECTED;
	}

	@Override
	public ResponsePacket request(Packet packet) {
		if (packet instanceof IndicationPacket) {
			throw new IllegalArgumentException("Indication not allowed");
		}
		LocalBind ll = new LocalBind(packet);
		ll.sync = true;
		boolean ret = false;
		ret = pending.offer(ll);
		if (!ret) {
			ResponsePacket rp = new ResponsePacket();
			rp.getHeader().setError(true);
			rp.setRequestId(packet.getId());
			return rp;
		}
		notifyWorker();
		synchronized(ll) {
			try {
				Log.e("ReaderChannel", ll+"  to wait");
				ll.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return (ResponsePacket)ll.resp;
	}

	@Override
	public void requestAsync(PacketProxy packet) {
		LocalBind ll = new LocalBind(packet);
		boolean ret = false;
		ret = pending.offer(ll);
		if (!ret) {
			if (packet.getListener() != null) {
				ResponsePacket rp = new ResponsePacket();
				rp.getHeader().setError(true);
				rp.setRequestId(packet.getId());
				packet.getListener() .onResponse(rp);
			}
		}
		notifyWorker();
	}

	@Override
	public void disconnect() {
		updateWorkerState(WorkerState.REQUEST_STOP);
		
	}
	
	@Override
	public boolean isConnected() {
		return cs == ConnectionState.CONNECTED;
	}
	
	
	@Override
	public void setPacketTransformer(Transformer<Packet, String> transformer) {
		this.packetTransform = transformer;
	}


	

	private void startWorker() {
	
		if (cs == ConnectionState.IDLE) {
			strap.group(group).channel(NioSocketChannel.class)
					.handler(new LocalChannel());

		}
		
		updateWorkerState(WorkerState.INITIALIZED);
		deamon = new Thread(this);
		deamon.start();
		int count = 0;
		while (!deamon.isAlive() && (cs != ConnectionState.CONNECTED) && count++ < 10) {
			try {
				wait(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void updateWorkerState(WorkerState ws) {
		synchronized (stLock) {
			st = ws;
		}
	}
	
	private void updateConnectionState(ConnectionState ws) {
		synchronized (csLock) {
			cs = ws;
		}
	}
	
	private void notifyWorker() {

		synchronized(trLock) {
			trLock.notifyAll();
		}
	}
	
	
	private LocalBind findRequestBind(Packet packet) {
		LocalBind lb = null;
		Iterator<LocalBind> it = waiting.iterator();
		while (it.hasNext()) {
			lb = it.next();
			if (lb.reqId == packet.getId()) {
				waiting.remove(lb);
			}
		}
		return lb;
	}
	
	
	private void handleResponseBind(LocalBind req, Packet resp) {
		V2Log.i("["+req.reqId+"]===> message statist queue cost : " +(req.sendtime - req.sendtime)+"   server cost:" +(req.respontime - req.sendtime));
		req.resp = resp;
		if (req.sync) {
			synchronized(req) {
				Log.e("ReaderChannel", req+"  to notify");
				req.notify();
			}
		} else if (((PacketProxy)req.req).getListener()  != null) {
			((PacketProxy)req.req).getListener().onResponse((ResponsePacket)req.resp);
		}
	}
	
	enum WorkerState {
		NONE,
		INITIALIZED,
		RUNNING,
		REQUEST_STOP,
		STOPPED;
	}
	
	
	enum ConnectionState {
		IDLE,
		CONNECTING,
		CONNECTED,
		DISCONNECTED,
		ERROR,
		
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
			Log.e("ReaderChannel", msg);
			if (packetTransform != null) {
				Packet p = packetTransform.unserializeFromStr(msg);
				if (p == null) {
					Log.e("ReaderChannel", " Parser error");
					return;
				}
				LocalBind lb = findRequestBind(p);
				Log.e("ReaderChannel", "local bind:" + lb);
				if (lb != null) {
					lb.respontime = System.currentTimeMillis();
					handleResponseBind(lb, p);
				} else {
					if (p instanceof IndicationPacket) {
						((PacketProxy)p).getListener().onNodification((IndicationPacket)p);
					}
				}
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
		
	}
	
	
	
	class LocalBind implements Comparable<LocalBind>  {
		
		long reqId;
		Packet req;
		Packet resp;
		boolean sync;
		boolean timeout;
		long sendtime;
		long queuetime;
		long respontime;
		boolean sendflag;
		
		public LocalBind(Packet req) {
			super();
			this.req = req;
			this.reqId = req.getId();
			queuetime = System.currentTimeMillis();
		}




		public LocalBind(Packet req, Packet resp) {
			super();
			this.req = req;
			this.resp = resp;
			queuetime = System.currentTimeMillis();
		}




		@Override
		public int compareTo(LocalBind another) {
			return this.req.compareTo(another.req);
		}
		
		
	}

}
