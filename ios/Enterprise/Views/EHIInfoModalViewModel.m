//
//  EHIInfoModalViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 5/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIInfoModalViewModel.h"
#import "EHICarClassPriceLineItem.h"
#import "EHICarClassExtra.h"
#import "EHILocationPolicy.h"


@interface EHIInfoModalViewModel ()
@property (copy  , nonatomic) EHIInfoModalAction action;
@property (weak  , nonatomic) id<EHIInfoModalModelable> model;
@end

@implementation EHIInfoModalViewModel

- (instancetype)initWithModel:(id<EHIInfoModalModelable>)model
{
    if(self = [super initWithModel:model]) {
        _secondButtonTitle = EHILocalizedString(@"modal_default_dismiss_title", @"GOT IT", @"Default title for modal dismiss button");
        _buttonLayout = EHIInfoModalButtonLayoutRegular;
    }
    
    return self;
}

# pragma mark - Built-In Updaters

- (void)updateWithModel:(id<EHIInfoModalModelable>)model
{
    [super updateWithModel:model];
  
    if([model conformsToProtocol:@protocol(EHIInfoModalModelable)]) {
        // store the model
        self.model = model;
        
        // bind required properties
        self.title   = model.infoTitle;
        self.details = model.infoDetails;
    }
}

# pragma mark - Accessors

- (BOOL)hidesActionButton
{
    return self.action == nil;
}

# pragma mark - Presentation / Dismissal

- (void)present:(EHIInfoModalAction)action
{
    // store the action if it exists
    self.action = action;

    // create modal transition with ourself as the parameter
    self.router.transition
        .present(EHIScreenInfoModal).object(self).start(nil);
}

- (void)dismissWithCompletion:(void (^)(void))completion
{
    self.router.transition
        .dismiss.start(completion);
}

# pragma mark - Actions

- (void)performActionForIndex:(NSInteger)index
{
    [self completeWithIndex:index canceled:NO];
}

- (void)cancel
{
    [self completeWithIndex:EHInfoModalIndexNone canceled:YES];
}

//
// Helpers
//

- (void)completeWithIndex:(NSInteger)index canceled:(BOOL)canceled
{
    // if we don't have an action, then we must dismiss
    BOOL shouldDismiss = !self.action;
  
    // attempt to call our action and then clean it up to avoid retain cycles
    if(self.action) {
        shouldDismiss = self.action(index, canceled);
        self.action   = nil;
    }

    // dismiss the modal unless our action requested we don't
    if(shouldDismiss) {
        [self dismissWithCompletion:nil];
    }
}

@end
