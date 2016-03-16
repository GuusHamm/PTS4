package nl.pts4;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderLine
{
    private PhotoConfigItem photo;
    private int amount;

    public OrderLine(int photoID, int amount) {
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
