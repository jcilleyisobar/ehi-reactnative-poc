//
//  EHILocationsDriveAllianceView.m
//  Enterprise
//
//  Created by Alex Koller on 6/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsDriveAllianceView.h"

@interface EHILocationsDriveAllianceView ()
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
@end

@implementation EHILocationsDriveAllianceView

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.layer.borderWidth = 1.0f;
    self.layer.borderColor = [UIColor ehi_grayColor2].CGColor;
    
    self.detailLabel.text = EHILocalizedString(@"info_modal_drive_alliance_details", @"However, we found locations near here serviced through our Drive Alliance® partners.", @"");
}

@end
