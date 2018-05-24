//
//  EHIPolicyInfoDetailsViewController.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 26.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPolicyDetailViewController.h"
#import "EHIPolicyDetailViewModel.h"

@interface EHIPolicyDetailViewController ()
@property (strong, nonatomic) EHIPolicyDetailViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UITextView *detailsTextView;
@end

@implementation EHIPolicyDetailViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIPolicyDetailViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPolicyDetailViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)     : dest(self, .title),
        source(model.name)      : dest(self, .nameLabel.text),
        source(model.details)   : dest(self, .detailsTextView.text)
    });
}

# pragma mark - NAVViewController

+ (NSString *)storyboardName
{
    return @"EHIPoliciesStoryboard";
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    context.routerState = EHIScreenPolicyDetail;
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context[EHIAnalyticsLocPolicyKey] = self.viewModel.name;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenPolicyDetail;
}

@end
