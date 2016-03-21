package nl.pts4.model;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderLineModel
{
    private int amount;
    private PhotoConfigurationModel photoConfiguration;



    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public void setPhoto(int photoID) {
//        this.photo = photoController.getPhotoConfigItemById(photoID);
    }

    public PhotoConfigurationModel getPhotoConfiguration()
    {
        return photoConfiguration;
    }
}
