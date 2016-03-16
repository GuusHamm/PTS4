package nl.pts4.controller;

import javax.sql.DataSource;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseController {
	private DataSource dataSource;

	private static DatabaseController instance;

	public static DatabaseController getInstance(){
		if (instance == null){
			instance = new DatabaseController();
		}
		return instance;
	}
}
