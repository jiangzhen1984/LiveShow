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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.V2.jni.util.V2Log;
import com.v2tech.net.lv.WebPackage;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.net.pkt.Transformer;

public class DeamonWorker implements Runnable, NetConnector,
		TimeoutNotificator.TimeoutHandler {

	NioEventLoopGroup group;
	Bootstrap strap;
	private WorkerState st;
	private ConnectionState cs;
	private Thread deamon;
	private TimeoutNotificator tiemoutWatchDog;
	private Object stLock;
	private Object trLock;
	private Object csLock;
	private int port;
	private String host;

	private Transformer<Packet, WebPackage.Packet> packetTransform;
	private List<WeakReference<NotificationListener>> callbacks;

	private Queue<LocalBind> pending;
	private Queue<LocalBind> waiting;

	private static DeamonWorker instance;
	private ReconnectThread reconnectThread;

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
		tiemoutWatchDog = new TimeoutNotificator(this, waiting);
		
		callbacks = new ArrayList<WeakReference<NotificationListener>>(10);
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
			updateConnectionState(ConnectionState.CONNECTED);
			while (st == WorkerState.RUNNING) {
				LocalBind lb = null;
				while ((lb = pending.poll()) != null) {
					WebPackage.Packet data = packetTransform.serialize(lb.req);
					Log.i("DeamonWorker", "write==>" + data);
					ChannelFuture cf = ch.writeAndFlush(data);
					cf.sync();
					synchronized (lb) {
						// FIXME if lb timeout
						lb.sendflag = true;
						lb.sendtime = System.currentTimeMillis();
					}
					waiting.offer(lb);
				}

				synchronized (trLock) {
					trLock.wait();
				}
			}

		} catch (Exception e) {
			V2Log.e(e.getMessage());
			Log.e("DeamonWorker", " error :", e);
			e.printStackTrace();
			if (ch != null && !ch.isOpen()) {
				updateConnectionState(ConnectionState.ERROR);
			}
			if (reconnectThread == null || !reconnectThread.isAlive()) {
				reconnectThread = new ReconnectThread();
				reconnectThread.start();
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
				synchronized (this) {
					wait(50);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (did) {
			return cs == ConnectionState.CONNECTED;
		}

		if (st == WorkerState.RUNNING || (deamon != null && deamon.isAlive())) {
			throw new RuntimeException(
					"Current worker is still running, please stop first");
		}

		this.port = port;
		this.host = host;

		startWorker();

		tiemoutWatchDog.requestStart();
		return cs == ConnectionState.CONNECTED;
	}

	@Override
	public ResponsePacket request(Packet packet) {
		if (packet instanceof IndicationPacket) {
			throw new IllegalArgumentException("Indication not allowed");
		}
		if (cs != ConnectionState.CONNECTED) {
			V2Log.w("====> no network:" + cs);
			ResponsePacket rp = new ResponsePacket();
			rp.getHeader().setError(true);
			rp.setRequestId(packet.getId());
			return rp;
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
		synchronized (ll) {
			try {
				Log.e("ReaderChannel", Thread.currentThread() + "==>" + ll
						+ "  to wait");
				ll.wait();
				Log.e("ReaderChannel", Thread.currentThread() + "==>" + ll
						+ "  restore ");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return (ResponsePacket) ll.resp;
	}

	@Override
	public void requestAsync(PacketProxy packet) {
		if (cs != ConnectionState.CONNECTED) {
			if (packet.getListener() != null) {
				ResponsePacket rp = new ResponsePacket();
				rp.getHeader().setError(true);
				rp.setRequestId(packet.getId());
				packet.getListener().onResponse(rp);
			}
		}
		
		LocalBind ll = new LocalBind(packet);
		boolean ret = false;
		ret = pending.offer(ll);
		if (!ret) {
			if (packet.getListener() != null) {
				ResponsePacket rp = new ResponsePacket();
				rp.getHeader().setError(true);
				rp.setRequestId(packet.getId());
				packet.getListener().onResponse(rp);
			}
		}
		notifyWorker();
	}

	@Override
	public void disconnect() {
		updateWorkerState(WorkerState.REQUEST_STOP);
		tiemoutWatchDog.requestStop();
	}

	@Override
	public boolean isConnected() {
		return cs == ConnectionState.CONNECTED;
	}

	public void setPacketTransformer(Transformer<Packet, WebPackage.Packet> transformer) {
		this.packetTransform = transformer;
	}

	@Override
	public void addNotificationListener(NotificationListener listener) {
		if (listener == null) {
			throw new RuntimeException(" try to add null listener");
		}
		callbacks.add(new WeakReference<NotificationListener>(listener));
	}
	
	@Override
	public void removeNotificationListener(NotificationListener listener) {
		int size = callbacks.size();
		for (int i = 0; i < size; i++) {
			if (callbacks.get(i).get() == listener) {
				callbacks.remove(i);
				break;
			}
		}
	}

	@Override
	public void onTimeout(LocalBind bind) {
		this.waiting.remove(bind);
		V2Log.e("Timeout " + bind);
		V2Log.e(this.waiting.toString());
		ResponsePacket rp = new ResponsePacket();
		rp.setRequestId(bind.reqId);
		rp.setErrorFlag(true);
		bind.resp = rp;
		if (bind.sync) {
			synchronized (bind) {
				bind.notify();
			}
		} else {
			if (!(bind.req instanceof PacketProxy)) {
				V2Log.e("==================");
			} else {
				PacketProxy pp = (PacketProxy) bind.req;
				if (pp != null && pp.getListener() != null) {
					pp.getListener().onTimeout(rp);
				}
			}
		}
	}

	private void startWorker() {

		if (cs == ConnectionState.IDLE) {
			strap.group(group).channel(NioSocketChannel.class)
					.handler(new LocalChannel());

		}

		updateWorkerState(WorkerState.INITIALIZED);
		deamon = new Thread(this);
		deamon.setName("DeamonWorker");
		deamon.start();
		int count = 0;
		while (!deamon.isAlive() && (cs != ConnectionState.CONNECTED)
				&& count++ < 10) {
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
			Log.i("DeamonWorker", " old cs:" + cs+"   new cs:"+ws);
			cs = ws;
		}
	}

	private void notifyWorker() {

		synchronized (trLock) {
			trLock.notifyAll();
		}
	}

	private LocalBind findRequestBind(ResponsePacket packet) {
		LocalBind lb = null;
		Iterator<LocalBind> it = waiting.iterator();
		while (it.hasNext()) {
			lb = it.next();
			if (lb.reqId == packet.getRequestId()) {
				it.remove();
				waiting.remove(lb);
				break;
			}
		}
		return lb;
	}

	private void handleResponseBind(LocalBind req, Packet resp) {
		V2Log.i("[" + req.reqId + "]===> message statist queue cost : "
				+ (req.sendtime - req.sendtime) + "   server cost:"
				+ (req.respontime - req.sendtime));
		req.resp = resp;
		if (req.sync) {
			synchronized (req) {
				Log.e("ReaderChannel", req + "  to notify");
				req.notify();
			}
		} else if (((PacketProxy) req.req).getListener() != null) {
			((PacketProxy) req.req).getListener().onResponse(
					(ResponsePacket) req.resp);
		}
	}
	
	
	private void sendNotificaiton(IndicationPacket p) {
		int size = callbacks.size();
		for (int i = 0; i < size; i++) {
			NotificationListener listener = callbacks.get(i).get();
			if (listener != null) {
				listener.onNodification(p);
			}
		}
	}

	enum WorkerState {
		NONE, INITIALIZED, RUNNING, REQUEST_STOP, STOPPED;
	}

	enum ConnectionState {
		IDLE, CONNECTING, CONNECTED, DISCONNECTED, ERROR,

	}

	class ReconnectThread extends Thread {

		@Override
		public void run() {
			while (cs != ConnectionState.CONNECTED) {
				Log.i("ReaderChannel", "try to reconnect====>" + cs);
				if (st == WorkerState.RUNNING
						|| (deamon != null && deamon.isAlive())) {
					Log.i("ReaderChannel", "try to disconnect====>" + st + "  "
							+ deamon);
					disconnect();
					try {
						synchronized (this) {
							wait(10000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				connect(host, port);
				try {
					synchronized (this) {
						wait(30000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class LocalChannel extends ChannelInitializer<SocketChannel> {
//		private final StringDecoder DECODER = new StringDecoder();
//		private final StringEncoder ENCODER = new StringEncoder();

		public LocalChannel() {
		}

		@Override
		public void initChannel(SocketChannel ch) {
			ChannelPipeline p = ch.pipeline();

			// // Add the text line codec combination first,
			// pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters
			// .lineDelimiter()));
			// pipeline.addLast(DECODER);
			// pipeline.addLast(ENCODER);
			// pipeline.addLast("ping", new IdleStateHandler(0, 10, 0,
			// TimeUnit.SECONDS));
			// // and then business logic.
			// pipeline.addLast(new ReaderChannel());

			p.addLast(new ProtobufVarint32FrameDecoder());
			p.addLast(new ProtobufDecoder(WebPackage.Packet
					.getDefaultInstance()));
			p.addLast(new ProtobufVarint32LengthFieldPrepender());
			p.addLast("ping", new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));

			p.addLast(new ProtobufEncoder());
			p.addLast(new ReaderChannel());

		}

	}

	class ReaderChannel extends SimpleChannelInboundHandler<WebPackage.Packet> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, WebPackage.Packet pack)
				throws Exception {
			if (pack == null) {
				return;
			}
			if (packetTransform != null) {
				Packet p = packetTransform.unserialize(pack);
				if (p == null) {
					Log.e("ReaderChannel", " Parser error");
					return;
				}
				Log.i("ReaderChannel", "transform====>" + p);
				if (p instanceof IndicationPacket) {
					Log.i("ReaderChannel", "notification====>" + p);
					sendNotificaiton((IndicationPacket) p);
					return;
				}
				LocalBind lb = findRequestBind((ResponsePacket) p);
				Log.i("ReaderChannel", "local bind:" + lb);
				if (p.getHeader().isError()) {
					Log.e("ReaderChannel", "error message:" + p.getHeader().getErrorMsg());
				}
				if (lb != null) {
					lb.respontime = System.currentTimeMillis();
					handleResponseBind(lb, p);
				}
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			Log.e("ReaderChannel", "Exception:" + cause);
			cause.printStackTrace();
			ctx.close();
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {

				WebPackage.Packet.Builder packet = WebPackage.Packet
						.newBuilder();
				packet.setPacketType(WebPackage.Packet.type.beat);

				ChannelFuture cf = ctx.channel().writeAndFlush(packet.build());
				cf.sync();
			}
		}
	}

}
