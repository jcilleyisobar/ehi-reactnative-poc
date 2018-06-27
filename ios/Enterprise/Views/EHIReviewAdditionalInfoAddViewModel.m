//
//  EHIReviewAdditionalInfoAddViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewAdditionalInfoAddViewModel.h"
#import "EHIContractAdditionalInfo.h"
#import "EHIAdditionalInformationViewModel.h"

@interface EHIReviewAdditionalInfoAddViewModel ()
@property (copy, nonatomic) NSArray *infos;
@end

@implementation EHIReviewAdditionalInfoAddViewModel

- (instancetype)initWithAdditionalInfo:(NSArray *)info
{
    if(self = [super init]) {
        _infos        = info;
        _addInfoTitle = EHILocalizedString(@"additional_info_add_information_button", @"Add Information", @"");
        _details      = [self buildDetails];
    }
    
    return self;
}

- (NSString *)buildDetails
{
    NSString *detail = EHILocalizedString(@"additional_info_add_your_prefix_text", @"Add your", @"");
    
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(detail).space;
    
    NSInteger lastIndex = self.infos.count - 1;
    (self.infos ?: @[]).sortBy(^(EHIContractAdditionalInfo *additionalInfo) {
        return additionalInfo.sequence;
    }).each(^(EHIContractAdditionalInfo *info, int currentIndex){
        NSString *name = info.name;
        if(name) {
            builder.appendText(name);
            
            BOOL isOptional = !info.isRequired;
            if(isOptional) {
                NSString *optionalText = EHILocalizedString(@"additional_info_header_optional", @"Optional", @"");
                builder.space.appendText(optionalText);
            }
            
            BOOL isLast = currentIndex == lastIndex;
            if(!isLast) {
                builder.appendText(@", ");
            } else {
                builder.appendText(@".");
            }
        }
    });
    
    return builder.string.string;
}

@end
