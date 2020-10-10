function getDocType(){ return undefined;}
function getDocName() {return $("meta[property='og:title']").attr("content");}
function getPageCount() {return $("meta[property='og:document:page']").attr("content");}
function getPageWidth() {return $('#outer_page_1').width();}
function getPageHeight() {return $('#outer_page_1').height();}
function getPageLeftMargin() {return $('#outer_page_1').offset().left;}
function snapshotInterval() {return 5000;}


function prepare() {
    $("#frscreen").click();
    $("#continueButton").click();
    $("#zoomInButton").click();
    $("#zoomInButton").click();
    $("#zoomInButton").click();
    $("#mainPanel").css("padding","0px");
    $("#header").remove();
    $("#readshop").remove();
    $("#readshop").remove();
    $(".edit-tips").remove();
    $(".skintips").remove();
    $("#pageContainer").css("padding-bottom","3000px");
    setTimeout(function () {
        goToPage(1);
    },200);
}

function goToPage(page) {
    $(".dk-bg").parent().remove();
    $("#outer_page_"+page)[0].scrollIntoView();
}