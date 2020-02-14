$("#full").click();
setTimeout(function(){
    if($("#layer_new_view_iframe").length>0){
        window.location.href=$("#layer_new_view_iframe").prop("src");
    }
},2000);
