package nl.pts4.model;

import nl.pts4.controller.DatabaseController;
import nl.pts4.controller.MainController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Teun on 6-4-2016.
 */
public class PriceModel {

    private List<EffectModel> effectModels;
    private List<ItemModel> itemModels;
    private List<PhotoModel> photoModels;

    public PriceModel(){}

    public List<EffectModel> getEffectModels() {
        return effectModels;
    }

    public List<ItemModel> getItemModels() {
        return itemModels;
    }

    public List<PhotoModel> getPhotoModels() {
        return photoModels;
    }

    public static PriceModel readAll(HttpServletRequest request) {
        List<PhotoModel> cart = (List<PhotoModel>) request.getSession().getAttribute(MainController.CART_ATTRIBUTE);

        PriceModel priceModel = new PriceModel();
        DatabaseController databaseController = DatabaseController.getInstance();

        priceModel.itemModels = databaseController.getItems();
        priceModel.effectModels = databaseController.getEffects();
        priceModel.photoModels = cart;
        return priceModel;
    }
}
