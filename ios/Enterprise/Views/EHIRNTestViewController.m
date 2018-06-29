//
//  RNtestViewController.m
//  Enterprise
//
//  Created by Jeff Cilley on 6/26/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRNTestViewController.h"
#import <React/RCTRootView.h>
#import "EHIRNManager.h"

@interface EHIRNTestViewController ()

@end

@implementation EHIRNTestViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Do any additional setup after loading the view.
    RCTRootView *rctView = [[[EHIRNManager sharedInstance] factory] createDealsListViewWithWeekendSpecialID:nil];
//    [[EHIRNManager sharedInstance] bindViewConstraints:rctView toSuperView:self.view];
    self.view = rctView;
//    [self.view addSubview:rctView];

    self.title = @"Foo Bar";
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenRNTest;
}

@end
