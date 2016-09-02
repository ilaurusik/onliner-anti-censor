var storage = {};

function mergeCommets(actual, stored){
	getCurrentTabUrl(function(url){
		var storedComments = storage['url'];
		if (!storedComments){
			storage['url'] = actual;
			return;
		}
		
		// TODO: merge 
	});
}


/**
 @param url
 @param callback(responceXml)
 */
function fetchActualComments(url, callback){
	var xhttp = new XMLHttpRequest();
	xhttp.responseType='document';
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			callback(this.responseXML);
		}
	};
	xhttp.open("GET", url, true);
	xhttp.send();
}


/**
 * Get the current URL.
 *
 * @param {function(string)} callback - called when the URL of the current tab
 *   is found.
 */
function getCurrentTabUrl(callback) {
  // Query filter to be passed to chrome.tabs.query - see
  // https://developer.chrome.com/extensions/tabs#method-query
  var queryInfo = {
    active: true,
    currentWindow: true
  };

  chrome.tabs.query(queryInfo, function(tabs) {
    // chrome.tabs.query invokes the callback with a list of tabs that match the
    // query. When the popup is opened, there is certainly a window and at least
    // one tab, so we can safely assume that |tabs| is a non-empty array.
    // A window can only have one active tab at a time, so the array consists of
    // exactly one tab.
    var tab = tabs[0];

    // A tab is a plain object that provides information about the tab.
    // See https://developer.chrome.com/extensions/tabs#type-Tab
    var url = tab.url;

    // tab.url is only available if the "activeTab" permission is declared.
    // If you want to see the URL of other tabs (e.g. after removing active:true
    // from |queryInfo|), then the "tabs" permission is required to see their
    // "url" properties.
    console.assert(typeof url == 'string', 'tab.url should be a string');

    callback(url);
  });

  // Most methods of the Chrome extension APIs are asynchronous. This means that
  // you CANNOT do something like this:
  //
  // var url;
  // chrome.tabs.query(queryInfo, function(tabs) {
  //   url = tabs[0].url;
  // });
  // alert(url); // Shows "undefined", because chrome.tabs.query is async.
}

function renderStatus(xmlDoc) {
    document.getElementById('status').innerHTML =
		xmlDoc.getElementsByTagName("title")[0].childNodes[0].nodeValue;
}

function subscribe(){
	console.log('Subscribe');
	document.getElementById('subscribe').style.display='none';
	document.getElementById('unsubscribe').style.display='block';

	getCurrentTabUrl(function(url){
			fetchActualComments(url, function(documentXML){
				storage[url] =  documentXML.getElementById("commentsList").childNodes;
			});
		}
	);
	return true;
}
function unsubscribe(){
	console.log('UnSubscribe');
	document.getElementById('unsubscribe').style.display='none';
	document.getElementById('subscribe').style.display='block';
	return true;
}


document.addEventListener('DOMContentLoaded', function() {

	document.getElementById('sButton').addEventListener('click', function() {
		subscribe();
	});
	document.getElementById('unsButton').addEventListener('click', function() {
		unsubscribe();
	});


	getCurrentTabUrl(function(url) {
		  
		/*var xhttp = new XMLHttpRequest();
		xhttp.responseType='document';
		xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				renderStatus(this.responseXML);
			}
		};
		xhttp.open("GET", url, true);
		xhttp.send();*/
	});
});
