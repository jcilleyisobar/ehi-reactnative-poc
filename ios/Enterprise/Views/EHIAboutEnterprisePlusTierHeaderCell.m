//
//  EHIAboutEnterprisePlusTierHeaderCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusTierHeaderCell.h"
#import "EHIAboutEnterprisePlusTierHeaderViewModel.h"

@interface EHIAboutEnterprisePlusTierHeaderCell ()
@property (strong, nonatomic) EHIAboutEnterprisePlusTierHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *firstLineLabel;
@property (weak  , nonatomic) IBOutlet UILabel *secondLineLabel;
@property (weak  , nonatomic) IBOutlet UIView *detailContainerView;
@end

@implementation EHIAboutEnterprisePlusTierHeaderCell

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutEnterprisePlusTierHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)      : dest(self, .titleLabel.text),
        source(model.firstLine)  : dest(self, .firstLineLabel.text),
        source(model.secondLine) : dest(self, .secondLineLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailContainerView.frame) + EHILightPadding
    };
}

@end
