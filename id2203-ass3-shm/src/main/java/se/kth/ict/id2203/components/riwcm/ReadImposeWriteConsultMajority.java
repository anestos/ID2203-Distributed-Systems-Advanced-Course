package se.kth.ict.id2203.components.riwcm;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.ar.ArReadRequest;
import se.kth.ict.id2203.ports.ar.ArReadResponse;
import se.kth.ict.id2203.ports.ar.ArWriteRequest;
import se.kth.ict.id2203.ports.ar.ArWriteResponse;
import se.kth.ict.id2203.ports.ar.AtomicRegister;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);
	private Negative<AtomicRegister> nnar = provides(AtomicRegister.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private int ts,wr,val,acks,writeval,rid,readval;
	private Boolean reading;
	private HashMap<Address, Help> readlist;

	private Set<Address> topology;
	private Address self;

	public ReadImposeWriteConsultMajority(ReadImposeWriteConsultMajorityInit event) {
		self = event.getSelfAddress();
		topology = event.getAllAddresses();
		ts=0;
		wr=0;
		val=0;
		acks=0;
		writeval=0;
		rid=0;
		readlist = new HashMap<Address, Help>();
		readval=0;
		reading = false;

		subscribe(handleRead, nnar);
		subscribe(handleBebDeliverRead, beb);
		subscribe(handlePp2pDeliverValue, pp2p);
		subscribe(handleWrite, nnar);
		subscribe(handleBebDeliverWrite, beb);
		subscribe(handlePp2pDeliverAck, pp2p);
	}
	private Handler<ArReadRequest> handleRead = new Handler<ArReadRequest>() {
		@Override
		public void handle(ArReadRequest event) {
			logger.info("handling Read Request");
			rid++;
			acks=0;
			readlist.clear();
			reading= true;
			trigger(new BebBroadcast(new BroadcastRead(self,rid)), beb);
		}
	};
	private Handler<BroadcastRead> handleBebDeliverRead = new Handler<BroadcastRead>() {
		@Override
		public void handle(BroadcastRead event) {
			logger.info("handling Broadcasted Read Request");
			trigger(new Pp2pSend(event.getSource(), new Value(self, event.getRid(), ts, wr, val)), pp2p);
		}
	};

	private Handler<Value> handlePp2pDeliverValue = new Handler<Value>() {
		@Override
		public void handle(Value event) {
			if (event.getR() == rid){
				logger.info("handling Pp2p Deliver Value");
				readlist.put(event.getSource(), new Help(event.getTs(), event.getWr(), event.getV()));
				if (readlist.size() > topology.size()/2){
					Help check = highest(readlist); 
					readlist.clear();
					if (reading){
						trigger(new BebBroadcast(new Write(self,rid,check.getTs(),check.getWr(),check.getV())),beb);
					} else {
						trigger(new BebBroadcast(new Write(self,rid,check.getTs()+1,self.getId(),writeval)),beb);
					}
				}
			}
		}
	};


	private Handler<ArWriteRequest> handleWrite = new Handler<ArWriteRequest>() {
		@Override
		public void handle(ArWriteRequest event) {
			logger.info("handling ArWrite Request");
			rid++;
			writeval = event.getValue();
			acks=0;
			readlist.clear();
			trigger(new BebBroadcast(new BroadcastRead(self, rid)),beb);
		}
	};

	private Handler<Write> handleBebDeliverWrite = new Handler<Write>() {
		@Override
		public void handle(Write event) {
			int tsprime = event.getTs();
			int wrprime = event.getWr();
			int vprime = event.getV();
			if (tsprime > ts || (tsprime==ts && wrprime > wr)){
				ts = tsprime;
				wr = wrprime;
				val = vprime;
			}

			trigger(new Pp2pSend(event.getSource(), new Ack(self, event.getR())), pp2p);
		}
	};

	private Handler<Ack> handlePp2pDeliverAck = new Handler<Ack>() {
		@Override
		public void handle(Ack event) {
			if (event.getR() == rid){
				acks++;
				if (acks > topology.size()/2){
					acks = 0;
					if (reading){
						reading = false;
						trigger(new ArReadResponse(val),nnar);
					} else {
						trigger(new ArWriteResponse(),nnar);
					}

				}


			}
		}
	};


	private Help highest(HashMap<Address, Help> hashmap){
		Help haha = null;
		for (Entry<Address, Help> entry : hashmap.entrySet()){
			if (haha != null) {
				if (entry.getValue().getTs() > haha.getTs()){
					haha = entry.getValue();
				} 
			} else{
				haha = entry.getValue();
			}
		}
		return haha;
	}
}
