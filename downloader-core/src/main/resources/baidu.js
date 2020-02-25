
function getDocType(){ return window.__fisData._data.WkInfo.DocInfo.docType;}
function getDocName() {return $("#doc-tittle-0").text();}
function getPageCount() {return $('.page-count').text().split('/')[1];}
function getPageWidth() {return $('.reader-page-1').width();}
function getPageHeight() {return $('.reader-page-1').height();}
function getPageLeftMargin() {return $('.reader-page-1').offset().left;}
function snapshotInterval() {return 1000;}


function prepare() {
    $(".goBtn").click();
    $(".top-right-fullScreen").remove();
    $(".reader-fullScreen").click();
    $(".banner-ad").remove();
    $(".fix-searchbar-wrap").remove();
    $(".reader-tools-bar-wrap").remove();
    $(".reader-back2top-wrap").remove();
    $(".lastcell-dialog").remove();
    $("body").css("overflow-x","hidden");

    $("#doc_bottom_wrap").remove();

    $("#next_doc_box").remove();

    $(".ft").remove();

    $("#ft").remove();
    $(".tag-tips").remove();
    $("#reader-container-inner-1").css('padding-bottom','1000px');
}

function goToPage(page) {
    if(getDocType().indexOf("ppt")>=0){
        $(".reader-pageNo-"+page)[0].scrollIntoView();
    }else{
        $('.reader-page-'+page)[0].scrollIntoView();
    }
}