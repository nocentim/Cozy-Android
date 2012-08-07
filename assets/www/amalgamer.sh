cat ../app/views/editor.coffee | sed -e 's/class exports.CNEditor extends Backbone.View/class CNEditor/g' > modified_editor.coffee

cat modified_editor.coffee | sed -e 's/stylesheets\/app.css/editor.css/g' > maglama.coffee

cat files/initPage.coffee maglama.coffee > amalgamed.coffee

stylus ../app/styles/editor.styl && mv ../app/styles/editor.css .

echo "initPage()" >> amalgamed.coffee

coffee -c amalgamed.coffee

cat ../vendor/scripts/jquery-1.7.1.js ../vendor/scripts/rangy-core.js ../vendor/scripts/rangy-selectionsaverestore-uncompressed.js ../vendor/scripts/showdown.js amalgamed.js > bin/amalgam.js

rm -f modified_editor.coffee maglama.coffee amalgamed.js amalgamed.coffee

