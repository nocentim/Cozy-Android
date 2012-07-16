function sayhello () {
	alert('hi,' + document.getElementById('name').value + '!') ;
}

function enter () {
	alert('appui sur la touche entre') ;
}

function showAndroidToast(toast) {
   	Android.showToast(toast);
}
