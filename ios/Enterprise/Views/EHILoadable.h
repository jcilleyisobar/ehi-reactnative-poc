//
//  EHILoadable.h
//  Enterprise
//
//  Created by Ty Cobb on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHILoadable <NSObject>
/** Pass-through to @c setIsLoading:animated: with @c animated as @c YES */
- (void)setIsLoading:(BOOL)isLoading;
/** Updates the view to display it's loading UI; animates if @c animated is @c YES */
- (void)setIsLoading:(BOOL)isLoading animated:(BOOL)animated;
@end
