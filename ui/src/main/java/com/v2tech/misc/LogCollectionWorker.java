package com.v2tech.misc;

//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
//import io.netty.handler.codec.http.multipart.HttpDataFactory;
//
//import java.io.File;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import com.V2.jni.util.V2Log;
//import com.v2tech.net.HttpUploadClient;
//import com.v2tech.net.HttpUploadClient.HttpUploadClientIntializer;
//import com.v2tech.util.GlobalConfig;
//import com.v2tech.view.Constants;

public class LogCollectionWorker extends Thread {

	public LogCollectionWorker() {

	}

	@Override
	public void run() {
//		EventLoopGroup group = new NioEventLoopGroup();
//		Bootstrap b = new Bootstrap();
//		b.group(group).channel(NioSocketChannel.class)
//				.handler(new HttpUploadClientIntializer(null));
//
//
//		try {
//			URI uriFile = new URI("http://" + Constants.N_SERVER+":9998/formpostmultipart");
//
//			HttpDataFactory factory = new DefaultHttpDataFactory(
//					DefaultHttpDataFactory.MINSIZE);
//			File cl = new File( GlobalConfig.getGlobalCrashPath()+"/v2tech/crash/");
//			if (cl.isDirectory()) {
//				String[] files = cl.list();
//				File crash = null;
//				for (String name : files) {
//					crash = new File(cl.getAbsoluteFile()+"/" + name);
//					V2Log.i("===>upload log "+ crash.getAbsolutePath());
//					try {
//						HttpUploadClient.formpostmultipart(b, Constants.N_SERVER, 9998, uriFile, factory, crash);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					crash.delete();
//				}
//			}
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
	}

}
