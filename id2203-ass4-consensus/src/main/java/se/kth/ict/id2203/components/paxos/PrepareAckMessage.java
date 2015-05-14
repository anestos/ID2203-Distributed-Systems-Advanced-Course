package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class PrepareAckMessage extends Pp2pDeliver {

	private static final long serialVersionUID = -6373008569751176594L;
	private int ts;
	private int av;
	private int pts;
	private int t;
	
	public PrepareAckMessage(Address source, int ts, int av, int pts, int t) {
		super(source);
		this.ts = ts;
		this.av = av;
		this.pts = pts;
		this.t = t;
	}
	
	public int getTs(){
		return ts;
	}
	
	public int getAv(){
		return av;
	}
	
	public int getPts(){
		return pts;
	}
	
	public int getT(){
		return t;
	}
}
