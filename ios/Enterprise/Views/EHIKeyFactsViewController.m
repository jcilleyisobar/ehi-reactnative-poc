//
//  EHIKeyFactsViewController.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsViewController.h"
#import "EHIListCollectionView.h"
#import "EHIKeyFactsViewModel.h"
#import "EHIKeyFactsHeaderCell.h"
#import "EHIKeyFactsSectionContentCell.h"
#import "EHIKeyFactsFooterCell.h"

@interface EHIKeyFactsViewController () <EHIListCollectionViewDelegate, EHIKeyFactsSectionContentActions>
@property (strong, nonatomic) EHIKeyFactsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIKeyFactsViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIKeyFactsViewModel new];
    }
    
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    EHIListDataSourceSection *header = self.collectionView.sections[EHIKeyFactsSectionHeader];
    header.klass = EHIKeyFactsHeaderCell.class;
    header.model = [EHIModel placeholder];
    
    EHIListDataSourceSection *content = self.collectionView.sections[EHIKeyFactsSectionContent];
    content.klass = EHIKeyFactsSectionContentCell.class;
    
    EHIListDataSourceSection *footer = self.collectionView.sections[EHIKeyFactsSectionFooter];
    footer.klass = EHIKeyFactsFooterCell.class;

    self.collectionView.sections.isDynamicallySized = YES;
}

# pragma mark - EHIKeyFactsSectionContentActions

- (void)didTapSectionContentHeader:(EHIKeyFactsSectionContentCell *)sender
{
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIKeyFactsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *content = self.collectionView.sections[EHIKeyFactsSectionContent];
    EHIListDataSourceSection *footer  = self.collectionView.sections[EHIKeyFactsSectionFooter];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .title),
        source(model.contentList) : dest(content, .models),
        source(model.reservation) : dest(footer, .model)
    });
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenKeyFacts;
}

@end
