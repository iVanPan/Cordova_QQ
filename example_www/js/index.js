var app = function () {
  this.checkClientInstalled = function () {
    YCQQ.checkClientInstalled(function () {
      alert('client is installed');
    }, function () {
      // if installed QQ Client version is not supported sso,also will get this error
      alert('client is not installed');
    });
  };
  this.ssoLogin = function () {
    var checkClientIsInstalled = 0;//default is 0,only for iOS
    YCQQ.ssoLogin(function (args) {
      alert(args.access_token);
      alert(args.userid);
    }, function (failReason) {
      alert(failReason);
    }, checkClientIsInstalled);
  };
  this.shareToQQ = function () {
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QQ 分享测试";
    args.description = "这个是cordova QQ 分享测试";
    args.imageUrl = "https://www.baidu.com/img/bdlogo.png";
    args.appName = "cordova—QQ";
    YCQQ.shareToQQ(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  };
  this.shareToQzone = function () {
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QZone 分享测试";
    args.description = "这个是cordova QZone 分享测试";
    var imgs =['https://www.baidu.com/img/bdlogo.png',
      'https://www.baidu.com/img/bdlogo.png',
      'https://www.baidu.com/img/bdlogo.png'];
    args.imageUrl = imgs;
    YCQQ.shareToQzone(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  };
  this.logout = function () {
    YCQQ.logout(function () {
      alert('logout success');
    }, function (failReason) {
      alert(failReason);
    });
  };
  this.addToFavirtes = function(){
    var args = {};
    args.url = "http://www.baidu.com";
    args.title = "这个是cordova QQ 收藏测试";
    args.description = "这个是cordova QQ 收藏测试";
    args.imageUrl = "https://www.baidu.com/img/bdlogo.png";
    args.appName = "cordova—QQ";
    YCQQ.addToQQFavorites(function () {
      alert("share success");
    }, function (failReason) {
      alert(failReason);
    }, args);
  }
}
