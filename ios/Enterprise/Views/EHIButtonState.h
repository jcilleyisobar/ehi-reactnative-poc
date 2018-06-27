//
//  EHIButtonState.h
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButtonType.h"

@interface EHIButtonState : NSObject

@property (copy, nonatomic) NSString *imageName;
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSAttributedString *attributedTitle;
@property (copy, nonatomic) UIColor *backgroundColor;
@property (copy, nonatomic) UIColor *titleColor;
@property (copy, nonatomic) UIColor *tintColor;
@property (copy, nonatomic) UIColor *borderColor;

// computed properties
@property (nonatomic, readonly) BOOL hasCustomProperties;

+ (NSDictionary *)statesForType:(EHIButtonType)type;

@end
