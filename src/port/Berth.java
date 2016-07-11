package port;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import warehouse.Container;
import warehouse.Warehouse;

public class Berth {

	private int id;
	private Warehouse portWarehouse;

	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}

	public int getId() {
		return id;
	}

	public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
			
		boolean portLock = false;
		
		synchronized (portWarehouse) {
			synchronized (shipWarehouse) {
				
			}
		}
		
		
		return result;
		
	}
	
	

	public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Berth berth = (Berth) o;

		if (id != berth.id) {
			return false;
		}
		return portWarehouse != null ? portWarehouse.equals(berth.portWarehouse) : berth.portWarehouse == null;

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (portWarehouse != null ? portWarehouse.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Berth{" +
				"id=" + id +
				", portWarehouse=" + portWarehouse +
				'}';
	}
}
