package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends FplDeliver{

	private static final long serialVersionUID = 3072271507387026398L;
	private int pts;
	private ArrayList<Object> v;
	private int size;
	private int t;
	protected AcceptMessage(Address source, int pts, ArrayList<Object> hv, int size, int t) {
		super(source);
		this.pts = pts;
		this.v = hv;
		this.size = size;
		this.t = t;
		// TODO Auto-generated constructor stub
	}
	public int getPts(){
		return pts;
	}
	public ArrayList<Object> getV(){
		return v;
	}
	public int getSize(){
		return size;
	}
	public int getT(){
		return t;
	}

}
