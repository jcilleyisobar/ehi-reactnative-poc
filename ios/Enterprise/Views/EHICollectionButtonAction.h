//
//  EHICollectionButtonAction.h
//  Enterprise
//
//  Created by Ty Cobb on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHICollectionButtonAction : NSObject
/** The title for the button */
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *iconName;
/** The attributed title for the button (will fall back to title if nil) */
@property (copy, nonatomic) NSAttributedString *attributedTitle;
/** Alignment for the title (defaults to center) */
@property (assign, nonatomic) UIControlContentHorizontalAlignment alignment;
/** A block to call when the button is tapped */
@property (copy, nonatomic) void(^block)(UIButton *);
@end
