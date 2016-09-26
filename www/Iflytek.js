var exec = require('cordova/exec');

exports.VoiceRecognitionStart = function (arg0, success, error) {
    exec(success, error, "Iflytek", "VoiceRecognitionStart", [arg0]);
};
exports.VoiceRecognitionStop = function (arg0, success, error) {
    exec(success, error, "Iflytek", "VoiceRecognitionStop", [arg0]);
};
