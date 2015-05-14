package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatRequestMessage extends Pp2pDeliver{

	private static final long serialVersionUID = 6688727985149323339L;
	
	private int seqnum;
	
	protected HeartbeatRequestMessage(Address source, int seqnum) {
		super(source);
		this.seqnum = seqnum;
	}

	public int getSeqnum() {
		return seqnum;
	}

}
