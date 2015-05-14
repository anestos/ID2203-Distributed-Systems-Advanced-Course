package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class Write extends BebDeliver{
	private static final long serialVersionUID = -3623539625554116939L;
	private int r;
	private int ts;
	private int wr;
	private int v;
	
	public Write(Address source, int r, int ts, int wr, int v) {
		super(source);
		this.r = r;
		this.ts = ts;
		this.wr = wr;
		this.v = v;
	}
	
	public int getR(){
		return r;
	}
	public int getTs(){
		return ts;
	}
	public int getWr(){
		return wr;
	}
	public int getV(){
		return v;
	}

}
