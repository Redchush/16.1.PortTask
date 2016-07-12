package ship;

import org.apache.log4j.Logger;
import port.Berth;
import port.Port;
import port.PortException;
import warehouse.Container;
import warehouse.Warehouse;

import java.util.List;
import java.util.Random;

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
	// 2) real ship must have information about this load factor

	/*
	*  the volume of ship warehouse at all
	 */
	public int getSize() {
		return shipWarehouse.getSize();
	}

	/*
	*  number of containers that ship warehouse contains now
	*/
	public int getRealSize() {
		return shipWarehouse.getRealSize();
	}

	/*
	*  number of containers that ship warehouse can place in it's warehouse now
	*/
	public int getFreeSize() {
		return shipWarehouse.getFreeSize();
	}

	public void stopThread() {
		stopThread = true;
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

/* it is strange situation in which the ship leaves the port in case of absence of free berth.
				 Really it must wait staying in queue until the berth will be freed by another ship.
				 So if the ship wait we are needn't the indicators of success of locking berth
				 See the realization of isLockeBerth method and Port class
*/
	private void inPort() throws PortException, InterruptedException {
		Berth berth = null;
		try {
				port.lockBerth(this);
				berth = port.getBerth(this);
				logger.debug("Корабль " + name + " пришвартовался к причалу " + berth.getId());
				ShipAction action = getNextAction();
				executeAction(action, berth);
		 } finally {
				port.unlockBerth(this);
				logger.debug("Корабль " + name + " отошел от причала " + (berth == null ? null : berth.getId()));
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
		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров на склад порта. \n Сейчас склад корабля " + getSize() +
				", из них свободно " + getFreeSize() + ", заполнено " + getRealSize());

		boolean result = berth.add(shipWarehouse, containersNumberToMove);

		if (!result) {
			logger.debug("Недостаточно места на складе порта для выгрузки кораблем "
					+ name + " " + containersNumberToMove + " контейнеров.\nСейчас склад корабля " + getSize() +
					", из них свободно " + getFreeSize() + ", заполнено " + getRealSize());
		} else {
			logger.debug("Корабль " + name + " выгрузил " + containersNumberToMove
					+ " контейнеров в порт.\nСейчас склад корабля " + getSize() +
					", из них свободно " + getFreeSize() + ", заполнено " + getRealSize());
			
		}
		return result;
	}

	private boolean loadFromPort(Berth berth) throws InterruptedException {
		
		int containersNumberToMove = containersFromPortCount(); //old method to special method. Explanation see after
	    logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров со склада порта.\nСейчас склад корабля " + getSize() +
				", из них свободно " + getFreeSize() + ", заполнено " + getRealSize());

		boolean result = berth.get(shipWarehouse, containersNumberToMove);
		
		if (result) {
			logger.debug("Корабль " + name + " загрузил " + containersNumberToMove
					+ " контейнеров из порта.\nСейчас склад корабля " + getSize() +
					", из них свободно " + getFreeSize() + ", заполнено " + getRealSize());
		} else {
			logger.debug("Недостаточно места на на корабле " + name
					+ " для погрузки " + containersNumberToMove + " контейнеров из порта."+
					"\nСейчас склад корабля " + getSize() +
					", из них свободно " + getFreeSize() + ", заполнено " + getRealSize() +
					" или в порту нет такого количества контейнеров");
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
