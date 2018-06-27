//
//  EHIPolicyInfoViewController.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 26.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPoliciesViewController.h"
#import "EHIPoliciesViewModel.h"
#import "EHIListCollectionView.h"
#import "EHILocationDetailsPolicyCell.h"

@interface EHIPoliciesViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIPoliciesViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIPoliciesViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIPoliciesViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // configure the policies section
    self.collectionView.section.klass = EHILocationDetailsPolicyCell.class;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPoliciesViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *section = self.collectionView.section;
    
    model.bind.map(@{
        source(model.policies) : dest(section, .models),
        source(model.title) : dest(self, .title),
    });
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectPolicyAtIndex:indexPath.item];
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    context.routerState = EHIScreenPolicies;
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenPolicies;
}

@end
