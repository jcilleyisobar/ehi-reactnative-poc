//
//  EHISettingsCellViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 1/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISettingsControlViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIInfoModalViewModel.h"
#import "EHISettings.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHISettingsControlViewModel ()
@property (assign, nonatomic) BOOL toggleEnabled;
@end

@implementation EHISettingsControlViewModel

- (void)dealloc
{
    [self removeSettingsObserverIfNeeded];
}

# pragma mark - Setters

- (void)setSettingsKey:(nullable NSString *)settingsKey
{
    [self removeSettingsObserverIfNeeded];
    
    _settingsKey = settingsKey;
    
    if(_settingsKey) {
        [[EHISettings sharedInstance] addObserver:self forKeyPath:_settingsKey options:NSKeyValueObservingOptionInitial|NSKeyValueObservingOptionNew context:nil];
    }
}

//
// Helpers
//

- (void)removeSettingsObserverIfNeeded
{
    if(_settingsKey) {
        [[EHISettings sharedInstance] removeObserver:self forKeyPath:_settingsKey];
    }
}

# pragma mark - Key-Value Observing

- (void)observeValueForKeyPath:(nullable NSString *)keyPath ofObject:(nullable id)object change:(nullable NSDictionary<NSString *,id> *)change context:(nullable void *)context
{
    if([keyPath isEqualToString:self.settingsKey]) {
        self.toggleEnabled = [change[NSKeyValueChangeNewKey] boolValue];
    }
}

# pragma mark - Computed

- (nullable NSAttributedString *)detailsAttributed
{
    if(!self.details) {
        return nil;
    }
    
    return EHIAttributedStringBuilder.new.size(14).lineSpacing(6).text(self.details).string;
}

- (BOOL)hidesDetails
{
    return (self.details.length == 0) || !self.hidesDetailIcon;
}

- (BOOL)hidesArrow
{
    return !self.hidesToggle || self.isAction;
}

- (BOOL)hidesToggle
{
    return self.settingsKey == nil;
}

# pragma mark - Actions

- (void)showDetailsModal
{
    // don't show modal if no details supplied
    if(!self.details) {
        return;
    }
    
    EHIInfoModalViewModel *viewModel = [EHIInfoModalViewModel new];
    viewModel.title = self.detailsTitle ?: self.title;
    viewModel.details = self.details;
    
    [viewModel present:nil];
}

- (void)enableToggle:(BOOL)enabled
{
    // allow subclass to respond to toggle before changing setting
    if(self.toggleModifer) {
        enabled = self.toggleModifer(enabled);
    }
    
    self.toggleEnabled = enabled;
    
    // update the settings
    [[EHISettings sharedInstance] setValue:@(enabled) forKeyPath:self.settingsKey];
    
    [self trackToggle];
}

# pragma mark - Analytics

- (void)trackToggle
{
    NSString *toggleKey = self.toggleKeys[self.settingsKey];
    if(!toggleKey) {
        return;
    }
    
    BOOL enabled     = self.toggleEnabled;
    NSString *state  = [self actionForState:enabled];
    NSString *action = [toggleKey ehi_appendComponent:state];
    
    [EHIAnalytics trackAction:action handler:nil];
}

- (NSDictionary *)toggleKeys
{
    return @{
        NSStringFromProperty(autoSaveUserInfo)                          : EHIAnalyticsSettingsActionAutoSave,
        NSStringFromProperty(allowDataCollection)                       : EHIAnalyticsSettingsActionDataCollection,
        NSStringFromProperty(saveSearchHistory)                         : EHIAnalyticsSettingsActionSaveSearchHistory,
        NSStringFromProperty(selectPreferredPaymentMethodAutomatically)  : EHIAnalyticsSettingsActionPreferredCreditCard,
    };
}

- (NSString *)actionForState:(BOOL)state
{
    return state ? @"ON" : @"OFF";
}

- (void)setToggleEnabled:(BOOL)toggleEnabled
{
    _toggleEnabled = toggleEnabled;
}

# pragma mark - Subclassing Hooks

+ (NSArray *)viewModels
{
    return @[];
}

@end

NS_ASSUME_NONNULL_END
