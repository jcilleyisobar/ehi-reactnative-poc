//
//  EHIRentalsUnauthenticatedViewController.m
//  Enterprise
//
//  Created by fhu on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsUnauthenticatedViewController.h"
#import "EHIRentalsUnauthenticatedViewModel.h"
#import "EHIActionButton.h"

@interface EHIRentalsUnauthenticatedViewController ()
@property (strong, nonatomic) EHIRentalsUnauthenticatedViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *signinHeaderLabel;
@property (weak  , nonatomic) IBOutlet UILabel *signinDetailsLabel;
@property (weak  , nonatomic) IBOutlet EHIActionButton *signinButton;

@property (weak  , nonatomic) IBOutlet UILabel *lookupDetailsLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *lookupButton;
@end

@implementation EHIRentalsUnauthenticatedViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIRentalsUnauthenticatedViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.lookupButton.type = EHIButtonTypeSecondary;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsUnauthenticatedViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.signinHeaderText)  : dest(self, .signinHeaderLabel.text),
        source(model.signinDetailText)  : dest(self, .signinDetailsLabel.text),
        source(model.signinButtonText)  : dest(self, .signinButton.ehi_title),
        source(model.lookupDetailsText) : dest(self, .lookupDetailsLabel.text),
        source(model.lookupButtonText)  : dest(self, .lookupButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didSelectSignIn:(id)sender
{
    [self.viewModel signin];
}

- (IBAction)didSelectLookup:(id)sender
{
    [self.viewModel lookup];
}

@end
