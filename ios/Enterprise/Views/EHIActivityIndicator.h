//
//  EHIActivityIndicator.h
//  Enterprise
//
//  Created by mplace on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHIActivityIndicatorType) {
    EHIActivityIndicatorTypeGreen,
    EHIActivityIndicatorTypeSmallWhite,
    EHIActivityIndicatorTypeELoader,
};

@interface EHIActivityIndicator : UIView

@property (assign, nonatomic) EHIActivityIndicatorType type;
@property (assign, nonatomic) BOOL isAnimating;
@property (assign, nonatomic) BOOL hidesWhenStopped;

- (instancetype)initWithFrame:(CGRect)frame type:(EHIActivityIndicatorType)type;
- (void)setType:(EHIActivityIndicatorType)type size:(CGSize)size;

- (void)startAnimating;
- (void)stopAnimating;

@end
