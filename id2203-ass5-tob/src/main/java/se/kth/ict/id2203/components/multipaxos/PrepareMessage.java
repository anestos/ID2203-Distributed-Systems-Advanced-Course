package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends FplDeliver{

	private static final long serialVersionUID = -2581064400982342179L;
	private int ts;
	private int al;
	private int t;
	protected PrepareMessage(Address source, int ts, int al, int t) {
		super(source);
		this.ts = ts;
		this.al = al;
		this.t = t;
	}
	public int getTs(){
		return ts;
	}
	public int getAl(){
		return al;
	}
	public int getT(){
		return t;
	}

}
