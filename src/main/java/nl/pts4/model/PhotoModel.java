package nl.pts4.model;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class PhotoModel {
	private UUID uuid;
	private AccountModel photographer;
	private AccountModel child;
	private SchoolModel school;
	private int price;
	private Date captureDate;
	private File filePath;

	public PhotoModel(UUID uuid, AccountModel photographer, AccountModel child, SchoolModel school, int price, Date captureDate, File filePath) {
		this.uuid = uuid;
		this.photographer = photographer;
		this.child = child;
		this.school = school;
		this.price = price;
		this.captureDate = captureDate;
		this.filePath = filePath;
	}
}
