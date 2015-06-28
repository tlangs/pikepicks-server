$(document).ready(function() {
    $("#inputPokemon").chosen();
});

$('#bulkUploadForm').submit(function() {
    if ($("#useFile")[0].checked) {
        this.action = "fileupload";
        this.method = "post";
        this.enctype =  "multipart/form-data";
    } else {
        this.action = "bulkupload";
        this.method = "get";
        this.enctype = "";
    }
    return true;
});

$("#useFile").click(function() {
    if (this.checked) {
        $("#bulkInput").prop("disabled", true);
        $("#fileUpload").prop("disabled", false);
    } else {
        $("#bulkInput").prop("disabled", false);
        $("#fileUpload").prop("disabled", true);
    }
});


$("#submitPokeForm").click(function() {
    var regions = "";
    if ($("#regionSelection").is(":visible") && $("#pickRegionsRadio").prop("checked")) {
        $("#regions input[type=checkbox]").each(function (i, box) {
            if (box.checked) regions += $(box).prop("id") + ",";
        });
    }
    if (regions.length != 0) regions = regions.substring(0, regions.length - 1);
    var url = "makepokemon?name=" + $("#inputText").val() +
        "&pokemon=" + $("#inputPokemon").chosen().val() +
        "&regions=" + regions +
        "&change=" + Math.random();
    $("#outputImg").prop("src", url);
});

$("#inputPokemon").chosen().change(function() {
    if ($(this).chosen().val() == "") {
        $("#regionSelection").slideDown();
    } else {
        $("#regionSelection").slideUp()
    }
});
//$("#inputPokemon").keyup(function(event) {
//    if ($(event.target).chosen().val() == "") {
//        $("#regionSelection").slideDown();
//    } else {
//        $("#regionSelection").slideUp()
//    }
//});

$("input[name=regionOptions]").click(function(event) {
    if ($(event.target).prop("id") == "allRegionsRadio") {
        $("#regions input").prop("disabled", true);
    }
    if ($(event.target).prop("id") == "pickRegionsRadio") {
        $("#regions input").prop("disabled", false);
    }
});
