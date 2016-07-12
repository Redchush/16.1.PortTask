public class previuse {

//    public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
//
//        if (numberOfConteiners == 0){
//            return true;
//        }
//        boolean result = false; // total result
//        boolean tempResult = true; //temporary result that second operation in transaction not failed
//        List<Container> containerList = null;
//        synchronized (portWarehouse) {
//            logger.debug("Сейчас в порту " + portWarehouse.getRealSize());
//            try{
//                containerList = shipWarehouse.getContainer(numberOfConteiners); // get containers from ship
//                if (containerList != null) {                                    // check that we really get containers
//                    tempResult = portWarehouse.addContainer(containerList);     //add this containers to port
//                    result = tempResult;
//                }
//                if (result){
//                    logger.debug("После вызгрузки с корабля " + numberOfConteiners
//                            + " контейнеров в порту = " + (portWarehouse.getRealSize())
//                            + " свободных = " + portWarehouse.getFreeSize());
//                }
//            } finally {
//                if ((!tempResult)&&(containerList!=null)){ 	//return the containers what was got from from warehouse
//                    shipWarehouse.addContainer(containerList);
//                }
//            }
//        }
//        return result;
//    }
//
//    public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
//        boolean result = false;
//        boolean tempResult = true;
//        List<Container> containerList = null;
//
//        ///!!!! So Ship has public method that change it's state may be situation in which we take containers
//        // to load in port, but can't return back if fail occurred because of lack of free space;
//        // so ship warehouse must be synchronised too
//        synchronized (portWarehouse) {
//            try {
//                containerList = portWarehouse.getContainer(numberOfConteiners);
//                if (containerList != null) {
//                    logger.debug("Сейчас в порту " + portWarehouse.getRealSize());
//                    tempResult = shipWarehouse.addContainer(containerList);
//                    logger.debug("После загрузки с корабля " + numberOfConteiners
//                            + " контейнеров = " + (portWarehouse.getRealSize()) + " свободных = "
//                            + portWarehouse.getFreeSize());
//                }
//                result = tempResult;
//            } finally {
//                if ((!tempResult)&&(containerList!=null)){ 	//return the containers what was got from from warehouse
//                    shipWarehouse.addContainer(containerList);
//                }
//            }
//        }
//        return result;
//    }
}
