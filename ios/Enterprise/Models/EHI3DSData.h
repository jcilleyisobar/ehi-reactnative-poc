//
//  EHI3DSData.h
//  Enterprise
//
//  Created by cgross on 1/25/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"

#define EHI3dsTermUrl       @"https://www.enterprise.com/3dsmobile" // has to be https otherwise it fails for some banks
#define EHI3dsPaResKey      @"PaRes"

@interface EHI3DSData : EHIModel <EHINetworkEncodable>

@property (assign, nonatomic, readonly) BOOL isSupported;

// computed
@property (strong, nonatomic, readonly) NSURL *url;
@property (copy  , nonatomic, readonly) NSString *body;

@end
