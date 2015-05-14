package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.tob.TobDeliver;

public class T extends Object {
	private final int seqNum;
	private final int pid;
	private final TobDeliver deliverEvent;

	public T(int seqNum, int pid, TobDeliver deliverEvent) {
		this.seqNum = seqNum;
		this.pid = pid;
		this.deliverEvent = deliverEvent;
	}
	public int getSeqNum() {
		return seqNum;
	}
	
	public int getPid() {
		return pid;
	}
	
	public TobDeliver getDeliverEvent() {
		return deliverEvent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof T) {
			T m = (T) obj;
			return seqNum == m.seqNum && pid == m.pid;
		}
		return false;
	}

	public int compareTo(T o) {
		int c = Integer.compare(seqNum, o.seqNum);
		if (c == 0) {
			c = Integer.compare(pid, o.pid);
		}
		return c;
	}

	@Override
	public String toString() {
		return String.format("Message(%d, %d)", seqNum, pid);
	}
}
