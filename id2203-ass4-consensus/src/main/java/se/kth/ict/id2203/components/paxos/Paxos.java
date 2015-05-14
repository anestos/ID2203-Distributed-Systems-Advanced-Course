package se.kth.ict.id2203.components.paxos;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.ac.AbortableConsensus;
import se.kth.ict.id2203.ports.ac.AcAbort;
import se.kth.ict.id2203.ports.ac.AcDecide;
import se.kth.ict.id2203.ports.ac.AcPropose;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class Paxos extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Paxos.class);

	private Negative<AbortableConsensus> ac = provides(AbortableConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private int t;
	private int prepts;
	private int acks;
	private HashMap<Address, HelpMePlz> readlist;
	private int ats;
	private int pts;
	private int av;
	private int pv;
	private Set<Address> topology;
	private Address self;
	
	public Paxos(PaxosInit init) {
		t = 0;
		prepts = 0;
		self = init.getSelfAddress();
		topology = init.getAllAddresses();
		readlist = new HashMap<Address, HelpMePlz>();
		acks =0;
		ats = 0;
		pts =0;
		av =0;
		pv =0;
		subscribe(handlePropose, ac);
		subscribe(handleBebDeliver, beb);
		subscribe(handleNack, pp2p);
		subscribe(handlePrepareAck, pp2p);
		subscribe(handleAcceptMessage, beb);
		subscribe(handleAcceptAck, pp2p);
		

	}
	
	private Handler<AcPropose> handlePropose = new Handler<AcPropose>() {
		@Override
		public void handle(AcPropose event) {
			t++;
			pts = (t*topology.size() )+ self.getId(); 
			pv = event.getValue();
			readlist.clear();
			acks =0;
			trigger(new BebBroadcast(new PrepareMessage(self, pts, t)), beb);
			logger.info("sending propose");
		}
	};
	
	private Handler<PrepareMessage> handleBebDeliver = new Handler<PrepareMessage>() {
		@Override
		public void handle(PrepareMessage event) {

			t = Math.max(event.getT(), t)+1;
			if (event.getPts() < prepts){
				trigger(new Pp2pSend(event.getSource(), new NackMessage(self, event.getPts(), t)), pp2p);
				logger.info("sending Nack");
			} else {
				prepts = event.getPts();
				trigger(new Pp2pSend(event.getSource(), new PrepareAckMessage(self, ats, av, event.getPts(), t)),pp2p);
				logger.info("sending prepare Ack");

			}
		}
	};
	
	private Handler<NackMessage> handleNack = new Handler<NackMessage>() {
		@Override
		public void handle(NackMessage event) {
			logger.info("handling Nack... aborting");
			t = Math.max(event.getT(), t)+1;
			if (event.getPts() == pts){
				pts = 0;
				trigger(new AcAbort(), ac);
			}
		}
	};

	private Handler<PrepareAckMessage> handlePrepareAck = new Handler<PrepareAckMessage>() {
		@Override
		public void handle(PrepareAckMessage event) {
			logger.info("handling prepare ack");
			t = Math.max(event.getT(), t)+1;
			if (event.getPts() == pts){
				readlist.put(event.getSource(), new HelpMePlz(event.getTs(), event.getAv()));
				if (readlist.size() > topology.size()/2){
					//pair with highest timestamp
					HelpMePlz check = highest(readlist);
					if (check.getTs() !=0){
						pv = event.getAv();
					}
					readlist.clear();
					trigger(new BebBroadcast(new AcceptMessage(self,pts,pv,t)),beb);
					logger.info("broadcasting accept");
				}
			}
		}
	};

	private Handler<AcceptMessage> handleAcceptMessage = new Handler<AcceptMessage>() {
		@Override
		public void handle(AcceptMessage event) {
			logger.info("handling accept");
			t = Math.max(event.getT(), t)+1;
			if (event.getTs() < prepts){
				trigger(new Pp2pSend(event.getSource(), new NackMessage(self,event.getTs(),t)), pp2p);
				logger.info("sending Nack2");
			} else {
				ats = event.getTs();
				prepts = event.getTs();
				av = event. getV();
				trigger(new Pp2pSend(event.getSource(), new AcceptAckMessage(self, ats, t)), pp2p);
				logger.info("sending AcceptAck");
			}
		}
	};

	private Handler<AcceptAckMessage> handleAcceptAck = new Handler<AcceptAckMessage>() {
		
		@Override
		public void handle(AcceptAckMessage event) {
			logger.info("handling Accept");

			t = Math.max(event.getT(), t)+1;
			if (event.getPts() == pts){
				acks++;
				if (acks > topology.size()/2){
					pts = 0;
					trigger(new AcDecide(pv), ac);
					logger.info("Deciding...");

				}
			}
		}
	};
	
	
	private HelpMePlz highest(HashMap<Address, HelpMePlz> hashmap){
		HelpMePlz haha = null;
		for (Entry<Address, HelpMePlz> entry : hashmap.entrySet()){
			if (haha != null) {
			if (entry.getValue().getTs() > haha.getTs()){
				haha = entry.getValue();
			} } else{
				haha = entry.getValue();
			}
		}
		return haha;
	}
}

