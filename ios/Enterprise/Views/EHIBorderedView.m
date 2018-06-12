//
//  EHIBorderedView.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIBorderedView.h"

@implementation EHIBorderedView

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.layer.borderColor = [UIColor ehi_greenColor].CGColor;
    self.layer.borderWidth = 1.0f;
}

@end
