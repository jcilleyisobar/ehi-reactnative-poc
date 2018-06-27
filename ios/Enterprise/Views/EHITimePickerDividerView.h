//
//  EHITimePickerDividerView.h
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHITimePickerDividerType) {
    EHITimePickerDividerTypeSolid,
    EHITimePickerDividerTypeTapered
};

@interface EHITimePickerDividerView : UIView
@property (assign, nonatomic) EHITimePickerDividerType type;
@end
