//
//  EHICalendarPlacardView.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICalendarPlacardView.h"
#import "EHICalendarPlacardViewModel.h"
#import "EHIArrowBorderLayer.h"

@interface EHICalendarPlacardView ()
@property (strong, nonatomic) EHICalendarPlacardViewModel *viewModel;
@property (strong, nonatomic) EHIArrowBorderLayer *borderLayer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHICalendarPlacardView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHICalendarPlacardViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];

    self.backgroundColor = [UIColor ehi_darkGreenColor];
    self.borderLayer.zPosition = 1;
    self.borderLayer.side = EHIArrowBorderLayerSideBottom;
    self.borderLayer.strokeColor = [UIColor ehi_darkGreenColor].CGColor;
    self.borderLayer.fillColor   = [UIColor ehi_darkGreenColor].CGColor;
}

# pragma mark - Reactions

- (void)registerReactions:(EHICalendarPlacardViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
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

+ (BOOL)isReplaceable
{
    return YES;
}

@end
