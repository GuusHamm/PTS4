function refreshPrices(prices) {
    $(".priceItem").each(function (index) {
        var effect = $(this).find("#effectSelect").val();
        var item = $(this).find("#itemSelect").val();
        var priceLabel = $(this).find("#itemprice");

        var effectPrice = 0, itemPrice = 0;
        $.each(prices.effectModels, function (index, value) {
            if (value.id == effect) {
                effectPrice = value.price;
            }
        });
        $.each(prices.itemModels, function (index, value){
            if (value.id == item) {
                itemPrice = value.price;
            }
        });
        var photoPrice = prices.photoModels[index].price;
        var priceInt = parseFloat(effectPrice) + parseFloat(itemPrice) + parseFloat(photoPrice);
        priceLabel.text("Price: €" + priceInt);

        var item_id = 'item_name_' + (index+1)
        var item_description = $('#itemSelect option:selected').text() +' with ' + $('#effectSelect option:selected').text() + ' Effect'
        $("input[name=" + item_id + "]").val(item_description);

        var item_id  = 'amount_' + (index+1)
        $("input[name=" + item_id  + "]").val(priceInt);


        console.log(priceInt);
        console.log("%s effect, %s item", effect, item);
        document.getElementsByName("amount_"+index).value = priceInt;
    });
}

$(document).ready(function () {
    $.get("/order/price", function (data) {
        refreshPrices(data);

        $("select").change( function(){ // any select that changes.
            refreshPrices(data);
        });
    });
});