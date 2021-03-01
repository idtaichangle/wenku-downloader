
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

function snapshotInterval() {return 2000;}


function prepare() {
    c(".moreBtn");
    c(".fold-page-text .read-all");
    c(".top-right-fullScreen");
    c("div.full-screen");
    c(".moreBtn");

    r("div.reader-topbar");
    r(".banner-ad");
    r(".fix-searchbar-wrap");
    r(".reader-tools-bar-wrap");
    r(".reader-back2top-wrap");

    r(".tag-tips");
    r("#doc-header-test");
    r(".lastcell-dialog");// pop ad
    r("#docBubble");
    r(".try-end-fold-page");


    if(s("div.content-wrapper")!=null){
        s("div.content-wrapper").style.padding=0;
    }
    if(s("#reader-container-inner-1")!=null){
        s("#reader-container-inner-1").style.paddingBottom=3000;
    }
    s("body").style.overflowX="hidden";


    setTimeout(function () {
        r(".top-right-fullScreen");
        goToPage(1);
        window.scrollBy(0,window.innerHeight);
        goToPage(1);
    },1000);
}

function getPageWidth() {
    if(s(".reader-pageNo-1 img")!=null){
        return s(".reader-pageNo-1 img").clientWidth;
    }else if(s('.reader-page-1')!=null){
        return s('.reader-page-1').clientWidth;
    }else if(s("#pageNo-1")!=null){
        return s("#pageNo-1").clientWidth;
    }
    return 0;
}

function getPageHeight() {
    if(s(".reader-pageNo-1 img")!=null){
        return s(".reader-pageNo-1 img").clientHeight;
    }else if(s('.reader-page-1')!=null){
        return s('.reader-page-1').clientHeight;
    }else if(s("#pageNo-1")!=null){
        return s("#pageNo-1").clientHeight;
    }
    return 0;
}

function getPageLeftMargin() {
    if(s(".reader-pageNo-1 img")!=null){
        return s(".reader-pageNo-1 img").getBoundingClientRect().x;
    }else if(s('.reader-page-1')!=null){
        return s('.reader-page-1').getBoundingClientRect().x;
    }else if(s("#pageNo-1")!=null){
        return s("#pageNo-1").getBoundingClientRect().x;
    }
    return 0;
}

function goToPage(page) {
    r(".hx-warp");
    c("#TANGRAM__PSP_4__closeBtn");//登录提示框
    c("span.read-all");//继续阅读
    if(s(".reader-pageNo-"+page)!=null){
        s(".reader-pageNo-"+page).scrollIntoView();
    }else if(s('.reader-page-'+page)!=null){
        s('.reader-page-'+page).scrollIntoView();
    }else if(s('#pageNo-'+page)!=null){
        s('#pageNo-'+page).scrollIntoView();
    }
}

function s(selector){
    return document.querySelector(selector);
}

function c(selector){
    if(s(selector)!=null){
        s(selector).click();
    }
}

function  r(selector){
    if(s(selector)!=null){
        s(selector).remove();
    }
}