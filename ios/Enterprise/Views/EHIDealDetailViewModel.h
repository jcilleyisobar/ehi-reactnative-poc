//
//  EHIDealDetailViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIDealDetailSection) {
    EHIDealDetailSectionImage,
    EHIDealDetailSectionDescription,
    EHIDealDetailSectionTerms
};

@class EHIDealCardViewModel;
@class EHIDealContentViewModel;
@class EHISectionHeaderModel;
@interface EHIDealDetailViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (copy  , nonatomic, readonly) NSString *longDescription;
@property (copy  , nonatomic, readonly) NSAttributedString *bookTitle;

@property (strong, nonatomic, readonly) EHIDealCardViewModel *dealModel;
@property (strong, nonatomic, readonly) EHIDealContentViewModel *descriptionModel;
@property (strong, nonatomic, readonly) EHIDealContentViewModel *termsModel;

- (EHISectionHeaderModel *)headerForSection:(EHIDealDetailSection)section;

- (void)bookNow;

@end
