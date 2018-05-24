//
//  EHIPlacardCell.m
//  Enterprise
//
//  Created by Alex Koller on 8/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPlacardCell.h"
#import "EHIArrowBorderLayer.h"
#import "EHIRestorableConstraint.h"

@interface EHIPlacardCell ()
@property (strong, nonatomic) EHIPlacardViewModel *viewModel;
@property (strong, nonatomic) EHIArrowBorderLayer *borderLayer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *infoIconWidthConstraint;
@end

@implementation EHIPlacardCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPlacardViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.borderLayer.zPosition = 1;
    self.borderLayer.side = EHIArrowBorderLayerSideBottom;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPlacardViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateBackgroundColor:)];
    
    model.bind.map(@{
        source(model.title)         : dest(self, .titleLabel.attributedText),
        source(model.hidesInfoIcon) : dest(self, .infoIconWidthConstraint.isDisabled)
    });
}

- (void)invalidateBackgroundColor:(MTRComputation *)computation
{
    BOOL isPriceDetails = self.viewModel.isPriceDetails;
    
    if(isPriceDetails) {
        self.borderLayer.fillColor = [UIColor ehi_tanColor].CGColor;
    } else {
        self.borderLayer.fillColor = [UIColor whiteColor].CGColor;
    }
}

# pragma mark - Actions

- (IBAction)didTapInfo:(UIButton *)sender
{
    [self ehi_performAction:@selector(didTapInfo) withSender:nil];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat padding = self.viewModel.isPriceDetails ? EHILightPadding : EHIHeavyPadding;
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetHeight(self.titleLabel.frame) + padding
    };
}

# pragma mark - Layer

- (EHIArrowBorderLayer *)borderLayer
{
    return (EHIArrowBorderLayer *)self.layer;
}

+ (Class)layerClass
{
    return [EHIArrowBorderLayer class];
}

@end
