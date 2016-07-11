package warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {
	private List<Container> containerList;
	private int size;
	//private Lock lock;

	public Warehouse(int size) {
		containerList = new ArrayList<Container>(); // changed : so was impossible to know how much
													// containers in warehouse at any moment
		//lock = new ReentrantLock();
		this.size = size;
	}

	public boolean addContainer(Container container) {     // changed: added the check of adding possibility
		return getFreeSize() != 0 && containerList.add(container);
	}

	public boolean addContainer(List<Container> containers) {
		boolean result = false;
		if(containerList.size() + containers.size() <= size){
			result = containerList.addAll(containers);
		}
		return result;
	}

	public Container getContainer() {
		if (containerList.size() > 0) {
			return containerList.remove(0);
		}
		return null;
	}

	public List<Container> getContainer(int amount) {
		if (containerList.size() >= amount) {			
			List<Container> cargo = new ArrayList<Container>(containerList.subList(0, amount));
			containerList.removeAll(cargo);
			return cargo;
		}
		return null;
	}
	/**
	* @return total size in containers of warehouse
	*/
	public int getSize(){
		return size;
	}

	/**
	 * @return number of containers that at warehouse in this moment
	 */
	public int getRealSize(){
		return containerList.size();
	}

	/**
	 * @return number of cantainers that warehouse can take
	 */
	public int getFreeSize(){
		return size - containerList.size();
	}
	
	/*public Lock getLock(){
		return lock;
	}*/

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Warehouse warehouse = (Warehouse) o;

		if (size != warehouse.size) {
			return false;
		}
		return containerList != null ? containerList.equals(warehouse.containerList) : warehouse.containerList == null;

	}

	@Override
	public int hashCode() {
		int result = containerList != null ? containerList.hashCode() : 0;
		result = 31 * result + size;
		return result;
	}

	@Override
	public String toString() {
		return "Warehouse{" +
				"containerList=" + containerList +
				", size=" + size +
				'}';
	}
}
