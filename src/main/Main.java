package main;

import java.util.ArrayList;
import java.util.List;

import port.Port;
import ship.Ship;
import warehouse.Container;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int сontainerBatchSize = 15;			//изменено. Ранее -- warehousePortSize
											// Неудачное название, ведь в реальности переменная
											// отображает размер "пачки" контейнеров, а не общий объем склада
											//
		List<Container> containerList = new ArrayList<Container>(сontainerBatchSize); // создаем 15 контейнеров
		for (int i=0; i<сontainerBatchSize; i++){   //добавляем под размер порта контейнеры
			containerList.add(new Container(i));   //с id от 0 до 15
		}

		int warehousePortsSize = 900;
		int berthPortsSize = 2;
		Port port = new Port(berthPortsSize, warehousePortsSize);
											//изменено:  "волшебные" числа обозначены переменными
											//создаем порт с 2 причалами и складом на 900 контейнеров
											//автосоздание склада с таким количеством контейн


		port.setContainersToWarehouse(containerList); //добавляем 15 контейнеров на к новому складу

		containerList = new ArrayList<Container>(сontainerBatchSize); // создаем еще 15 контейнеров
		for (int i=0; i<сontainerBatchSize; i++){
			containerList.add(new Container(i+30));               //с id от 30 до 45
		}
		Ship ship1 = new Ship("Ship1", port, 90);				//создаем корабль с именем Ship1,
														// привязанного к порту port
														// грузоподьемностью 90
		ship1.setContainersToWarehouse(containerList);  //добавляем 15 контейнеров в хранилище корабля
		
		containerList = new ArrayList<Container>(сontainerBatchSize); //еще 15 контейнеров
		for (int i=0; i<сontainerBatchSize; i++){
			containerList.add(new Container(i+60));         //с id от 60 ддо 75
		}
		Ship ship2 = new Ship("Ship2", port, 90);
														//создаем корабль с именем Ship2,
														// привязанного к порту port
														// грузоподьемностью 90
		ship2.setContainersToWarehouse(containerList);  //добавляем 15 контейнеров в хранилище корабля
		
		containerList = new ArrayList<Container>(сontainerBatchSize); //еще 15 контейнеров

		for (int i=0; i<сontainerBatchSize; i++){			 //ранее: с id от 60 ддо 75	-- в реальности те же
													// контейнеры!!! Нет equals в Container --> фактически создали новые. --> исправлен класс
													//
			containerList.add(new Container(i+90));  // логически : нельзя одни и те же контейры
													// загрузить на два разных кораблы --> исплавлен генератор id
		}
		Ship ship3 = new Ship("Ship3", port, 90); 	//создаем корабль с именем Ship3,
													// привязанного к порту port
													// грузоподьемностью 90
		ship3.setContainersToWarehouse(containerList);		// загружаем в него 15 контейнеров
		
		// итого: 1 порт на 900 контейнеров и 2 причала(занято 15): разделяемый ресурс
		// Причалы id =0, id = 1
		// 3 корабля по 90 контейнеров. В каждом по 15 контейнеров.
		// Итого контейнеров --> 60 контейнеров "курсирует"

		new Thread(ship1).start();		//отправляем корабли в плавание
		new Thread(ship2).start();		
		new Thread(ship3).start();
		

		Thread.sleep(3000);				//ждем
		
		ship1.stopThread();				// заканчиваем работу
		ship2.stopThread();
		ship3.stopThread();

	}

}
