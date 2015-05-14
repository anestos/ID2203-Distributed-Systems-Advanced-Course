package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import java.util.List;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends FplDeliver{

	private static final long serialVersionUID = 3072271507387026398L;
	private int ts;
	private ArrayList<Object> usuf;
	private int offs;
	private int t;
	protected AcceptMessage(Address source, int ts, ArrayList<Object> usuf, int offs, int t) {
		super(source);
		this.ts = ts;
		this.usuf = usuf;
		this.offs = offs;
		this.t = t;
		// TODO Auto-generated constructor stub
	}
	public int getTs(){
		return ts;
	}
	public ArrayList<Object> getUsuf(){
		return usuf;
	}
	public int getOffs(){
		return offs;
	}
	public int getT(){
		return t;
	}

}
