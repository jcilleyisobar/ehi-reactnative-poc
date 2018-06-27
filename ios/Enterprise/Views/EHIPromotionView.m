//
//  EHIPromotionView.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionView.h"
#import "EHIPromotionViewModel.h"
#import "EHIButton.h"

@interface EHIPromotionView ()
@property (strong, nonatomic) EHIPromotionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *promotionNameLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *getStartedButton;
@end

@implementation EHIPromotionView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionViewModel new];
    }

    return self;
}

# pragma mark - View Lifecycle

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    UIEdgeInsets insets = (UIEdgeInsets) { .left = 20, .right = 20, .top = 3 };
    self.getStartedButton.contentEdgeInsets        = insets;
    self.getStartedButton.titleLabel.numberOfLines = 1;
    self.getStartedButton.titleLabel.textAlignment = NSTextAlignmentCenter;
}

# pragma mark - Actions

- (IBAction)didTapGetStarted:(id)sender
{
    [self ehi_performAction:@selector(didTapPromotionGetStarted) withSender:self];
}

# pragma mark - Reactions

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    [self.viewModel updateWithModel:model];
}

- (void)registerReactions:(EHIPromotionViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.promotionName)        : dest(self, .promotionNameLabel.text),
        source(model.promotionButtonTitle) : dest(self, .getStartedButton.ehi_title),
    });
}

@end
