//
//  ___FILENAME___
//  ___PROJECTNAME___
//
//  Created by ___FULLUSERNAME___ on ___DATE___.
//  ___COPYRIGHT___
//

#import "___FILEBASENAME___.h"
#import "___VARIABLE_productName:identifier___ViewModel.h"


@interface ___FILEBASENAMEASIDENTIFIER___ ()
@property (strong, nonatomic) ___VARIABLE_productName:identifier___ViewModel *viewModel;
@end

@implementation ___FILEBASENAMEASIDENTIFIER___

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [___VARIABLE_productName:identifier___ViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(___VARIABLE_productName:identifier___ViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
    });
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return @"";
}

@end
