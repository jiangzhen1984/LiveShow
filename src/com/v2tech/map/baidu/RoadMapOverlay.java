package com.v2tech.map.baidu;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.v2tech.v2liveshow.R;

public class RoadMapOverlay extends WalkingRouteOverlay {

	public RoadMapOverlay(BaiduMap bm) {
		super(bm);
	}

	
	 @Override
     public BitmapDescriptor getStartMarker() {
         return BitmapDescriptorFactory.fromResource(R.drawable.watcher_location);
     }

     @Override
     public BitmapDescriptor getTerminalMarker() {
         return BitmapDescriptorFactory.fromResource(R.drawable.watcher_location);
     }
}
