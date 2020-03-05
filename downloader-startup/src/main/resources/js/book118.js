
function getDocType(){ return $("#Preview .title img").attr("src").split("productView-")[1].split("_")[0];}
function getDocName() {return doc_title;}
function getPageCount() {return $('#pagenumber').text().replace("页","").trim();}
function getPageWidth() {return $("iframe:last").contents().find("div[data-id=1]").width();}
function getPageHeight() {return $("iframe:last").contents().find("div[data-id=1]").height();}
function getPageLeftMargin() {return $("iframe:last").offset().left;}
function snapshotInterval() {return 2000;}


function prepare() {
    side_customer.close();
    $("#full").click();

    setTimeout(function(){
        var frame = $("iframe:last").contents();
        frame.find("#alert").remove();
        frame.find("#newView .bar").remove();
        frame.find(".webpreview-recommend").remove();
        frame.find(".bd").css("padding-bottom","1000px")
        frame.find("#webpreview_last_cel").click();

    },1000);

    setTimeout(function () {
        goToPage(1);
    },1100);
}

function goToPage(page) {
    $("iframe:last").contents().find("div[data-id="+page+"]")[0].scrollIntoView();
}