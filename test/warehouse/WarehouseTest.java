package warehouse;

import org.junit.BeforeClass;
import org.junit.Test;
import port.Port;
import ship.Ship;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class WarehouseTest {
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

        containerList = new ArrayList<Container>(сontainerBatchSize); // создаем еще 80 контейнеров
        for (int i = 0; i < сontainerBatchSize; i++) {
            containerList.add(new Container(i + 30));               //с id от 30 до 45
        }

        ship1 = new Ship("Ship1", port, shipWarehouse);                //создаем корабль с именем Ship1,
        ship1.setContainersToWarehouse(containerList);  //добавляем 15 контейнеров в хранилище корабля
    }


    @Test
    public void addContainer() throws Exception {

    }

    @Test
    public void addContainer1() throws Exception {

    }

    @Test
    public void getContainer() throws Exception {

    }

    @Test
    public void getContainer1() throws Exception {

    }

    @Test
    public void getSize() throws Exception {
        assertEquals(shipWarehouse, ship1.getSize());
    }

    @Test
    public void getRealSize() throws Exception {
        int expected = сontainerBatchSize;
        assertEquals(expected, ship1.getRealSize());
    }

    @Test
    public void getFreeSize() throws Exception {
        int expected = shipWarehouse - сontainerBatchSize;
        assertEquals(expected, ship1.getFreeSize());
    }

}