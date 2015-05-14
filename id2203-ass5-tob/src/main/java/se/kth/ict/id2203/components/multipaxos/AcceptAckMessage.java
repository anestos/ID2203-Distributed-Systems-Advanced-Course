package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class AcceptAckMessage extends FplDeliver{

	private static final long serialVersionUID = 4697999641462106216L;
	private int pts;
	private int l;
	private int t;
	protected AcceptAckMessage(Address source, int pts, int l, int t) {
		super(source);
		this.pts = pts;
		this.l = l;
		this.t = t;
	}
	public int getPts(){
		return pts;
	}
	public int getL(){
		return l;
	}
	public int getT(){
		return t;
	}

}
