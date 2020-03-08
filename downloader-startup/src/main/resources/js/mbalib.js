function getDocType(){ return wgDocType;}
function getDocName() {return wgDocTitle;}
function getPageCount() {return $(".num span").text().split('/')[1];}
function getPageWidth() {return $("#page0").width();}
function getPageHeight() {return $("#page0").height();}
function getPageLeftMargin() {return $("#page0").offset().left;}
function snapshotInterval() {return 3000;}

function prepare() {
    $("#fullScreen").click();
    $(".ad-br").remove();
    $("#doc-fix").remove();
    $(".reader-end-download").css("padding-top","3000px");
    $("body").css("overflow-x","hidden");
    $(".wjdx-close").click();
    setTimeout(function () {
        goToPage(1);
    },200);
}

function goToPage(page) {
    $("#page"+(page-1))[0].scrollIntoView();
}