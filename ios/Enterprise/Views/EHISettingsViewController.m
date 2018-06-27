//
//  EHISettingsViewController.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsViewController.h"
#import "EHISettingsViewModel.h"
#import "EHIListCollectionView.h"
#import "EHISectionHeader.h"
#import "EHISettingsControlCell.h"
#import "EHISettingsAboutCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHISettingsViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHISettingsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHISettingsViewController

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISettingsViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHISettingsSectionNotification) : EHISettingsControlCell.class,
        @(EHISettingsSectionSecurity)     : EHISettingsControlCell.class,
        @(EHISettingsSectionPrivacy)      : EHISettingsControlCell.class,
        @(EHISettingsSectionAbout)        : EHISettingsAboutCell.class,
    }];
    
    // configure cells
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = section.index == EHISettingsSectionPrivacy;
        section.header.klass = EHISectionHeader.class;
        section.header.model = [self.viewModel headerForSection:section.index];
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHISettingsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *notifications = self.collectionView.sections[EHISettingsSectionNotification];
    EHIListDataSourceSection *security      = self.collectionView.sections[EHISettingsSectionSecurity];
    EHIListDataSourceSection *privacy       = self.collectionView.sections[EHISettingsSectionPrivacy];
    EHIListDataSourceSection *about         = self.collectionView.sections[EHISettingsSectionAbout];
    
    model.bind.map(@{
        source(model.title)              : dest(self, .title),
        source(model.notificationModels) : dest(notifications, .models),
        source(model.securityModels)     : dest(security, .models),
        source(model.privacyModels)      : dest(privacy, .models),
        source(model.aboutModels)        : dest(about, .models),
    });
}

# pragma mark - EHIListCollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenSettings state:EHIScreenSettings];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSettings;
}

@end

NS_ASSUME_NONNULL_END