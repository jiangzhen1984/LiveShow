package com.v2tech.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import com.V2.jni.util.V2Log;

/**
 * This class is meant to be run against {@link HttpUploadServer}.
 */
public final class HttpUploadClient {

	// static final String BASE_URL = System.getProperty("baseUrl",
	// "http://127.0.0.1:8080/");
	static final String BASE_URL = System.getProperty("baseUrl",
			"http://115.28.100.110:9998/");

	// static final String FILE = System.getProperty("file", "upload.txt");

	public static void main(String[] args) throws Exception {
		String postSimple, postFile, get;
		if (BASE_URL.endsWith("/")) {
			postSimple = BASE_URL + "formpost";
			postFile = BASE_URL + "formpostmultipart";
			get = BASE_URL + "formget";
		} else {
			postSimple = BASE_URL + "/formpost";
			postFile = BASE_URL + "/formpostmultipart";
			get = BASE_URL + "/formget";
		}

		URI uriSimple = new URI(postSimple);
		String scheme = uriSimple.getScheme() == null ? "http" : uriSimple
				.getScheme();
		String host = uriSimple.getHost() == null ? "127.0.0.1" : uriSimple
				.getHost();
		int port = uriSimple.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme)
				&& !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		final boolean ssl = "https".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} else {
			sslCtx = null;
		}

		URI uriFile = new URI(postFile);//
		// File file = new File("E:"+File.separatorChar+"Chrysanthemum.jpg");
		File file = new File("E:" + File.separatorChar + "error.log");
		if (!file.canRead()) {
			throw new FileNotFoundException();
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();

		// setup the factory: here using a mixed memory/disk based on size
		// threshold
		HttpDataFactory factory = new DefaultHttpDataFactory(
				DefaultHttpDataFactory.MINSIZE); // Disk if MINSIZE exceed

		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new HttpUploadClientIntializer(sslCtx));

