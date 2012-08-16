function deleteContentAndroid() {
	var editorBody$  = $("#__ed-iframe-body") ;
	var editorCtrler = editorBody$.prop( '__editorCtl') ;
	editorCtrler.deleteContent() ;
}

function indentation () {
	var editorBody$  = $("#__ed-iframe-body") ;
	var editorCtrler = editorBody$.prop( '__editorCtl') ;
	editorCtrler.tab() ;
}

function unindentation () {
	var editorBody$  = $("#__ed-iframe-body") ;
	var editorCtrler = editorBody$.prop( '__editorCtl') ;
	editorCtrler.shiftTab() ;
}	

function markerListAndroid() {
	var editorBody$  = $("#__ed-iframe-body") ;
	var editorCtrler = editorBody$.prop( '__editorCtl') ;
	editorCtrler.markerList() ;
}

function titleListAndroid() {
	var editorBody$  = $("#__ed-iframe-body") ;
	var editorCtrler = editorBody$.prop( '__editorCtl') ;
	editorCtrler.titleList() ;
}
 
function aaagetEditorContentAndroid() {
	var editorBody$  = $("#__ed-iframe-body") ;
	var editorCtrler = editorBody$.prop( '__editorCtl') ;
	alert(editorCtrler.getEditorContent()) ;
 }
 
function getEditorContentAndroid() {
	alert(document.getElementById("__ed-iframe-body").innerHTML);
	//console.log("allo");
}
 