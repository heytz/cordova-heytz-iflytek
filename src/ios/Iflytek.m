/********* Iflytek.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "IATViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "Definition.h"
#import "ISRDataHelper.h"
#import "IATConfigViewController.h"
#import "IATConfig.h"

@interface Iflytek : CDVPlugin<IFlySpeechRecognizerDelegate> {
    IFlySpeechRecognizer *iFlySpeechRecognizer;
    CDVPluginResult* pluginResult;
    NSString * resultFromJson;
    CDVInvokedUrlCommand * commandHolder;
}

- (void)VoiceRecognitionStart:(CDVInvokedUrlCommand*)command;
@end

@implementation Iflytek
-(void)pluginInitialize{
    NSString *initString = [[NSString alloc] initWithFormat:@"appid=%@",@"54dd7faf"];
    [IFlySpeechUtility createUtility:initString];
    
}
- (void)VoiceRecognitionStart:(CDVInvokedUrlCommand*)command
{
    //todo 需要添加权限的判断
    iFlySpeechRecognizer = [IFlySpeechRecognizer sharedInstance];
    [iFlySpeechRecognizer setParameter: @"iat" forKey: [IFlySpeechConstant IFLY_DOMAIN]];
     iFlySpeechRecognizer.delegate = self;
    if (iFlySpeechRecognizer != nil) {
        IATConfig *instance = [IATConfig sharedInstance];
        
        //设置最长录音时间
        [iFlySpeechRecognizer setParameter:instance.speechTimeout forKey:[IFlySpeechConstant SPEECH_TIMEOUT]];
        //设置后端点
        [iFlySpeechRecognizer setParameter:instance.vadEos forKey:[IFlySpeechConstant VAD_EOS]];
        //设置前端点
        [iFlySpeechRecognizer setParameter:instance.vadBos forKey:[IFlySpeechConstant VAD_BOS]];
        //网络等待时间
        [iFlySpeechRecognizer setParameter:@"20000" forKey:[IFlySpeechConstant NET_TIMEOUT]];
        
        //设置采样率，推荐使用16K
        [iFlySpeechRecognizer setParameter:instance.sampleRate forKey:[IFlySpeechConstant SAMPLE_RATE]];
        
        if ([instance.language isEqualToString:[IATConfig chinese]]) {
            //设置语言
            [iFlySpeechRecognizer setParameter:instance.language forKey:[IFlySpeechConstant LANGUAGE]];
            //设置方言
            [iFlySpeechRecognizer setParameter:instance.accent forKey:[IFlySpeechConstant ACCENT]];
        }else if ([instance.language isEqualToString:[IATConfig english]]) {
            [iFlySpeechRecognizer setParameter:instance.language forKey:[IFlySpeechConstant LANGUAGE]];
        }
        //设置是否返回标点符号
        [iFlySpeechRecognizer setParameter:instance.dot forKey:[IFlySpeechConstant ASR_PTT]];
        
    }
    [iFlySpeechRecognizer setParameter:@"asrview.pcm" forKey:[IFlySpeechConstant ASR_AUDIO_PATH]];
    [iFlySpeechRecognizer startListening];
    commandHolder = command;

}
- (void) onResults:(NSArray *) results isLast:(BOOL)isLast
{
    NSMutableString *resultString = [[NSMutableString alloc] init];
    NSDictionary *dic = results[0];
    for (NSString *key in dic) {
        [resultString appendFormat:@"%@",key];
    }
    NSString *_result =[NSString stringWithFormat:@"%@",resultString];
    NSLog(@"_result=%@",_result);
    resultFromJson =[NSString stringWithFormat:@"%@%@",resultFromJson,[ISRDataHelper stringFromJson:resultString]];
    NSLog(@"_result=%@",resultFromJson);
    if (resultFromJson != nil && isLast==true) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:resultFromJson];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:commandHolder.callbackId];
    }

}
- (void)onError: (IFlySpeechError *) error
{
    if (error.errorCode == 0 ) {
        if (resultFromJson.length == 0) {
            NSLog(@"_result=%@",@"no result");
        }else {
            NSLog(@"_result=%@",resultFromJson);
        }
    }
}
- (void) onEndOfSpeech
{
}
- (void) onBeginOfSpeech
{
}
- (void) onVolumeChanged: (int)volume
{
}
@end