//			// Simple Get form: no factory used (not usable)
//			List<Entry<String, String>> headers = formget(b, host, port, get,
//					uriSimple);
//			if (headers == null) {
//				factory.cleanAllHttpDatas();
//				return;
//			}
//
//			// Simple Post form: factory used for big attributes
//			List<InterfaceHttpData> bodylist = formpost(b, host, port,
//					uriSimple, file, factory, headers);
//			if (bodylist == null) {
//				factory.cleanAllHttpDatas();
//				return;
//			}

			// Multipart Post form: factory used
			formpostmultipart(b, host, port, uriFile, factory, file);
		} finally {
			// Shut down executor threads to exit.
			group.shutdownGracefully();

			// Really clean all temporary files if they still exist
			factory.cleanAllHttpDatas();
		}
	}

	/**
	 * Standard usage of HTTP API in Netty without file Upload (get is not able
	 * to achieve File upload due to limitation on request size).
	 * 
	 * @return the list of headers that will be used in every example after
	 **/
	private static List<Entry<String, String>> formget(Bootstrap bootstrap,
			String host, int port, String get, URI uriSimple) throws Exception {
		// XXX /formget
		// No use of HttpPostRequestEncoder since not a POST
		Channel channel = bootstrap.connect(host, port).sync().channel();

		// Prepare the HTTP request.
		QueryStringEncoder encoder = new QueryStringEncoder(get);
		// add Form attribute
		encoder.addParam("getform", "GET");
		encoder.addParam("info1", "92");
		encoder.addParam("info2", "15811084491");
		// not the big one since it is not compatible with GET size
		// encoder.addParam("thirdinfo", textArea);
		encoder.addParam("thirdinfo",
				"third value\r\ntest second line\r\n\r\nnew line\r\n");
		encoder.addParam("Send", "Send");

		URI uriGet = new URI(encoder.toString());
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.GET, uriGet.toASCIIString());
		HttpHeaders headers = request.headers();
		headers.set(HttpHeaders.Names.HOST, host);
		headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP
				+ ',' + HttpHeaders.Values.DEFLATE);

		headers.set(HttpHeaders.Names.ACCEPT_CHARSET,
				"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		headers.set(HttpHeaders.Names.ACCEPT_LANGUAGE, "fr");
		headers.set(HttpHeaders.Names.REFERER, uriSimple.toString());
		headers.set(HttpHeaders.Names.USER_AGENT,
				"Netty Simple Http Client side");
		headers.set(HttpHeaders.Names.ACCEPT,
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		// connection will not close but needed
		// headers.set("Connection","keep-alive");
		// headers.set("Keep-Alive","300");

		headers.set(HttpHeaders.Names.COOKIE, ClientCookieEncoder.encode(
				new DefaultCookie("my-cookie", "foo"), new DefaultCookie(
						"another-cookie", "bar")));

		// send request
		List<Entry<String, String>> entries = headers.entries();
		channel.writeAndFlush(request);

		// Wait for the server to close the connection.
		channel.closeFuture().sync();

		return entries;
	}

	/**
	 * Standard post without multipart but already support on Factory (memory
	 * management)
	 * 
	 * @return the list of HttpData object (attribute and file) to be reused on
	 *         next post
	 */
	public static List<InterfaceHttpData> formpost(Bootstrap bootstrap,
			String host, int port, URI uriSimple, File file,
			HttpDataFactory factory, List<Entry<String, String>> headers)
			throws Exception {
		// XXX /formpost
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));
		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.sync().channel();

		// Prepare the HTTP request.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, uriSimple.toASCIIString());

		// Use the PostBody encoder
		HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(
				factory, request, false); // false => not multipart

		// it is legal to add directly header or cookie into the request until
		// finalize
		for (Entry<String, String> entry : headers) {
			request.headers().set(entry.getKey(), entry.getValue());
		}

		// add Form attribute
		bodyRequestEncoder.addBodyAttribute("getform", "POST");
		bodyRequestEncoder.addBodyAttribute("info1", "92");
		bodyRequestEncoder.addBodyAttribute("info2", "15811084491");
		bodyRequestEncoder.addBodyFileUpload("myfile", file,
				"application/x-zip-compressed", false);

		// finalize request
		request = bodyRequestEncoder.finalizeRequest();

		// Create the bodylist to be reused on the last version with Multipart
		// support
		List<InterfaceHttpData> bodylist = bodyRequestEncoder
				.getBodyListAttributes();

		// send request
		channel.write(request);

		// test if request was chunked and if so, finish the write
		if (bodyRequestEncoder.isChunked()) { // could do either
												// request.isChunked()
			// either do it through ChunkedWriteHandler
			channel.write(bodyRequestEncoder);
		}
		channel.flush();

		// Do not clear here since we will reuse the InterfaceHttpData on the
		// next request
		// for the example (limit action on client side). Take this as a
		// broadcast of the same
		// request on both Post actions.
		//
		// On standard program, it is clearly recommended to clean all files
		// after each request
		// bodyRequestEncoder.cleanFiles();

		// Wait for the server to close the connection.
		channel.closeFuture().sync();
		return bodylist;
	}

	/**
	 * Multipart example
	 */
	public static void formpostmultipart(Bootstrap bootstrap, String host,
			int port, URI uri, HttpDataFactory factory,
			File file) throws Exception {
		// XXX /formpostmultipart
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));
		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.sync().channel();

		// Prepare the HTTP request.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, uri.toASCIIString());

		// Use the PostBody encoder
		HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(
				factory, request, true); // true => multipart

		// it is legal to add directly header or cookie into the request until
		// finalize
		
		HttpHeaders headers = request.headers();
		headers.set(HttpHeaders.Names.HOST, host);
		headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP
				+ ',' + HttpHeaders.Values.DEFLATE);

		headers.set(HttpHeaders.Names.ACCEPT_CHARSET,
				"utf-8,utf-8;q=0.7,*;q=0.7");
		headers.set(HttpHeaders.Names.ACCEPT,
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.headers().set("info1", "92");
		request.headers().set("info2", "15811084491");
		
		
		
		bodyRequestEncoder.addBodyAttribute("info1", "92");
		bodyRequestEncoder.addBodyAttribute("info2", "15811084491");
		bodyRequestEncoder.addBodyFileUpload("myfile", file,
				"application/x-zip-compressed", false);

		// add Form attribute from previous request in formpost()
		bodyRequestEncoder.setBodyHttpDatas(bodyRequestEncoder.getBodyListAttributes());

		// finalize request
		bodyRequestEncoder.finalizeRequest();

		// send request
		channel.write(request);

		// test if request was chunked and if so, finish the write
		if (bodyRequestEncoder.isChunked()) {
			channel.write(bodyRequestEncoder);
		}
		channel.flush();

		// Now no more use of file representation (and list of HttpData)
		bodyRequestEncoder.cleanFiles();

		// Wait for the server to close the connection.
		channel.closeFuture().sync();
	}

	public static class HttpUploadClientIntializer extends
			ChannelInitializer<SocketChannel> {

		private final SslContext sslCtx;

		public HttpUploadClientIntializer(SslContext sslCtx) {
			this.sslCtx = sslCtx;
		}

		@Override
		public void initChannel(SocketChannel ch) {
			ChannelPipeline pipeline = ch.pipeline();

			if (sslCtx != null) {
				pipeline.addLast("ssl", sslCtx.newHandler(ch.alloc()));
			}

			pipeline.addLast("codec", new HttpClientCodec());

			// Remove the following line if you don't want automatic content
			// decompression.
			pipeline.addLast("inflater", new HttpContentDecompressor());

			// to be used since huge file transfer
			pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

			pipeline.addLast("handler", new HttpUploadClientHandler());
		}
	}

	public static class HttpUploadClientHandler extends
			SimpleChannelInboundHandler<HttpObject> {

		private boolean readingChunks;

		@Override
		public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
			if (msg instanceof HttpResponse) {
				HttpResponse response = (HttpResponse) msg;
				V2Log.i("STATUS: " + response.getStatus());
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.channel().close();
		}
	}
}
