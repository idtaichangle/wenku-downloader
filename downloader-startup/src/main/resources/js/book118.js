
function getDocType(){ return $("#Preview .title img").attr("src").split("productView-")[1].split("_")[0];}
function getDocName() {return doc_title;}
function getPageCount() {
    var search=$(".view-dialog p:first").text().search("其中可免费阅读\\d");
    if(search>=0){
        return $(".view-dialog p:first").text().match("(?<=其中可免费阅读)\\d")[0]
    }else{
        return $('#pagenumber').text().replace(/[^0-9]/ig,"").trim();
    }
}


function snapshotInterval() {return 2000;}


function openIframe() {

    $("#full").click();
    setTimeout(function(){
        window.location.href= $("iframe[id*='view']").attr("src");
    },500);

}

function prepare() {

    setTimeout(function () {
        $("#alert").remove();
        $("#newView .bar").remove();
        $(".webpreview-recommend").remove();
        $(".bd").css("padding-bottom","3000px");
        $("#webpreview_last_cel").click();
    },1000);

    setTimeout(function () {
        goToPage(1);
    },1100);
}


function getPageWidth() {
    if($("div[data-id=1] img").length>0){
        return $("div[data-id=1] img").width();
    }else if($("#p0 img").length>0){
        return $("#p0 img").width();
    }else if($("#view0").length>0){//ppt
        return $("#view0").width()*scaleFactor();
    }
    return 0;
}

function getPageHeight() {
    if($("div[data-id=1] img").length>0){
        return $("div[data-id=1] img").height();
    }else if($("#p0 img").length>0){
        return $("#p0 img").height();
    }else if($("#view0").length>0){//ppt
        return $("#view0").height()*scaleFactor();
    }
    return 0;
}

function getPageLeftMargin() {
    if($("div[data-id=1] img").length>0){
        return $("div[data-id=1] img").offset().left;
    }else if($("#p0 img").length>0){
        return $("#p0 img").offset().left;
    }else if($("#view0").length>0){//ppt
        return $("#view0").offset().left;
    }
    return 0;
}


function scaleFactor() {
    return $("#view").attr("style").match("(?<=scale\\().+(?=\\))")[0];
}

function goToPage(page) {
    if($("div[data-id=1] img").length>0){
        $("div[data-id="+page+"] img")[0].scrollIntoView();
    }else if($("#p0 img").length>0){
        $("#p"+(page-1)+" img")[0].scrollIntoView();
    }else if($("#view0").length>0){//ppt
        nextPage();
        goPage(page-1);
        skipAnimation(page);
    }
}

function skipAnimation(page) {// skip ppt animation,show all content
    if($("#animt"+(page-1)).length>0){// exist animt in this page
        $("#slide"+(page-1)+" div[id*=sp").show();
    }
}
