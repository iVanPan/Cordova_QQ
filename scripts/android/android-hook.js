var fs = require('fs');

module.exports = function (context) {
    var options = context.opts;
    var androidLibDir = options.projectRoot + '/platforms/android/libs/';
    var android_support_V4_file = 'android-support-v4.jar';  
    var V4JarFileTarget = androidLibDir + android_support_V4_file;
    console.log(V4JarFileTarget);
    if (context.hook == 'before_plugin_install') {
        if (fs.existsSync(V4JarFileTarget)) {
            try{
                fs.unlinkSync(V4JarFileTarget);
            }catch(e){
               console.error('Error caught by catch block:', e);
           }
        }
    }
};
