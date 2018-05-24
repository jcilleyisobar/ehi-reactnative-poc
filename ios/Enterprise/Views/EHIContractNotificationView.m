//
//  EHIContractNotificationBannerCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIContractNotificationView.h"
#import "EHIContractNotificationViewModel.h"

@interface EHIContractNotificationView ()
@property (strong, nonatomic) EHIContractNotificationViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIContractNotificationView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIContractNotificationViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIContractNotificationViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)           : dest(self, .titleLabel.text),
        source(model.backgroundColor) : dest(self, .backgroundColor)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
