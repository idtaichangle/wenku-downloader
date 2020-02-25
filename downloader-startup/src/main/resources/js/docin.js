function getDocType(){ return jQuery('.info_list dd:nth-child(2)').text();}
function getDocName() {return jQuery("meta[property='og:title']").attr("content");}
function getPageCount() {return jQuery('.page_num').text().split('/')[1];}
function getPageWidth() {
    if(getDocType().indexOf("ppt")>=0){
        return jQuery('#page_1 .panel_inner').width();
    }else{
        return jQuery('#page_1').width();
    }
}
function getPageHeight() {
    if(getDocType().indexOf("ppt")>=0){
        return jQuery('#page_1 .panel_inner').height();
    }else{
        return jQuery('#page_1').height();
    }
}
function getPageLeftMargin() {
    if(getDocType().indexOf("ppt")>=0){
        return jQuery('#page_1 .panel_inner').offset().left;
    }else{
        return jQuery('#page_1').offset().left;
    }
}
function snapshotInterval() {return 3000;}


function prepare() {
    if(jQuery(".btn_fullscreen").hasClass("btn_none")){/*不能全屏*/
        jQuery('.convertTips').remove();
        jQuery('#html_1').css('padding-top',0);
    }else{
        jQuery('.btn_fullscreen').click();
        jQuery('#j_zoomout').click();
    }
    jQuery('.adBox').remove();
    jQuery('.backToTop').remove();
    jQuery('.docin_reader_tools').remove();
    jQuery('#docinShareSlider').remove();
    jQuery('.aside').remove();
    jQuery('.reader_tools_bar_wrap').remove();
    document.getElementById('page_1').scrollIntoView();
    jQuery('#contentcontainer').css('padding-bottom','1000px');
}


function goToPage(page) {
    docinReader.gotoPage(page,1);
    jQuery("#page_"+page)[0].scrollIntoView();
}