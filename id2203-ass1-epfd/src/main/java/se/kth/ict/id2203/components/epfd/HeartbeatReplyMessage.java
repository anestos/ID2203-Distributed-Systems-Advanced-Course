package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatReplyMessage extends Pp2pDeliver {

	private static final long serialVersionUID = -7678165393077733049L;
	
	private int seqnum;

	protected HeartbeatReplyMessage(Address source, int seqnum) {
		super(source);
		this.seqnum = seqnum;
	}

	public int getSeqnum() {
		return seqnum;
	}

}
