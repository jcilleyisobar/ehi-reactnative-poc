//
//  UIView+Unarchiving.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIView+Unarchiving.h"

@implementation UIView (Unarchiving)

+ (instancetype)ehi_instanceFromNib
{
    return [self ehi_instanceFromNibWithName:NSStringFromClass(self)];
}

+ (instancetype)ehi_deviceSpecificInstanceFromNib
{
    NSString *nibName = UIDeviceifyName(NSStringFromClass(self));
    return [self ehi_instanceFromNibWithName:nibName];
}

+ (instancetype)ehi_instanceFromNibWithName:(NSString *)name
{
    return [[NSBundle mainBundle] loadNibNamed:name owner:nil options:nil].firstObject;
}

@end

@implementation UIStoryboard (Unarchiving)

+ (instancetype)ehi_deviceSpecificStoryboardWithName:(NSString *)name
{
    name = UIDeviceifyName(name);
    return [UIStoryboard storyboardWithName:name bundle:nil];
}

@end

NSString * UIDeviceifyName(NSString *name) {
    return UIDeviceifyNameWithIdiom(name, UI_USER_INTERFACE_IDIOM());
}

NSString * UIDeviceifyNameWithIdiom(NSString *name, UIUserInterfaceIdiom idiom) {
    return idiom == UIUserInterfaceIdiomPad ? [name stringByAppendingString:@"-ipad"] : name;
}
