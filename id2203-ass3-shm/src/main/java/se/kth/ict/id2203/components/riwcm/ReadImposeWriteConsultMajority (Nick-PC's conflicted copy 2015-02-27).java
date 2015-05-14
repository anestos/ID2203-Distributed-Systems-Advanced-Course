package se.kth.ict.id2203.components.riwcm;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.ar.AtomicRegister;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Negative<AtomicRegister> nnar = provides(AtomicRegister.class);

	private int[] tswrval;
	private int acks;
	private int writeval;
	private int rid;
	private String[] readlist;
	private String readval;
	private Set<Address> topology;
	private Boolean reading = false;
	
	public ReadImposeWriteConsultMajority(ReadImposeWriteConsultMajorityInit event) {
		topology = event.getAllAddresses();
		
		tswrval = new int[topology.size()];
		acks = 0;
		subscribe(bebDeliver, beb);
		subscribe(pp2pDeliver, pp2p);
		subscribe(nnarRead, nnar);
		readval = null;
		readlist = new String[topology.size()];
	}
	
	private Handler<BebDeliver> nnarRead = new Handler<BebDeliver>() {
		@Override
		public void handle(BebDeliver event) {
			rid++;
			acks = 0;
			readlist = new String[topology.size()];
			reading = true;
			trigger(new BebBroadcast(event), beb);

		}
	};
	
	private Handler<Message> bebDeliver = new Handler<Message>() {
		@Override
		public void handle(Message event) {


		}
	};
	
	private Handler<Message> pp2pDeliver = new Handler<Message>() {
		@Override
		public void handle(Message event) {


		}
	};
}
