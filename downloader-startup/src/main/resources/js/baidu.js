
function getDocType(){
    if(typeof pageData !== "undefined"){
        return pageData.tplName;
    }
    return window.__fisData._data.WkInfo.DocInfo.docType;
}
function getDocName() {
    if(typeof pageData !== "undefined"){
        return pageData.docInfo2019.doc_info.title;
    }
    if(typeof window.__fisData !== "undefined"){
        return window.__fisData._data.WkInfo.DocInfo.title;
    }
    return $("#doc-tittle-0").text();
}

function getPageCount() {
    if(typeof pageData !== "undefined"){
        return pageData.readerInfo2019.free_page;
    }
    if(typeof window.__fisData !== "undefined"){
        if(window.__fisData._data.WkInfo.DocInfo.freepagenum>0){
            return window.__fisData._data.WkInfo.DocInfo.freepagenum;
        }
        else{
            return window.__fisData._data.WkInfo.DocInfo.totalPageNum;
        }
    }
    return $('.page-count').text().split('/')[1];
}

function snapshotInterval() {return 1000;}


function prepare() {
    $(".moreBtn").click();// more
	$(".fold-page-text .read-all").click();
    $(".top-right-fullScreen").click(); //full screen
	$("div.full-screen").click();

	$("div.reader-topbar").remove();
	$("div.content-wrapper").css("padding","0px");
    $(".banner-ad").remove();
    $(".fix-searchbar-wrap").remove();
    $(".reader-tools-bar-wrap").remove();
    $(".reader-back2top-wrap").remove();

    $(".tag-tips").remove();
    $("#doc-header-test").remove();
    $(".lastcell-dialog").remove();// pop ad
    $("#docBubble").remove();
	$(".try-end-fold-page").remove();

    $("#reader-container-inner-1").css('padding-bottom','3000px');
    $("body").css("overflow-x","hidden");


    setTimeout(function () {
        $(".top-right-fullScreen").remove();
        goToPage(1);
        window.scrollBy(0,window.innerHeight);
        goToPage(1);
    },1000);
}

function getPageWidth() {
    if($(".reader-pageNo-1 img").length>0){
        return $(".reader-pageNo-1 img").width();
    }else if($('.reader-page-1').length>0){
        return $('.reader-page-1').width();
    }else if($('#pageNo-1').length>0){
        return $('#pageNo-1').width();
    }
	return 0;
}

function getPageHeight() {
	if($(".reader-pageNo-1 img").length>0){
        return $(".reader-pageNo-1 img").height();
    }else if($('.reader-page-1').length>0){
        return $('.reader-page-1').height();
    }else if($('#pageNo-1').length>0){
        return $('#pageNo-1').height();
    }
	return 0;
}

function getPageLeftMargin() {
	if($(".reader-pageNo-1 img").length>0){
        return $(".reader-pageNo-1 img").offset().left;
    }else if($('.reader-page-1').length>0){
        return $('.reader-page-1').offset().left;
    }else if($('#pageNo-1').length>0){
        return $('#pageNo-1').offset().left;
    }
	return 0;
}

function goToPage(page) {
	if($(".reader-pageNo-"+page).length>0){
        $(".reader-pageNo-"+page)[0].scrollIntoView();
    }else if($('.reader-page-'+page).length>0){
        $('.reader-page-'+page)[0].scrollIntoView();
    }else if($('#pageNo-'+page).length>0){
        $('#pageNo-'+page)[0].scrollIntoView();
    }
}