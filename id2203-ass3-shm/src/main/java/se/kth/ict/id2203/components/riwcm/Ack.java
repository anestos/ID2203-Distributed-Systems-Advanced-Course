package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class Ack extends Pp2pDeliver{
private static final long serialVersionUID = 2113327960566606308L;
	private int r;
	
	protected Ack(Address source, int r) {
		super(source);
		this.r = r;
	}

	public int getR(){
		return r;
	}
}
