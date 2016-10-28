var exec = require('cordova/exec');

exports.VoiceRecognitionStart = function (arg0, success, error) {
    exec(success, error, "Iflytek", "VoiceRecognitionStart", [arg0]);
};

exports.VoiceRecognitionStop = function (arg0, success, error) {
    exec(success, error, "Iflytek", "VoiceRecognitionStop", [arg0]);
};

// 唤醒
exports.wakeup = function (success, error, params) {
    exec(success, error, "Iflytek", "startWakeuper", [params]);
};

// 唤醒
exports.wakeup = function (success, error, params) {
    exec(success, error, "Iflytek", "stopWakeuper", [params]);
};
