package com.v2tech.net.pkt;


public abstract class Packet implements Comparable<Packet> {
	
	protected Header header;
	
	protected long id;
	
	protected int prioirty;
	
	
	protected Packet () {
		this.header = new Header();
		this.id = header.id;
	}
	
	
	public void setErrorFlag(boolean flag) {
		this.header.setError(flag);
	}
	
	public Header getHeader() {
		return this.header;
	}
	
	

	public long getId() {
		return id;
	}


	@Override
	public int compareTo(Packet another) {
		if (this == another) {
			return 0;
		}
		if (prioirty > another.prioirty) {
			return -1;
		} else if (prioirty == another.prioirty) {
			return 0;
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
