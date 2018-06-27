//
//  EHIProfileFooterCell.m
//  Enterprise
//
//  Created by fhu on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileFooterCell.h"
#import "EHIButton.h"
#import "EHIProfileFooterViewModel.h"

@interface EHIProfileFooterCell ()
@property (strong, nonatomic) EHIProfileFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *changePasswordButton;
@end

@implementation EHIProfileFooterCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfileFooterViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.changePasswordButton.ehi_title = self.viewModel.changePasswordTitle;
}

#pragma mark - IB Actions

- (IBAction)didSelectChangePassword:(id)sender
{
    [self.viewModel changePassword];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 95.0f };
    return metrics;
}

@end
