package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class NackMessage extends Pp2pDeliver {

	private static final long serialVersionUID = -6373008569751176594L;
	
	private int pts;
	private int t;
	
	public NackMessage(Address source, int pts, int t) {
		super(source);
		this.pts=pts;
		this.t=t;
	}
	public int getT(){
		return t;
	}
	public int getPts(){
		return pts;
	}
}
