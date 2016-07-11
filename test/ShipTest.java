import org.junit.BeforeClass;
import org.junit.Test;
import port.Berth;
import port.Port;
import ship.Ship;
import warehouse.Container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;


public class ShipTest {
    private static Ship ship1;
    private static int сontainerBatchSize = 15;
    private static int shipWarehouse = 90;
    @BeforeClass
    public static void init(){

        List<Container> containerList = new ArrayList<Container>(сontainerBatchSize); // создаем 15 контейнеров
        for (int i = 0; i < сontainerBatchSize; i++) {   //добавляем под размер порта контейнеры
            containerList.add(new Container(i));   //с id от 0 до 15
        }
        int warehousePortsSize = 900;
        int berthPortsSize = 2;
        Port port = new Port(berthPortsSize, warehousePortsSize);
        port.setContainersToWarehouse(containerList); //добавляем 15 контейнеров на к новому складу

        containerList = new ArrayList<Container>(сontainerBatchSize); // создаем еще 15 контейнеров
        for (int i = 0; i < сontainerBatchSize; i++) {
            containerList.add(new Container(i + 30));               //с id от 30 до 45
        }
        ship1 = new Ship("Ship1", port, shipWarehouse);                //создаем корабль с именем Ship1,
        ship1.setContainersToWarehouse(containerList);  //добавляем 15 контейнеров в хранилище корабля
    }


    @Test
    public void containersToPortCount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = getMethodByName("containersToPortCount");
        for (int i = 0; i < 100; i++) {
           Integer res = (Integer) m.invoke(ship1);
            assertTrue(сontainerBatchSize >= res);
        }
        m.setAccessible(false);
    }

    @Test
    public void containersFromPortCount()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = getMethodByName("containersFromPortCount");
        for (int i = 0; i < 100; i++) {
            Integer res = (Integer) m.invoke(ship1);
            System.out.println(res);
            assertTrue(shipWarehouse - сontainerBatchSize >= res);
        }
        m.setAccessible(false);
    }

    private int conteinersToPortCount(){
        Random random = new Random();
        return random.nextInt(20) + 1;
    }

    private int conteinersFromPortCount() {
        Random random = new Random();
        return random.nextInt(20) + 1;
    }

  /*  private int conteinersCount() {//!!!!   //old version : can load and unload random containers count,
        // despite of the limited warehouse storage
        Random random = new Random();
        return random.nextInt(20) + 1;
    }*/

    private Method getMethodByName(String name) throws NoSuchMethodException {
        Method m = ship1.getClass().getDeclaredMethod(name);
        m.setAccessible(true);
        return m;
    }

}