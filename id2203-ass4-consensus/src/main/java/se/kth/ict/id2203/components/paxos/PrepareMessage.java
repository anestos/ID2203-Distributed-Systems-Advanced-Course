package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends BebDeliver {

	private static final long serialVersionUID = 3263548422502840119L;
	private int pts;
	private int t;
	
	public PrepareMessage(Address source, int pts, int t) {
		super(source);
		this.pts =pts;
		this.t = t;
	}
	
	
	public int getPts(){
		return pts;
	}
	
	public int getT(){
		return t;
	}
}
