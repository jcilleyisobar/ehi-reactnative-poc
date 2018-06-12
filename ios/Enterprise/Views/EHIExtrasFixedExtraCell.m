//
//  EHIReservationExtrasMandatoryItemCell.m
//  Enterprise
//
//  Created by fhu on 4/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIExtrasFixedExtraCell.h"
#import "EHIExtrasExtraViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIExtrasFixedExtraCell ()
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *priceLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *priceLeading;
@property (strong, nonatomic) EHIExtrasExtraViewModel *viewModel;
@end

@implementation EHIExtrasFixedExtraCell

# pragma mark - Reactions

- (void)registerReactions:(EHIExtrasExtraViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateBackgroundColor:)];
    model.bind.map(@{
        source(model.title)     : dest(self, .titleLabel.text),
        source(model.totalText) : ^(NSString *text) {
            self.priceLabel.text = text;
            self.priceLeading.isDisabled = text == nil;
        }
    });
}

- (void)invalidateBackgroundColor:(MTRComputation *)computation
{
    BOOL isIncludedExtras = self.viewModel.extra.status == EHICarClassExtraStatusIncluded;
    if(isIncludedExtras) {
        self.contentView.backgroundColor = [UIColor ehi_graySpecialColor];
    } else {
        self.contentView.backgroundColor = [UIColor whiteColor];
    }
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = 50.0f,
    };
}

@end
