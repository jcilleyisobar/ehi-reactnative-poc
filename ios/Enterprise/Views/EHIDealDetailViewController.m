//
//  EHIDealDetailViewController.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealDetailViewController.h"
#import "EHIDealDetailViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIDealCardCell.h"
#import "EHIDealContentCell.h"
#import "EHISectionHeader.h"
#import "EHIActivityIndicator.h"
#import "EHIButton.h"

@interface EHIDealDetailViewController ()
@property (strong, nonatomic) EHIDealDetailViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet EHIButton *bookButton;
@end

@implementation EHIDealDetailViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDealDetailViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.bookButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    
    [self setupCollectionView];
}

- (void)setupCollectionView
{
    [self.collectionView.sections construct:@{
        @(EHIDealDetailSectionImage)       : EHIDealCardCell.class,
        @(EHIDealDetailSectionDescription) : EHIDealContentCell.class,
        @(EHIDealDetailSectionTerms)       : EHIDealContentCell.class,
     }];

    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.header.klass       = EHISectionHeader.class;
        section.header.model       = [self.viewModel headerForSection:section.index];
        section.isDynamicallySized = section.index != EHIDealDetailSectionImage;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDealDetailViewModel *)model
{
    [super registerReactions:model];

    EHIListDataSourceSection *deal        = self.collectionView.sections[EHIDealDetailSectionImage];
    EHIListDataSourceSection *description = self.collectionView.sections[EHIDealDetailSectionDescription];
    EHIListDataSourceSection *terms       = self.collectionView.sections[EHIDealDetailSectionTerms];
    
    model.bind.map(@{
        source(model.title)            : dest(self, .title),
        source(model.dealModel)        : dest(deal, .model),
        source(model.descriptionModel) : dest(description, .model),
        source(model.termsModel)       : dest(terms, .model),
        source(model.bookTitle)        : dest(self, .bookButton.ehi_attributedTitle),
        source(model.isLoading)        : dest(self, .loadingIndicator.isAnimating),
    });
}

# pragma mark - EHIViewController

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Actions

- (IBAction)didTapBookNow:(UIControl *)sender
{
    [self.viewModel bookNow];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenDealDetails;
}

@end
