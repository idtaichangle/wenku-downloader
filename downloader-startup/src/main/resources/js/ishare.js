
for(var i=0;i<100;i++){
    $(".show-more-text").click();
}

function getDocType(){ return pageConfig.report.format;}
function getDocName() {return $('.crumb strong').text().split('.')[0];}
function getPageCount() {return $('.page-input-con span').text();}
function getPageWidth() {return $("div[data-num='1']").width();}
function getPageHeight() {return $("div[data-num='1']").height();}
function getPageLeftMargin() {return $("div[data-num='1']").offset().left;}
function snapshotInterval() {return 3000;}


function prepare() {
    $("a.reader-fullScreen").click();
    $("div.detail-fixed-full").remove();
    $("div.icon-vip-share-wrap").remove();
    $("span.reader-fullScreen-no").remove();
    $("div.pc-tui-coupon").remove();
    $(".tui-detail").remove();
    $("div.icon-detail-wrap").remove();
    $(".detail-main").css("padding-bottom","1000px");
    $("body").css("overflow-x","hidden");
    setTimeout(function () {
        goToPage(1);
    },200);
}

function goToPage(page) {
    $("div[data-num='"+page+"']")[0].scrollIntoView();
}