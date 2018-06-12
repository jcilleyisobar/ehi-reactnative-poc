//
//  EHIClassDetailsAttributesCell.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassDetailsAttributesCell.h"
#import "EHIClassDetailsAttributesViewModel.h"
#import "EHIClassDetailsTitledInfoView.h"
#import "EHILabel.h"

@interface EHIClassDetailsAttributesCell ()
@property (strong, nonatomic) EHIClassDetailsAttributesViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet EHIClassDetailsTitledInfoView *passengerInfoView;
@property (weak, nonatomic) IBOutlet EHIClassDetailsTitledInfoView *luggageInfoView;
@property (weak, nonatomic) IBOutlet EHILabel *annotationLabel;
@end

@implementation EHIClassDetailsAttributesCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassDetailsAttributesViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassDetailsAttributesViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.passengerInfoModel) : dest(self, .passengerInfoView.model),
        source(model.luggageInfoModel)   : dest(self, .luggageInfoView.model),
        source(model.makeModelDisclaimer): dest(self, .annotationLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat padding = 2 * EHILightPadding;
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = self.containerView.bounds.size.height + padding,
    };
}

@end
