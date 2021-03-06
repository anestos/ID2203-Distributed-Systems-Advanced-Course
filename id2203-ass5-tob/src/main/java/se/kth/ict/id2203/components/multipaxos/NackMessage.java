package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class NackMessage extends FplDeliver{
	
	private static final long serialVersionUID = 4904584634495941815L;
	private int ts;
	private int t;
	protected NackMessage(Address source, int ts, int t) {
		super(source);
		this.ts = ts;
		this.t = t;
	}

	public int getTs(){
		return ts;
	}
	public int getT(){
		return t;
	}
}
