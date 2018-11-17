package rs.raf.os.test;

public class DiskFile {

	private String name;
	private int firstCluster;
	private int clusterCount;
	private int fileSize;
	
	public DiskFile(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFirstCluster() {
		return firstCluster;
	}

	public void setFirstCluster(int firstCluster) {
		this.firstCluster = firstCluster;
	}

	public int getClusterCount() {
		return clusterCount;
	}

	public void setClusterCount(int clusterCount) {
		this.clusterCount = clusterCount;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	
}
