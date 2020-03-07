
function getDocType(){ return window.__fisData._data.WkInfo.DocInfo.docType;}
function getDocName() {
    if(getDocType().indexOf("ppt")>=0){
        return window.__fisData._data.WkInfo.DocInfo.title;
    }else{
        return $("#doc-tittle-0").text();
    }
}
function getPageCount() {
    if(getDocType().indexOf("ppt")>=0){
        return window.__fisData._data.WkInfo.DocInfo.totalPageNum;
    }else{
        return $('.page-count').text().split('/')[1];
    }
}
function snapshotInterval() {return 1000;}


function prepare() {
    $(".moreBtn").click();// more
    $(".top-right-fullScreen").click(); //full screen

    $(".banner-ad").remove();
    $(".fix-searchbar-wrap").remove();
    $(".reader-tools-bar-wrap").remove();
    $(".reader-back2top-wrap").remove();

    $(".tag-tips").remove();
    $("#doc-header-test").remove();
    $(".lastcell-dialog").remove();// pop ad
    $("#docBubble").remove();

    $("#reader-container-inner-1").css('padding-bottom','1000px');
    $("body").css("overflow-x","hidden");


    setTimeout(function () {
        $(".top-right-fullScreen").remove();
        goToPage(1);
        window.scrollBy(0,window.innerHeight);
    },1000);
}

function getPageWidth() {
    if(getDocType().indexOf("ppt")>=0){
        return $(".reader-pageNo-1 img").width();
    }else{
        return $('.reader-page-1').width();
    }
}
function getPageHeight() {
    if(getDocType().indexOf("ppt")>=0){
        return $(".reader-pageNo-1 img").height();
    }else{
        return $('.reader-page-1').height();
    }
}
function getPageLeftMargin() {
    if(getDocType().indexOf("ppt")>=0){
        return $(".reader-pageNo-1 img").offset().left;
    }else{
        return $('.reader-page-1').offset().left;
    }
}

function goToPage(page) {
    if(getDocType().indexOf("ppt")>=0){
        $(".reader-pageNo-"+page)[0].scrollIntoView();
    }else{
        $('.reader-page-'+page)[0].scrollIntoView();
    }
}