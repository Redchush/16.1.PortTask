package port;

import org.apache.log4j.Logger;
import warehouse.Container;
import warehouse.Warehouse;

import java.util.List;
import java.util.concurrent.locks.Lock;

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

        if (numberOfConteiners == 0){
            return true;
        }
        boolean result = loadOperation(shipWarehouse, portWarehouse, numberOfConteiners);
        return result;
    }

    public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {

        if (numberOfConteiners == 0 ){
            return true;
        }
        boolean result = loadOperation(portWarehouse, shipWarehouse, numberOfConteiners);
        return result;
    }
    /**
     * the method reload the containers from one warehouse to another as a transaction
     *  in case of interruption during executing operation rollback the state of warehouse, from which the
     *  containers was got
     */

    ///It is possible to use common method because we need synchronise by both port and ship warehouse
    // 1) port warehouse is a common resource shared by all ship threads
    // 2) Ship has  public  method that change it's state : may be situation in which we take containers
    // to load in port, but can't return back if fail occurred because of lack of free space;
    // so ship warehouse must be synchronised too
    private boolean loadOperation(Warehouse from, Warehouse to, int count){
        boolean result = false;
        boolean tempResult = true;
        List<Container> containerList = null;

        Lock lockFrom = from.getLock();
        Lock lockTo = to.getLock();
        lockFrom.lock();
        lockTo.lock();
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
            lockFrom.unlock();
            lockTo.unlock();
        }

        return result;
    }
}
