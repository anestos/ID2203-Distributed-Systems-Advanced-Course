package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class Value extends Pp2pDeliver{
	private static final long serialVersionUID = -5089068488908989104L;
	private int r;
	private int ts;
	private int wr;
	private int v;
	
	protected Value(Address source, int r, int ts, int wr, int v) {
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
