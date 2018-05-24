//
//  EHIFormFieldBasicProfileCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldBasicProfileCell.h"
#import "EHIFormFieldBasicProfileViewModel.h"

@interface EHIFormFieldBasicProfileCell ()
@property (strong, nonatomic) EHIFormFieldBasicProfileViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIImageView *acessoryIcon;
@end

@implementation EHIFormFieldBasicProfileCell

- (void)registerReactions:(EHIFormFieldBasicProfileViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.hideAcessoryIcon) : dest(self, .acessoryIcon.hidden),
    });
}

@end
