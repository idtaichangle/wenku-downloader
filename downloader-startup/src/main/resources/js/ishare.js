
for(var i=0;i<300;i++){
    $(".show-more-text").click();
}

function getDocType(){ return pageConfig.report.format;}
function getDocName() {return $('.crumb strong').text().split('.')[0];}
function getPageCount() {
    return $("div[data-num]").length;
}
function snapshotInterval() {return 3000;}


function prepare() {
    $("a.reader-fullScreen").click();
    $("div.detail-fixed-full").hide();
    $("div.icon-vip-share-wrap").remove();
    $("span.reader-fullScreen-no").remove();
    $("div.pc-tui-coupon").remove();
    $(".tui-detail").remove();
    $("div.icon-detail-wrap").remove();
    $(".detail-main").css("padding-bottom","3000px");
    $("body").css("overflow-x","hidden");
    setTimeout(function () {
        goToPage(1);
    },200);
}

function getPageWidth() {
    if($("div[data-num='1'] img").length>0){
        return $("div[data-num='1'] img").width()*scaleFactor();
    }else{
        return $("div[data-num='1']").width();
    }
}
function getPageHeight() {
    if($("div[data-num='1'] img").length>0){
        return $("div[data-num='1'] img").height()*scaleFactor();
    }else{
        return $("div[data-num='1']").height();
    }
}
function getPageLeftMargin() {
    if($("div[data-num='1'] img").length>0){
        return $("div[data-num='1'] img").offset().left;
    }else{
        return $("div[data-num='1']").offset().left;
    }
}

function scaleFactor() {
    return $("div[data-num='1'] .article-main").attr("style").match("(?<=scale\\().+(?=\\))")[0];
}

function goToPage(page) {
    if($("div[data-num='1'] img").length>0){
        $("div[data-num='"+page+"'] img")[0].scrollIntoView();
    }else{
        $("div[data-num='"+page+"']")[0].scrollIntoView();
    }
}