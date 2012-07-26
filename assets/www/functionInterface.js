
function indentation () {
	var editor_html$ = $("iframe").contents().find("html")
	var editorBody$  = editor_html$.find("body")
	var editorCtrler = editorBody$.prop( '__editorCtl')
	editorCtrler.tab()
}

function unindentation () {
	var editor_html$ = $("iframe").contents().find("html")
	var editorBody$  = editor_html$.find("body")
	var editorCtrler = editorBody$.prop( '__editorCtl')
	editorCtrler.shiftTab()
}	

function markerListAndroid() {
		var editor_html$ = $("iframe").contents().find("html")
	var editorBody$  = editor_html$.find("body")
	var editorCtrler = editorBody$.prop( '__editorCtl')
	editorCtrler.markerList()
}

function titleListAndroid() {
		var editor_html$ = $("iframe").contents().find("html")
	var editorBody$  = editor_html$.find("body")
	var editorCtrler = editorBody$.prop( '__editorCtl')
	editorCtrler.titleList()
}
