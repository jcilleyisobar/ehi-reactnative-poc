//
//  EHIPriceFormatterTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"

SpecBegin(EHIPriceFormatterTests)

describe(@"the price formatter", ^{
    __block EHIPriceFormatter *formatter;
    __block NSLocale *locale;
    __block EHIPrice *price = [EHIPrice new];

	context(@"on United States as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"en_US"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"CA$10.00");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"$10.00");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"€10.00");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"£10.00");
			});
		});
	});

    context(@"on Canada (English) as COR", ^{
        beforeAll(^{
            locale = [NSLocale localeWithLocaleIdentifier:@"en_CA"];
        });

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"$10.00");
			});
		});

        context(@"USD dollar price", ^{
        	before(^{
        		price = [EHIPrice modelWithDictionary:@{
                	@"amount" : @(10.0),
                	@"code"   : @"USD",
                	@"symbol" : @"$"
            	}];
            	formatter = [EHIPriceFormatter format:price].locale(locale);
        	});
            it(@"should format with USD dollar", ^{
                expect(formatter.string).to.equal(@"US$10.00");
            });
        });

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"€10.00");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"£10.00");
			});
		});
    });

	context(@"on Canada (French) as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"fr_CA"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 $");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 $ US");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"10,00 €");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"10,00 £");
			});
		});
	});

	context(@"on England as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"en_GB"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"CA$10.00");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"US$10.00");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"€10.00");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"£10.00");
			});
		});
	});

	context(@"on United States (Spanish) as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"es_US"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"CA$10.00");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"$10.00");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"€10.00");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"£10.00");
			});
		});
	});

	context(@"on Spain as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"es_ES"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 CA$");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 $");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"10,00 €");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"10,00 GBP");
			});
		});
	});

	context(@"on France as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"fr_FR"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 $CA");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 $US");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"10,00 €");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"10,00 £GB");
			});
		});
	});

	context(@"on German as COR", ^{
		beforeAll(^{
			locale = [NSLocale localeWithLocaleIdentifier:@"de_DE"];
		});

		context(@"CAD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"CAD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with CAD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 CA$");
			});
		});

		context(@"USD dollar price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"USD",
					@"symbol" : @"$"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with USD dollar", ^{
				expect(formatter.string).to.equal(@"10,00 $");
			});
		});

		context(@"EUR price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"EUR",
					@"symbol" : @"€"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with EUR", ^{
				expect(formatter.string).to.equal(@"10,00 €");
			});
		});

		context(@"GBP price", ^{
			before(^{
				price = [EHIPrice modelWithDictionary:@{
					@"amount" : @(10.0),
					@"code"   : @"GBP",
					@"symbol" : @"£"
				}];
				formatter = [EHIPriceFormatter format:price].locale(locale);
			});
			it(@"should format with pound", ^{
				expect(formatter.string).to.equal(@"10,00 £");
			});
		});
	});
});


SpecEnd
