package com.v2tech.net.lv;

import java.util.List;
import java.util.Map;

import com.v2tech.net.pkt.ResponsePacket;

public class WatcherListQueryRespPacket extends ResponsePacket {

	
	public List<Map<String, String>> watcherList;
}
