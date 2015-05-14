/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203.components.crb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import se.kth.ict.id2203.ports.crb.CausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.crb.CrbBroadcast;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitingCrb extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(WaitingCrb.class);
	private Positive<ReliableBroadcast> rb = requires(ReliableBroadcast.class);
	private Negative<CausalOrderReliableBroadcast> crb = provides(CausalOrderReliableBroadcast.class);

	private int[] V;
	private int lsn;
	private HashMap<int[], CrbDataMessage> pending;
	private Address self;
	private int count =0;

	public WaitingCrb(WaitingCrbInit init) {
		pending = new HashMap<int[], CrbDataMessage>();;
		lsn = 0;
		self = init.getSelfAddress();
		V = new int[3];
		subscribe(crbBroadcast, crb);
		subscribe(rbDeliver, rb);
	}



	private Handler<CrbBroadcast> crbBroadcast = new Handler<CrbBroadcast>() {
		@Override
		public void handle(CrbBroadcast event) {
			int[] W = V.clone();
			W[rank(self)] = lsn;
			lsn++;
			trigger(new RbBroadcast(new CrbDataMessage(self, event.getDeliverEvent(), W)), rb);

		}
	};

	private Handler<CrbDataMessage> rbDeliver = new Handler<CrbDataMessage>() {
		@Override
		public void handle(CrbDataMessage event) {
			pending.put(event.getVector(),event);
			//logger.info("got message from " + event.getSource().getId()+" with vector "+ event.getVector()[0]+event.getVector()[1]+event.getVector()[2]);
			
			while(itsAlive(pending, V)){
				Object[] array = pending.values().toArray();
	            for(int i=0; i<array.length; i++) {
	            	CrbDataMessage m = (CrbDataMessage) array[i];
	            	
					if (compareThem(m.getVector(), V)){
						pending.remove(m.getVector());
						V[rank(m.getSource())]++;
						trigger(m.getMsg(), crb);
						count++;
						//logger.info(""+count+" DELIVERED FROM: " + event.getSource().getId()+" VECTOR: "+ event.getVector()[0]+event.getVector()[1]+event.getVector()[2]+"    buffer size: " + pending.size());


					}
					
				}
			}
		}
	};
	
	private int rank(Address addr) {
		return addr.getId()-1;
	}

	private Boolean compareThem(int[] a, int[] b){
		Boolean checking = false; 
		int x = 0;
		for (int i = 0; i < a.length; i++) {
            if (a[i] <= b[i]) {
            	x++;   
            }         
		}
		if (x == b.length) {
			checking = true;
		}
		return checking;
	}
	
	private Boolean itsAlive(HashMap<int[], CrbDataMessage> a, int[] b){
		Boolean checking = false;
		Object[] array = a.values().toArray();
        for(int i=0; i<array.length; i++) {
        	CrbDataMessage m = (CrbDataMessage) array[i];
			if (compareThem(m.getVector(), b)){
				checking = true;
			} 
		}

		return checking;
	}
}