function encodeBase64FromImg(picture, format) {
    format = format ? format : "image/jpg";
    
    var canvas = document.createElement("canvas");
    canvas.width = picture.naturalWidth;
    canvas.height = picture.naturalHeight;
    
    var ctx = canvas.getContext("2d");
    ctx.drawImage(picture, 0, 0);

    var base64 = canvas.toDataURL(format);

    return base64.replace(/data:[^\/]*\/[^\,]*,/, "");
};

document.getElementById("start-camera").onclick = function() {
    navigator.GeneanetCustomCamera.startCamera(
        {
            imgBackgroundBase64: encodeBase64FromImg(document.getElementsByTagName("img")[0], "image/png"),
//            imgBackgroundBase64OtherOrientation: encodeBase64FromImg(document.getElementsByTagName("img")[1], "image/png"),
            opacity: false,
            miniature: false,
            quality: 75,
            targetWeight:640,
            targetHeight:480
        },
        function(imageData) {
            var image = document.getElementById('myImage');
            image.src = "data:image/jpeg;base64," + imageData;

            localStorage.setItem('image', image.src);            
        },
        function() {
            window.console.log("fail");
        }
    );
}