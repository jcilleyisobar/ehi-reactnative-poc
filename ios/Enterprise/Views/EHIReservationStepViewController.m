//
//  EHIReservationFlowStepViewController.m
//  Enterprise
//
//  Created by mplace on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewController.h"
#import "EHIBarButtonItem.h"

@implementation EHIReservationStepViewController

- (void)viewWillAppear:(BOOL)animated
{
    // tell the builder it's ready to receive reactions
    [self.builder becomeReady];

    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
   
    // tell the builder to stop allowing reactions
    [self.builder resignReady];
}

# pragma mark - EHIViewControlller

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
  
    // remove the menu button supplied by our superclass
    if(self.navigationController.viewControllers.count == 1) {
        item.leftBarButtonItems = nil;
    }

    // add cancel button to the right side
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeDiscard target:self action:@selector(didTapCancelButton:)];
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Interface Actions

- (void)didTapCancelButton:(id)sender
{
    [self.builder cancelReservation];
}

# pragma mark - Accessors

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    // update the analytics context of the buidler
    [self.builder updateAnalyticsContext:context];

    [super updateAnalyticsContext:context];
}

@end
