package port;

import org.apache.log4j.Logger;
import warehouse.Container;
import warehouse.Warehouse;

import java.util.List;

public class Berth {

	private int id;
	private final Warehouse portWarehouse; // change modifier to final
	private final static Logger logger = Logger.getRootLogger();

	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}

	public int getId() {
		return id;
	}

	public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		if (numberOfConteiners == 0){
			return true;
		}
		synchronized (portWarehouse) { //synchronized (shipWarehouse) deleted so nobody but the ship itself can
			// change it's containers storage

			logger.debug("Сейчас в порту " + portWarehouse.getRealSize());

			result = loadOperation(shipWarehouse, portWarehouse, numberOfConteiners);
			if (result){
				logger.debug("После вызгрузки с корабля " + numberOfConteiners
						+ " контейнеров в порту = " + (portWarehouse.getRealSize())
						+ " свободных = " + portWarehouse.getFreeSize());
			}

		}
		return result;
	}



	public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		synchronized (portWarehouse) {
			logger.debug("Сейчас в порту " + portWarehouse.getRealSize());
			result = loadOperation(portWarehouse, shipWarehouse, numberOfConteiners);
			if (result){
				logger.debug("После загрузки с корабля " + numberOfConteiners
						+ " контейнеров = " + (portWarehouse.getRealSize()) + " свободных = "
						+ portWarehouse.getFreeSize());
			}
		}
		return result;
	}
	/**
	 * the method reload the containers from one warehouse to another as a transaction
	 *  in case of interruption during executing operation rollback the state of warehouse, from which the
	 *  containers was got
	 */
	private boolean loadOperation(Warehouse from, Warehouse to, int count){
		boolean result = false;
		boolean tempResult = true;
		List<Container> containerList = null;
		try{
			containerList = from.getContainer(count);
			if (containerList != null) {
				tempResult = to.addContainer(containerList);
				result = tempResult;
			}
		} finally {
			if ((!tempResult)&&(containerList!=null)){ 	//return the containers what was got from from warehouse
				from.addContainer(containerList);
			}
		}
		return result;
	}

}
