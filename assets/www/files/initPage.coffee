initPage = () ->
    $("body").html '<div id="editorContent" class="table-ly-ctnt"><iframe id="editorIframe" src="#"><html id="__ed-iframe-html"><head><link rel="stylesheet" href="editor.css"></head><body id="__ed-iframe-body" contenteditable="true"></body></html></iframe></div>'

    editorIframe$ = $("iframe")
    $("iframe").on "onHistoryChanged", () ->
        console.log "history updated"
    
    # callback to execute after editor's initialization
    # the contexte (this) inside the function is the editor
    cb = () ->
        this.deleteContent()

    # creation of the editor
    editor = new CNEditor( $('#editorIframe')[0], cb )
    return editor
