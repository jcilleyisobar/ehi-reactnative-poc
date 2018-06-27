//
//  EHIRequiredInfoCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 02/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRequiredInfoCell.h"
#import "EHIRequiredInfoView.h"
#import "EHIRequiredInfoViewModel.h"

@interface EHIRequiredInfoCell ()
@property (strong, nonatomic) EHIRequiredInfoViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet EHIRequiredInfoView *requiredView;
@end

@implementation EHIRequiredInfoCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRequiredInfoViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(EHIRequiredInfoViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    [self.requiredView updateWithModel:model metrics:metrics];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.requiredView.frame)
    };
}

@end
