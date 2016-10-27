//
//  ISRConfigViewController.m
//  MSCDemo_UI
//
//  Created by wangdan on 15-4-25.
//  Copyright (c) 2015年 iflytek. All rights reserved.
//

#import "IATConfigVIewController.h"
#import "IATConfig.h"


@interface IATConfigVIewController ()
@end

@implementation IATConfigVIewController


#pragma mark - 视图生命周期
- (void)viewDidLoad {
    [super viewDidLoad];
    [self initView];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


/**
 用于storyboard scrollview 可以滑动
 ****/
-(void)viewDidLayoutSubviews
{
    CGSize size = [UIScreen mainScreen].bounds.size;
    _backScrollView.contentSize = CGSizeMake(size.width ,size.height+300);
}


#pragma mark -  界面UI处理
-(void)initView
{
    
    self.view.backgroundColor = [UIColor colorWithRed:26.0/255.0 green:26.0/255.0 blue:26.0/255.0 alpha:1.0];
}


- (IBAction)accentSegHandler:(id)sender {

    
    UISegmentedControl *control = sender;
    if (control.selectedSegmentIndex == 0) {//粤语
        
        [IATConfig sharedInstance].language = [IFlySpeechConstant LANGUAGE_CHINESE];
        [IATConfig sharedInstance].accent = [IFlySpeechConstant ACCENT_MANDARIN];
        
    }else if (control.selectedSegmentIndex == 1) {//英语
        [IATConfig sharedInstance].language = [IFlySpeechConstant LANGUAGE_ENGLISH];
        
    }else if (control.selectedSegmentIndex == 2) {//河南话
        [IATConfig sharedInstance].language = [IFlySpeechConstant LANGUAGE_CHINESE];
        [IATConfig sharedInstance].accent = [IFlySpeechConstant ACCENT_HENANESE];
        
    }else if (control.selectedSegmentIndex == 3) {//粤语
        
        [IATConfig sharedInstance].language = [IFlySpeechConstant LANGUAGE_CHINESE];
        [IATConfig sharedInstance].accent = [IFlySpeechConstant ACCENT_CANTONESE];
        
    }
}

- (IBAction)viewSegHandler:(id)sender {
    UISegmentedControl *control = sender;
    if (control.selectedSegmentIndex == 0) {
        [IATConfig sharedInstance].haveView = NO;
        
    }else if (control.selectedSegmentIndex == 1) {
        [IATConfig sharedInstance].haveView = YES;
        
    }
}

- (IBAction)sampleSegHandler:(id)sender {
    
    UISegmentedControl *control = sender;
    if (control.selectedSegmentIndex == 0) {
        [IATConfig sharedInstance].sampleRate = [IFlySpeechConstant SAMPLE_RATE_16K];
        
    }else if (control.selectedSegmentIndex == 1) {
        [IATConfig sharedInstance].sampleRate = [IFlySpeechConstant SAMPLE_RATE_8K];
        
    }
}


- (IBAction)dotSegHandler:(id)sender {
    UISegmentedControl *control = sender;
    
    if (control.selectedSegmentIndex == 0) {
        [IATConfig sharedInstance].dot = [IFlySpeechConstant ASR_PTT_HAVEDOT];
        
    }else if (control.selectedSegmentIndex == 1) {
        [IATConfig sharedInstance].dot = [IFlySpeechConstant ASR_PTT_NODOT];
    }
}



@end
