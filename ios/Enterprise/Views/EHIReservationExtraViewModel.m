//
//  EHIReservationExtraViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationExtraViewModel.h"
#import "EHICarClassExtra.h"

@implementation EHIReservationExtraViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClassExtra class]]) {
        [self updateWithExtra:model];
    } else {
        [self prepareEditPlaceholder];
    }
}

- (void)updateWithExtra:(EHICarClassExtra *)extra
{
    self.sectionTitle = extra.shouldShowSectionTitle ? [self sectionTitleForExtras:extra] : @"";
    self.title = [self titleForExtra:extra];
    self.isEditable = extra.isOptional || extra.isWaived;
    self.hasExtras  = YES;
    self.lastInSection = extra.lastInSection;
}

- (void)prepareEditPlaceholder
{
    self.sectionTitle = EHILocalizedString(@"reservation_review_added_extras_section_title", @"ADDED EXTRAS", @"header title for reservations review screen's added extras section");
    NSString *title = EHILocalizedString(@"review_modify_no_extras_title", @"No Extras Added", @"");
    self.title      = EHIAttributedStringBuilder.new.text(title).fontStyle(EHIFontStyleItalic, 17.0f).color([UIColor ehi_grayColor3]).string;
    self.isEditable = YES;
}

//
// Helpers
//

- (NSString *)sectionTitleForExtras:(EHICarClassExtra *)extra
{
    switch (extra.status) {
        case EHICarClassExtraStatusIncluded:
            return EHILocalizedString(@"reservation_review_incl_extras_section_title", @"INCLUDED EXTRAS", @"header title for reservations review screen's included extras section");
            break;
        case EHICarClassExtraStatusMandatory:
            return EHILocalizedString(@"reservation_review_mand_extras_section_title", @"MANDATORY EXTRAS", @"header title for reservations review screen's mandatory extras section");
            break;
        case EHICarClassExtraStatusOptional:
        case EHICarClassExtraStatusWaived:
            return EHILocalizedString(@"reservation_review_added_extras_section_title", @"ADDED EXTRAS", @"header title for reservations review screen's added extras section");
            break;
        default:
            return @"";
            break;
    }
}

- (NSAttributedString *)titleForExtra:(EHICarClassExtra *)extra
{
    if(!extra.name) {
        return nil;
    }
    
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new]
        .text(extra.name).size(17.0);
    
    // append subtitle if needed
    if(extra.selectedQuantity > 1) {
        NSString *quantity = [NSString stringWithFormat:@"(x%@)", @(extra.selectedQuantity).description];
        builder.space
            .appendText(quantity).size(14.0).color([UIColor ehi_grayColor3]);
    }
    
    return builder.string;
}

@end
