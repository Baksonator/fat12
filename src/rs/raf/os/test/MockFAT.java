package rs.raf.os.test;

import rs.raf.os.fat.FAT16;
import rs.raf.os.fat.FATException;

public class MockFAT implements FAT16 {

	private int clusterWidth;
	private int clusterCount;
	private int[] FATdata;
	
	public MockFAT(int clusterWidth) {
		this.clusterWidth = clusterWidth;
		clusterCount = 0xFFED;
		FATdata = new int[clusterCount];
	}
	
	public MockFAT(int clusterWidth, int clusterCount) {
		this.clusterWidth = clusterWidth;
		this.clusterCount = clusterCount;
		FATdata = new int[clusterCount];
	}
	
	@Override
	public int getEndOfChain() {
		return 0xFFF8;
	}

	@Override
	public int getClusterCount() {
		return clusterCount;
	}

	@Override
	public int getClusterWidth() {
		return clusterWidth;
	}

	@Override
	public int readCluster(int clusterID) throws FATException {
		if(clusterID < 2 || clusterID >= clusterCount + 2) {
			throw new FATException("Invalid clusterID!");
		}
		return FATdata[clusterID - 2];
	}

	@Override
	public void writeCluster(int clusterID, int valueToWrite) throws FATException {
		if(clusterID < 2 || clusterID >= clusterCount + 2) {
			throw new FATException("Invalid clusterID!");
		}
		FATdata[clusterID - 2] = valueToWrite;
	}

	@Override
	public String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i < clusterCount - 1; i++) {
			sb.append(FATdata[i]);
			sb.append("|");
		}
		sb.append(FATdata[clusterCount - 1]);
		sb.append("]");
		return sb.toString();
	}
	
}
