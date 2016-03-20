package com.v2tech.net;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Packet implements Comparable<Packet> {
	
	private static AtomicLong atomic = new AtomicLong();
	
	protected long id;
	
	protected long timestamp;

	protected int prioirty;
	
	
	protected Packet () {
		id = atomic.getAndIncrement();
	}

	@Override
	public int compareTo(Packet another) {
		if (this == another) {
			return 0;
		}
		if (prioirty > another.prioirty) {
			return -1;
		} else if (prioirty == another.prioirty) {
			if (timestamp > another.timestamp) {
				return 1;
			} else if (timestamp == another.timestamp) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return 1;
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Packet other = (Packet) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
	
	
}
