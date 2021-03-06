function getDocType(){ return $("meta[property='og:document:type']").attr("content");}
function getDocName() {return $("meta[property='og:title']").attr("content");}
function getPageCount() {
    if(typeof ForceFreepage !== "undefined"){
        return ForceFreepage;
    }
    return $("meta[property='og:document:page']").attr("content");
}
function getPageWidth() {return $('#outer_page_1').width();}
function getPageHeight() {return $('#outer_page_1').height();}
function getPageLeftMargin() {return $('#outer_page_1').offset().left;}
function snapshotInterval() {return 3000;}


function prepare() {
    showmorepage();
    $("#readshop").remove();
    $("#boxright").remove();
    $("#page_more").css("padding-bottom","3000px");
    $("body").css("overflow-x","hidden");
    setTimeout(function () {
        goToPage(1);
    },200);
}

function goToPage(page) {
    $("#outer_page_"+page)[0].scrollIntoView();
}
