package com.v2tech.map.baidu;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.v2tech.R;

public class RoadMapOverlay extends WalkingRouteOverlay {
	
	int startMarkerRes = 0;
	int terminalMarkerRes = 0;

	public RoadMapOverlay(BaiduMap bm) {
		super(bm);
	}

	
	 @Override
     public BitmapDescriptor getStartMarker() {
		 if (startMarkerRes > 0) {
			 return BitmapDescriptorFactory.fromResource(startMarkerRes);
		 }
         return BitmapDescriptorFactory.fromResource(R.drawable.watcher_location);
     }

     @Override
     public BitmapDescriptor getTerminalMarker() {
    	 if (terminalMarkerRes > 0) {
			 return BitmapDescriptorFactory.fromResource(terminalMarkerRes);
		 }
         return BitmapDescriptorFactory.fromResource(R.drawable.watcher_location);
     }
}
