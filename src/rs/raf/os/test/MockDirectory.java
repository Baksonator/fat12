package rs.raf.os.test;

import rs.raf.os.dir.AbstractDirectory;
import rs.raf.os.dir.DirectoryException;
import rs.raf.os.disk.Disk;
import rs.raf.os.disk.DiskUtil;
import rs.raf.os.fat.FAT16;

public class MockDirectory extends AbstractDirectory {

	private DiskFile[] files;
	
	public MockDirectory(FAT16 fat, Disk disk) {
		super(fat, disk);
		files = new DiskFile[getUsableTotalSpace() / (fat.getClusterCount() * fat.getClusterWidth())];
	}
	
	@Override
	public boolean writeFile(String name, byte[] data) {
		for(int i = 0; i < files.length; i++) {
			if(files[i] != null && files[i].getName().equals(name)) {
				if(getUsableFreeSpace() < data.length - files[i].getFileSize()) {
					return false;
				} else {
					deleteFile(name);
					files[i].setName(name);
					files[i].setFileSize(data.length);
					if(data.length % (disk.getSectorSize() * fat.getClusterCount()) == 0) {
						files[i].setClusterCount(data.length / (disk.getSectorSize() * fat.getClusterWidth()));
					} else {
						files[i].setClusterCount(data.length / (disk.getSectorSize() * fat.getClusterWidth()) + 1);
					}
					int firstCluster = -1;
					int lastFat = 0;
					for(int j = 2; j < fat.getClusterCount() + 2; j++) {
						if(fat.readCluster(j) == 0) {
							firstCluster = j;
							lastFat = j;
							break;
						}
					}
					int remainingClusters = files[i].getClusterCount() - 1;
					files[i].setFirstCluster(firstCluster);
					byte[] currentSectorData = DiskUtil.slice(data, 0, disk.getSectorSize() * fat.getClusterWidth());
					disk.writeSectors((firstCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth(), currentSectorData);
					int k = 1;
					if(remainingClusters == 0) {
						fat.writeCluster(firstCluster, 0xFFF8);
						return true;
					} else {
						while(remainingClusters > 0) {
							for(int j = lastFat + 1; j < fat.getClusterCount() + 2; j++) {
								if(fat.readCluster(j) == 0) {
									fat.writeCluster(firstCluster, j);
									firstCluster = j;
									lastFat = j;
									break;
								}
							}
							remainingClusters--;
							currentSectorData = DiskUtil.slice(data, k * disk.getSectorSize() * fat.getClusterWidth(), disk.getSectorSize() * fat.getClusterWidth());
							disk.writeSectors((firstCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth(), currentSectorData);
							k++;
						}
						fat.writeCluster(firstCluster, 0xFFF8);
						return true;
					}
				}
			}
		}
		if(getUsableFreeSpace() < data.length) {
			return false;
		}
		for(int i = 0; i < files.length; i++) {
			if(files[i] == null || files[i].getName().equals("E5")) {
				files[i] = new DiskFile(name);
				files[i].setFileSize(data.length);
				if(data.length % (disk.getSectorSize() * fat.getClusterCount()) == 0) {
					files[i].setClusterCount(data.length / (disk.getSectorSize() * fat.getClusterWidth()));
				} else {
					files[i].setClusterCount(data.length / (disk.getSectorSize() * fat.getClusterWidth()) + 1);
				}
				int firstCluster = -1;
				int lastFat = 0;
				for(int j = 2; j < fat.getClusterCount() + 2; j++) {
					if(fat.readCluster(j) == 0) {
						firstCluster = j;
						lastFat = j;
						break;
					}
				}
				int remainingClusters = files[i].getClusterCount() - 1;
				files[i].setFirstCluster(firstCluster);
				byte[] currentSectorData = DiskUtil.slice(data, 0, disk.getSectorSize() * fat.getClusterWidth());
				disk.writeSectors((firstCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth(), currentSectorData);
				int k = 1;
				if(remainingClusters == 0) {
					fat.writeCluster(firstCluster, 0xFFF8);
					return true;
				} else {
					while(remainingClusters > 0) {
						for(int j = lastFat + 1; j < fat.getClusterCount() + 2; j++) {
							if(fat.readCluster(j) == 0) {
								fat.writeCluster(firstCluster, j);
								firstCluster = j;
								lastFat = j;
								break;
							}
						}
						remainingClusters--;
						currentSectorData = DiskUtil.slice(data, k * disk.getSectorSize() * fat.getClusterWidth(), disk.getSectorSize() * fat.getClusterWidth());
						disk.writeSectors((firstCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth(), currentSectorData);
						k++;
					}
					fat.writeCluster(firstCluster, 0xFFF8);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public byte[] readFile(String name) throws DirectoryException {
		for(int i = 0; i < files.length; i++) {
			if(files[i] != null && files[i].getName().equals(name)) {
				DiskFile read = files[i];
				byte[] returnValue = new byte[read.getFileSize()];
				int firstCluster = files[i].getFirstCluster();
				int nextCluster = fat.readCluster(firstCluster);
				byte[] currentSector = disk.readSectors((firstCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth());
				if(nextCluster == 0xFFF8) {
					for(int j = 0; j < read.getFileSize(); j++) {
						returnValue[j] = currentSector[j];
					}
					return returnValue;
				} else {
					for(int j = 0; j < disk.getSectorSize() * fat.getClusterWidth(); j++) {
						returnValue[j] = currentSector[j];
					}
					int k = 1;
					while(fat.readCluster(nextCluster) != 0xFFF8) {
						currentSector = disk.readSectors((nextCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth());
						for(int j = 0; j < disk.getSectorSize() * fat.getClusterWidth(); j++) {
							returnValue[k * disk.getSectorSize() * fat.getClusterWidth() + j] = currentSector[j];
						}
						k++;
						int tmp = fat.readCluster(nextCluster);
						nextCluster = tmp;
					}
					currentSector = disk.readSectors((nextCluster - 2) * fat.getClusterWidth(), fat.getClusterWidth());
					for(int j = 0; j < read.getFileSize() % ((read.getClusterCount() - 1) * disk.getSectorSize() * fat.getClusterWidth()); j++) {
						returnValue[k * disk.getSectorSize() * fat.getClusterWidth() + j] = currentSector[j];
					}
					return returnValue;
				}
			}
		}
		throw new DirectoryException("No such file!");
	}

	@Override
	public void deleteFile(String name) throws DirectoryException {
		for(int i = 0; i < files.length; i++) {
			if(files[i] != null && files[i].getName().equals(name)) {
				files[i].setName("E5");
				int firstCluster = files[i].getFirstCluster();
				int nextCluster = fat.readCluster(firstCluster);
				fat.writeCluster(firstCluster, 0);
				while(nextCluster != 0xFFF8) {
					int tmp = fat.readCluster(nextCluster);
					fat.writeCluster(nextCluster, 0);
					nextCluster = tmp;
				}
				return;
			}
		}
		throw new DirectoryException("No such file!");
	}

	@Override
	public String[] listFiles() {
		int numberOfFiles = 0;
		for(int i = 0; i < files.length; i++) {
			if(files[i] != null && files[i].getName() != "E5") {
				numberOfFiles++;
			}
		}
		String[] returnString = new String[numberOfFiles];
		int j = 0;
		for(int i = 0; i < files.length; i++) {
			if(files[i] != null && files[i].getName() != "E5") {
				returnString[j++] = files[i].getName();
			}
		}
		return returnString;
	}

	@Override
	public int getUsableTotalSpace() {
		return Math.min(fat.getClusterCount() * fat.getClusterWidth() * disk.getSectorSize(), disk.diskSize());
	}

	@Override
	public int getUsableFreeSpace() {
		int sum = 0;
		for(int i = 0; i < files.length; i++) {
			if(files[i] != null && files[i].getName() != "E5") {
				sum += files[i].getClusterCount() * fat.getClusterWidth() * disk.getSectorSize();
			}
		}
		return getUsableTotalSpace() - sum;
	}

	public DiskFile[] getFiles() {
		return files;
	}

}
