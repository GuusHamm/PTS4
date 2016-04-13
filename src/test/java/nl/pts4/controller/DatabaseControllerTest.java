package nl.pts4.controller;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseControllerTest {
	DatabaseController databaseController;
	@Before
	public void setup(){
		DatabaseController.getTestInstance().createTables();
		databaseController = DatabaseController.getTestInstance();
	}

	@Test
	public void testGetAllOrders() throws Exception {
		DatabaseController.getInstance().getAllOrders();
	}

	@Test
	public void testRegisterAccount() throws Exception {
		// Test short password
		databaseController.createAccount("Name name", "email@email.com", "sh");

	}
}