//
//  EHIMenuHeaderView.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIMenuHeaderView.h"

@interface EHIMenuHeaderView ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIMenuHeaderView

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.backgroundColor = [UIColor ehi_mediumGreenColor];
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[NSString class]]) {
        self.titleLabel.text = model;
    }
}

+ (BOOL)isReplaceable
{
    return YES;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.titleLabel.frame) + EHILightPadding
    };
}

@end
