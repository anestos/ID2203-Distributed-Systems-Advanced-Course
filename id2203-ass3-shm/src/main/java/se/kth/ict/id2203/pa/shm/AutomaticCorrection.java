package se.kth.ict.id2203.pa.shm;

import se.kth.ict.id2203.sim.shm.SharedMemoryTester;

public class AutomaticCorrection {
	public static void main(String[] args) {
		String email = "anestos@kth.se";
		String password = "VNoZpo";
		SharedMemoryTester.correctAndSubmit(email, password);
	}
}
