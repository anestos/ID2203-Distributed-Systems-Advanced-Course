package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import java.util.List;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class PrepareAckMessage extends FplDeliver{
	
	private static final long serialVersionUID = 4904584634495941815L;
	private int ts;
	private int t;
	private int l;
	private int pts;
	private ArrayList<Object> usuf;

	
	
	protected PrepareAckMessage(Address source, int pts, int ts, ArrayList<Object> usuf, int l, int t) {
		super(source);
		this.ts = ts;
		this.pts = pts;
		this.l = l;
		this.t = t;
		this.usuf = usuf;
	}

	

	public int getTs(){
		return ts;
	}
	public int getT(){
		return t;
	}
	public int getPts(){
		return pts;
	}
	public int getL(){
		return l;
	}
	public ArrayList<Object> getUsuf(){
		return usuf;
	}
}
