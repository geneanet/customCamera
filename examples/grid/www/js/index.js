function getGrid(inverse) {
    var format = "image/png";
    var width = window.innerWidth * devicePixelRatio;
    var height = window.innerHeight * devicePixelRatio;
    if (inverse) {
        width = window.innerHeight * devicePixelRatio;
        height = window.innerWidth * devicePixelRatio; 
    }
    var widthInterval = width * 0.25;
    var heightInterval = height * 0.25;
    var x = widthInterval;
    var y = heightInterval;

    var canvas = document.getElementById('my-canvas');;
    canvas.width = width;
    canvas.height = height;
    
    var ctx = canvas.getContext("2d");

    ctx.beginPath();

    while (x < width) {
        ctx.moveTo(x, 0);
        ctx.lineTo(x, height);
        x += widthInterval;
    }

    while (y < height) {
        ctx.moveTo(0, y);
        ctx.lineTo(width, y);
        y += heightInterval;
    }
    ctx.stroke();

    ctx.closePath();

    var base64 = canvas.toDataURL(format);

    return base64.replace(/data:[^\/]*\/[^\,]*,/, "");
};

document.getElementById("start-camera").onclick = function() {
    navigator.GeneanetCustomCamera.startCamera(
        {
            imgBackgroundBase64: getGrid(),
            imgBackgroundBase64OtherOrientation: getGrid(true),
            opacity: false,
            miniature: false
        },
        function() {
            window.console.log("success");
        },
        function() {
            window.console.log("fail");
        }
    );
}