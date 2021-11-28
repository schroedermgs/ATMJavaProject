const imageSources = ["resources/images/ice-sunset.png",
"resources/images/penguin-family2.png",
"resources/images/cute-seals.png",
"resources/images/iceberg.png"];
var imageIndex = 0;

function displayBannerImage(){
	imageIndex = (imageIndex===imageSources.length-1) ? 0 : imageIndex+1;
	document.getElementById("header").style.backgroundImage = `url('${imageSources[imageIndex]}')`
}

setInterval(displayBannerImage,11000);

var loggedTime = 45;
var counting = false;

function countdownTimeout(){
	if(counting){
		loggedTime -= 1;
		if(loggedTime<=0){
			counting = false;
			logout();
			resetTimer();
			setTimeout(()=>alert("You have been logged out due to inactivity."),1200)
		}
	}
}

var timerTick = setInterval(countdownTimeout,1000);
clearInterval(timerTick);

function resetTimer(){
	loggedTime = 45;
}
window.addEventListener("mousemove",resetTimer);


function fixTwoDecimals(num){
	var newNum = String(num);
	try{
		newNum = newNum.split(".")[0] + "." + newNum.split(".")[1].substring(0,2);
	}catch(e){}
	return newNum;
}