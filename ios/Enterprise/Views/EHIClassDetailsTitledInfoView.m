//
//  EHIClassDetailsTitledInfoView.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassDetailsTitledInfoView.h"
#import "EHILabel.h"

@interface EHIClassDetailsTitledInfoView ()
@property (weak, nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet EHILabel *infoLabel;
@end

@implementation EHIClassDetailsTitledInfoView

- (void)setModel:(EHIClassDetailsTitledInfoModel *)model
{
    if(_model == model) {
        return;
    }
    
    _model = model;
    
    self.titleLabel.text = model.title;
    self.iconImageView.ehi_imageName = model.imageName;
    self.infoLabel.text = model.info;
}

@end
