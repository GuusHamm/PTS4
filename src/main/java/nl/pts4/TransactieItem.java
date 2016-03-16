package nl.pts4;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class TransactieItem
{
    private int id;
    private Date orderDate;
    //TODO replace with an account
    private Account account;
    //TODO replace with a PhotoConfiguration object (if we'll make one of those
    private ArrayList<PhotoConfigItem> photos;

    public TransactieItem(int id, Date orderDate, int accountID, ArrayList<Integer> photoIDs) {
        this.id = id;
        this.orderDate = orderDate;
        //TODO account pakken met Account id
//        this.account = accountController.getAccount(accountID)
        //TODO Get photos with the ids
        for (int i : photoIDs) {
//            photos.add(photoController.getPhotoConfigItem(i));
        }
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public ArrayList<PhotoConfigItem> getPhotos()
    {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoConfigItem> photos)
    {
        this.photos = photos;
    }

    public Date getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(Date orderDate)
    {
        this.orderDate = orderDate;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
