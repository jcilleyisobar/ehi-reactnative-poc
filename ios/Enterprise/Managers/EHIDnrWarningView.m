//
//  EHIDnrWarningView.m
//  Enterprise
//
//  Created by fhu on 6/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDnrWarningView.h"

@interface EHIDnrWarningView ()
@property (weak, nonatomic) IBOutlet UILabel *warningLabel;
@end

@implementation EHIDnrWarningView

- (void)awakeFromNib
{
    [super awakeFromNib];

    self.warningLabel.text = EHILocalizedString(@"my_profile_dnr_text", @"Your account is on our Do Not Rent list. Please call us.", @"");
}

@end
