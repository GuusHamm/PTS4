package nl.pts4.Models;

import nl.pts4.PhotoConfigItem;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderLineModel
{
    private PhotoConfigItem photo;
    private int amount;

    public OrderLineModel(int photoID, int amount) {
//        this.photo = photoController.getPhotoConfigItemById(photoID);
        this.amount = amount;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public PhotoConfigItem getPhoto()
    {
        return photo;
    }

    public void setPhoto(PhotoConfigItem photo)
    {
        this.photo = photo;
    }

    public void setPhoto(int photoID)
    {
//        this.photo = photoController.getPhotoConfigItemById(photoID);
    }

}
