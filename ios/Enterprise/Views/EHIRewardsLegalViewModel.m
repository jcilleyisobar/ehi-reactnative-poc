//
//  EHIRewardsLegalViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/23/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsLegalViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIWebViewModel.h"

@interface EHIRewardsLegalViewModel ()
@property (copy, nonatomic) NSAttributedString *legal;
@end

@implementation EHIRewardsLegalViewModel

- (NSAttributedString *)legal
{
    if(!_legal) {
        NSString *legalText = EHILocalizedString(@"rewards_legal_points_long_info", @"* Points are earned on qualifying dollars at participating locations, but points are not earned on fuel, taxes, surcharges and non qualifying rentals. Membership tier is based on qualifying rentals earned within a program year. See #{terms} for more information.", @"");
        NSString *termsText = EHILocalizedString(@"eplus_terms_and_conditions_navigation_title", @"Enterprise Plus Terms & Conditions", @"");
        
        NSAttributedString *attributedLegalText =
        [NSAttributedString attributedStringWithString:termsText
                                                  font:[UIFont ehi_fontWithStyle:EHIFontStyleBold size:15.0f]
                                                 color:[UIColor ehi_greenColor]
                                            tapHandler:^{
                                                [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypeTermsAndConditions] present];
                                            }];
    
        EHIAttributedStringBuilder *legalBuilder = EHIAttributedStringBuilder.new
        .text(legalText).color([UIColor ehi_grayColor4]).fontStyle(EHIFontStyleRegular, 15.0f).replace(@"#{terms}", attributedLegalText);
        
        legalBuilder.attributes(@{NSBaselineOffsetAttributeName: @1});
        
        _legal = legalBuilder.newline.string;
    }
    
    return _legal;
}

@end
