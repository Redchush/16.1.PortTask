package port;

import org.apache.log4j.Logger;
import ship.Ship;
import warehouse.Container;
import warehouse.Warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Port {
	private final static Logger logger = Logger.getRootLogger();

	/*
	 * changed List to BlockingQueue
	 * 	the list as data storage is inappropriate to multithreading task. (see lockBerth() method)
	 * 	The one of queue realisation must be used. In this case --> blocking queue to deal with multithreading and
	 * 	eliminate synchronize block.
	 */
	private BlockingQueue<Berth> berthList; // очередь причалов
	private Warehouse portWarehouse; // хранилище порта
	private Map<Ship, Berth> usedBerths; // какой корабль у какого причала стоит

	public Port(int berthSize, int warehouseSize) {
		portWarehouse = new Warehouse(warehouseSize); // создаем пустое
		// хранилище
		berthList = new LinkedBlockingQueue<Berth>(berthSize); // создаем очередь причалов
		for (int i = 0; i < berthSize; i++) { // заполняем очередь причалов
			// непосредственно самими причалами
			berthList.add(new Berth(i, portWarehouse));
		}

		usedBerths = new HashMap<Ship, Berth>(); // создаем объект, который будет
		logger.debug("Порт создан.");
	}

	/**
	 * Added containers to port's warehouse
	 * rectifying : Checking for free space port's in warehouse
	 * to be sure of successful adding use hasFreeSizeFor()
	 *
	 * @return true if containers added successfully, false in other case
	 * @see Port#hasFreeSizeFor
	 */
	public boolean setContainersToWarehouse(List<Container> containerList) { // method changed.
		if (hasFreeSizeFor(containerList.size())) {
			portWarehouse.addContainer(containerList);
			return true;
		}
		logger.debug("Can't add more containers to port + " +
				" than this wharehouse can contain. You can check free size before loading");
		return false;
	}


	/*	previous decision:
    *            synchronized (berthList) {
    *                berth = berthList.remove(0);}
    *            may produce IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
    *            so in case of free berth absence -->  IndexOutOfBoundsException;
    *    solution:
	*    @see  berthList description
    */
	public boolean lockBerth(Ship ship) throws InterruptedException {
		boolean result = false;
		logger.debug("Корабль " + ship.getName() + " ждет свободного причала");
		Berth berth = berthList.take();
		usedBerths.put(ship, berth);
		return true;
	}

	public boolean unlockBerth(Ship ship) {
		Berth berth = usedBerths.get(ship);
		berthList.offer(berth);
		usedBerths.remove(ship);
		return true;
	}

	public Berth getBerth(Ship ship) throws PortException {
		Berth berth = usedBerths.get(ship);
		if (berth == null) {
			throw new PortException("Try to use Berth without blocking.");
		}
		return berth;
	}

	/**
	 * Checking for free space port's in warehouse
	 *
	 * @return whether the port can take this containers count
	 */
	public boolean hasFreeSizeFor(int numOfContainers) {
		return portWarehouse.getFreeSize() > numOfContainers;
	}

}
