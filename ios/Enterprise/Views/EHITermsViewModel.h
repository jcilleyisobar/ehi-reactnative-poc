//
//  EHITermsViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHITermsViewModel : EHIViewModel <MTRReactive>

/** Title for the terms and conditions screen */
@property (copy  , nonatomic, readonly) NSString *title;
/** Title for the terms accept button */
@property (copy  , nonatomic, readonly) NSString *acceptTitle;
/** The HTML content render for the terms and conditions */
@property (copy  , nonatomic, nullable, readonly) NSString *termsContent;
/** @c YES if the terms content is currently being loaded */
@property (assign, nonatomic, readonly) BOOL isLoading;
/** An optional handler that is called back when the terms are accepted/denied */
@property (copy  , nonatomic, nullable) void(^handler)(NSString *acceptedTermsVersion, BOOL accepted);

/** Call when the user completes or rejects the terms */
- (void)acceptTerms:(BOOL)didAccept;

- (instancetype)initWithContentString:(NSString *)content;

@end

NS_ASSUME_NONNULL_END
