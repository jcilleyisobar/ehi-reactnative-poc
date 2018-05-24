//
//  EHISectionHeaderModel.h
//  Enterprise
//
//  Created by mplace on 2/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_OPTIONS(NSUInteger, EHISectionHeaderDividerStyle) {
    EHISectionHeaderDividerStyleDefault,
    EHISectionHeaderDividerStyleFancy
};

typedef NS_OPTIONS(NSUInteger, EHISectionHeaderStyle) {
    EHISectionHeaderStyleDefault  = 0,
    EHISectionHeaderStyleImage    = 1 << 0,
    EHISectionHeaderStyleAction   = 1 << 1,
    EHISectionHeaderStyleDivider  = 1 << 2,
    EHISectionHeaderStyleWrapText = 1 << 3,
};

@interface EHISectionHeaderModel : EHIModel

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSAttributedString *attributedTitle;
@property (copy  , nonatomic) NSString *iconName;
@property (copy  , nonatomic) UIColor *backgroundColor;
@property (copy  , nonatomic) NSString *actionButtonTitle;
@property (assign, nonatomic) EHISectionHeaderStyle style;
@property (assign, nonatomic) EHISectionHeaderDividerStyle dividerStyle;

/** Returns a new model with the given title */
+ (instancetype)modelWithTitle:(NSString *)title;
/** Returns a new model with the given attributed title */
+ (instancetype)modelWithAttributedTitle:(NSAttributedString *)title;
/**
 @brief Returns an index-mapped dictionary of models; 

 If any of the titles in the @c titles parameter are of @c NSNull, they will be
 ommitted from the result.
*/

+ (NSDictionary *)modelsWithTitles:(NSArray *)titles;

@end
