//
//  EHITemporalSelectionViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 03/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHITemporalSelectionLayout) {
    EHITemporalSelectionLayoutMap,
    EHITemporalSelectionLayoutFilter
};

typedef NS_ENUM(NSInteger, EHITemporalSelectionType) {
    EHITemporalSelectionTypeDate,
    EHITemporalSelectionTypeTime
};

typedef struct {
    EHITemporalSelectionType type;
    EHITemporalSelectionLayout layout;
} EHITemporalSelectionConfig;

@interface EHITemporalSelectionViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithConfig:(EHITemporalSelectionConfig)config;

@property (copy  , nonatomic, readonly) NSAttributedString *valueString;
@property (assign, nonatomic, readonly) BOOL hideClear;
@property (assign, nonatomic, readonly) EHITemporalSelectionType type;
@property (strong, nonatomic) NSDate *value;
@property (assign, nonatomic) BOOL hasValue;

- (void)didTapClearValue;

@end
