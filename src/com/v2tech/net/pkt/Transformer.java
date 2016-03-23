package com.v2tech.net.pkt;

public interface Transformer<F, T> {

	public T serialize(F f);

	public F unserializeFromStr(T t);
	
	
}
