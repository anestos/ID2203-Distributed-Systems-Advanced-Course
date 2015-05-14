package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class DecideMessage extends FplDeliver{

	private static final long serialVersionUID = -3904595327646843088L;
	private int ts;
	private int l;
	private int t;
	
	protected DecideMessage(Address source, int ts, int l, int t) {
		super(source);
		this.ts = ts;
		this.l = l;
		this.t = t;
		// TODO Auto-generated constructor stub
	}
	public int getTs(){
		return ts;
	}
	public int getL(){
		return l;
	}
	public int getT(){
		return t;
	}

}
