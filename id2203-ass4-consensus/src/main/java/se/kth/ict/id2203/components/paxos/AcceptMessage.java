package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends BebDeliver {

	private static final long serialVersionUID = -6373008569751176594L;

	private int ts;
	private int v;
	private int t;
	
	public AcceptMessage(Address source, int ts, int v, int t) {
		super(source);
		this.ts = ts;
		this.v = v;
		this.t = t;
	}
	
	public int getTs(){
		return ts;
	}
	public int getV(){
		return v;
	}
	public int getT(){
		return t;
	}
}
