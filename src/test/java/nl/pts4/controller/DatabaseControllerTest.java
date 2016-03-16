package nl.pts4.controller;

import org.junit.Test;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseControllerTest {

	@Test
	public void testGetInstance() throws Exception {
		DatabaseController databaseController = DatabaseController.getInstance();

	}

	@Test
	public void testCreateAccount() throws Exception{
		DatabaseController.getInstance().createAccount("Guus Hamm", "guushamm@gmail.com","test");
	}
}