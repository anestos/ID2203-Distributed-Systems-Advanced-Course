package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class BroadcastRead extends BebDeliver{
	private int rid;
	public BroadcastRead(Address source, int rid) {
		super(source);
		this.rid = rid;
		
	}
	public int getRid(){
		return rid;
	}

}
