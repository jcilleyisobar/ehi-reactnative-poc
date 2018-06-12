//
//  EHINotificationSettingsViewController.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHINotificationSettingsViewController.h"
#import "EHINotificationSettingsViewModel.h"
#import "EHINotificationSettingsOptionCell.h"
#import "EHIListCollectionView.h"

@interface EHINotificationSettingsViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHINotificationSettingsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHINotificationSettingsViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.collectionView.section.klass = EHINotificationSettingsOptionCell.class;
}

# pragma mark - Reactions

- (void)registerReactions:(EHINotificationSettingsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *section = self.collectionView.section;
    
    model.bind.map(@{
        source(model.title)   : dest(self, .title),
        source(model.options) : dest(section, .models),
    });
}

# pragma mark - EHIListCollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectOptionAtIndex:indexPath.item];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSettingsNotifications;
}
@end
