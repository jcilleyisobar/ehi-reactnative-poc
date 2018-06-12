//
//  EHIItineraryWarningView.m
//  Enterprise
//
//  Created by fhu on 6/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryWarningView.h"

@interface EHIItineraryWarningView()
@property (weak, nonatomic) IBOutlet UILabel *warningLabel;
@end

@implementation EHIItineraryWarningView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.warningLabel.text = EHILocalizedString(@"info_modal_mismatch_account_title", @"We're sorry, the code you entered is not attached to your profile", @"");
    }
    
    return self;
}

@end
