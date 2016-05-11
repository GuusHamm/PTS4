package nl.pts4.model;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class PhotoConfigurationModel {
    private int id;
    private EffectModel effect;
    private ItemModel item;
    private PhotoModel photo;

    public PhotoConfigurationModel(int id, EffectModel effect, ItemModel item, PhotoModel photo) {
        this.id = id;
        this.effect = effect;
        this.item = item;
        this.photo = photo;
    }

    public double getTotalPrice() {
        double returnValue = 0;
        //TODO add the price of the itemModel and the EffectModel, not yet implemented as of yet.
        returnValue += photo.getPrice();
        returnValue += item.getPrice();
        returnValue += effect.getPrice();

        return returnValue;
    }
}
