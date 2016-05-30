function refreshPrices(prices) {
    $(".priceItem").each(function (index) {
        console.log($(this));
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
        var priceInt = parseInt(effectPrice) + parseInt(itemPrice) + parseInt(photoPrice);
        priceLabel.text("Price: €" + priceInt);
        document.getElementsByName("amount_"+index).value = priceInt;
        //document.getElementById("ppvalue"+index).value = priceInt;
        console.log(priceInt);
        console.log("%s effect, %s item", effect, item);
    });
}

$(document).ready(function () {

    $.get("/order/price", function (data) {
        refreshPrices(data);

        $("select").change( function(){ // any select that changes.
            refreshPrices(data);
        })
    });



});