package ship;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import port.Berth;
import port.Port;
import port.PortException;
import warehouse.Container;
import warehouse.Warehouse;

public class Ship implements Runnable {

	private final static Logger logger = Logger.getRootLogger();
	private volatile boolean stopThread = false;

	private String name;
	private Port port;
	private Warehouse shipWarehouse;

	public Ship(String name, Port port, int shipWarehouseSize) {
		this.name = name;
		this.port = port;
		shipWarehouse = new Warehouse(shipWarehouseSize);
	}

	/** Added containers to ships's warehouse
	 * rectifying : Checking for free space  in ships's warehouse
	 * to be sure of successful adding use hasFreeSizeFor()
	 * @see Ship#hasFreeSizeFor
	 * @return true if containers added successfully, false in other case
	 */

	public boolean setContainersToWarehouse(List<Container> containerList) {
		if (hasFreeSizeFor(containerList.size())){
			shipWarehouse.addContainer(containerList);
			return true;
		}
		logger.error("Can't add more containers to ship " + this.name +
				" than this wharehouse can contain. You can check free size before loading");
		return false;
	}

	/**
	 * Checking for free space ships's in warehouse
	 * @param numOfContainers number of containers to be added
	 * @return whether the ship can take this containers count
	 */
	public boolean hasFreeSizeFor(int numOfContainers) {
		return shipWarehouse.getFreeSize() > numOfContainers;
	}

	public String getName() {
		return name;
	}

	//without this methods
	// 1) class barely testable
	// 2) real ship must have information about this loaded factor
	public int getSize() {
		return shipWarehouse.getSize();
	}

	public int getRealSize() {
		return shipWarehouse.getRealSize();
	}

	public int getFreeSize() {
		return shipWarehouse.getFreeSize();
	}

	public void stopThread() {
		stopThread = true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Ship ship = (Ship) o;

		if (stopThread != ship.stopThread) {
			return false;
		}
		if (name != null ? !name.equals(ship.name) : ship.name != null) {
			return false;
		}
		if (port != null ? !port.equals(ship.port) : ship.port != null) {
			return false;
		}
		return shipWarehouse != null ? shipWarehouse.equals(ship.shipWarehouse) : ship.shipWarehouse == null;

	}

	@Override
	public int hashCode() {
		int result = (stopThread ? 1 : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (port != null ? port.hashCode() : 0);
		result = 31 * result + (shipWarehouse != null ? shipWarehouse.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Ship{" +
				"stopThread=" + stopThread +
				", name='" + name + '\'' +
				", port=" + port +
				", shipWarehouse=" + shipWarehouse +
				'}';
	}

	public void run() {
		try {
			while (!stopThread) {
				atSea();
				inPort();
			}
		} catch (InterruptedException e) {
			logger.error("С кораблем случилась неприятность и он уничтожен.", e);
		} catch (PortException e) {
			logger.error("С кораблем случилась неприятность и он уничтожен.", e);//!!! переписать сообщение
		}
	}

	private void atSea() throws InterruptedException {
		Thread.sleep(1000);
	}


	private void inPort() throws PortException, InterruptedException {

		boolean isLockedBerth = false;
		Berth berth = null;
		try {
			isLockedBerth = port.lockBerth(this);
			
			if (isLockedBerth) {
				berth = port.getBerth(this);
				logger.debug("Корабль " + name + " пришвартовался к причалу " + berth.getId());
				ShipAction action = getNextAction();
				executeAction(action, berth);
			} else {
				logger.debug("Кораблю " + name + " отказано в швартовке к причалу ");
			}
		} finally {
			if (isLockedBerth){
				port.unlockBerth(this);
				logger.debug("Корабль " + name + " отошел от причала " + berth.getId());
			}
		}
		
	}

	private void executeAction(ShipAction action, Berth berth) throws InterruptedException {
		switch (action) {
		case LOAD_TO_PORT:
 				loadToPort(berth);
			break;
		case LOAD_FROM_PORT:
				loadFromPort(berth);
			break;
		}
	}

	private boolean loadToPort(Berth berth) throws InterruptedException {

		int containersNumberToMove = containersToPortCount();// change old method to special method. Explanation see
		// after
		boolean result = false;

		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров на склад порта.");

		result = berth.add(shipWarehouse, containersNumberToMove);
		
		if (!result) {
			logger.debug("Недостаточно места на складе порта для выгрузки кораблем "
					+ name + " " + containersNumberToMove + " контейнеров.");
		} else {
			logger.debug("Корабль " + name + " выгрузил " + containersNumberToMove
					+ " контейнеров в порт.");
			
		}
		return result;
	}

	private boolean loadFromPort(Berth berth) throws InterruptedException {
		
		int containersNumberToMove = containersFromPortCount(); //old method to special method. Explanation see after
		boolean result = false;

		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров со склада порта.");
		
		result = berth.get(shipWarehouse, containersNumberToMove);
		
		if (result) {
			logger.debug("Корабль " + name + " загрузил " + containersNumberToMove
					+ " контейнеров из порта.");
		} else {
			logger.debug("Недостаточно места на на корабле " + name
					+ " для погрузки " + containersNumberToMove + " контейнеров из порта.");
		}
		
		return result;
	}
	//old version : the common method can afford load and unload random containers count ,
	// despite of the limited warehouse storage and real containers storaged in ship
	// new version : two specialized methods

	/**
	 *  create random number limited by real size of warehouse
	 *  @see Warehouse#getRealSize()
	 */
	private int containersToPortCount() {
		Random random = new Random();
		return random.nextInt(getRealSize());
	}

	/**
	*  create random number limited by size of warehouse
	*  @see Warehouse#getSize()
	*/
	private int containersFromPortCount() {
		Random random = new Random();
		return random.nextInt(getFreeSize());
	}

	private ShipAction getNextAction() {
		Random random = new Random();
		int value = random.nextInt(4000);
		if (value < 1000) {
			return ShipAction.LOAD_TO_PORT;
		} else if (value < 2000) {
			return ShipAction.LOAD_FROM_PORT;
		}
		return ShipAction.LOAD_TO_PORT;
	}

	private enum ShipAction {				//make private access : this enum uses only in this class frame
		LOAD_TO_PORT, LOAD_FROM_PORT
	}
}
