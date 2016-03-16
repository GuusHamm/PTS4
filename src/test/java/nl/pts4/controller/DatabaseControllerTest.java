package nl.pts4.controller;

import org.junit.Test;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseControllerTest {

	@Test
	public void testGetAllOrders() throws Exception {
		DatabaseController.getInstance().getAllOrders();

	}
}