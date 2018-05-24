//
//  EHIGaugeTierView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIGaugeTierView.h"
#import "EHIGaugeTierViewModel.h"
#import "EHIGaugeView.h"

@interface EHIGaugeTierView ()
@property (strong, nonatomic) EHIGaugeTierViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *amountLabel;
@property (weak  , nonatomic) IBOutlet UILabel *unitLabel;
@property (weak  , nonatomic) IBOutlet UILabel *totalLabel;

@property (weak  , nonatomic) IBOutlet EHIGaugeView *gaugeView;
@end

@implementation EHIGaugeTierView

# pragma mark - Reactions

- (void)registerReactions:(EHIGaugeTierViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.currentAmount) : dest(self, .amountLabel.attributedText),
        source(model.unitTitle)     : dest(self, .unitLabel.text),
        source(model.total)         : dest(self, .totalLabel.text),
        source(model.gaugeModel)    : dest(self, .gaugeView.viewModel)
    });
}

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.unitLabel.frame)
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
