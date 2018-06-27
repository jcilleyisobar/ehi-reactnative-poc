//
//  EHIWebViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIWebContent.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIWebViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *htmlString;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL hideModalNavigationBar;

/** Instantiates the web view model with the specified type */
- (instancetype)initWithType:(EHIWebContentType)type;
/** Instantiates the web view model with the specified type and HTML string */
- (instancetype)initWithType:(EHIWebContentType)type htmlString:(NSString *)htmlString;
/** Pushes the web view modal onto the current navigation stack */
- (void)push;
/** Presents the web view modal onto the current router */
- (void)present;
- (void)dismiss;

/** Anchor links are broken on iOS 11, so we'll generate a JS code to scroll to it **/
- (BOOL)isAnchorLink:(NSString *)link;
- (NSString *)javascriptScrollCommandForLink:(NSString *)link;
- (BOOL)canHandleAnchorLinks;

@end

NS_ASSUME_NONNULL_END
