//
//  EHIArchGeometry.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/6/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//
//
//           ________                             __                   ____        ____
//          /  _____/  ____  ____   _____   _____/  |________ ___.__. /_   |__  __/_   |
//         /   \  ____/ __ \/  _ \ /     \_/ __ \   __\_  __ <   |  |  |   \  \/  /|   |
//         \    \_\  \  ___(  <_> )  Y Y  \  ___/|  |  |  | \/\___  |  |   |>    < |   |
//          \______  /\___  >____/|__|_|  /\___  >__|  |__|   / ____|  |___/__/\_ \|___|
//                 \/     \/            \/     \/             \/                 \/
//
//
//
//          The radius parameter is the arc radius (typically the view/layer's width divided by 2)
//          The center is used by the algorithm as the origin of the segments draw
//
//          The points will be calculated by using the segments as steps.
//          On each step the angle is increased, until it reaches the end of the arc (180°)
//          At each step, we do the following:
//
//          1) Calculate the angle relative to the segment step. (1..n)
//
//          2) Calculate the opposite and adjacent legs.               .............F.O.R.M.U.L.A.S..............
//                                                                     |                                        |
//          3) The adjacent point will yield the x coordinate.         |   opposite = sin(angle) * hypotenuse   |
//                                                                     |   adjacent = cos(angle) * hypotenuse   |
//          4) The opposite point will yield the y coordinate.         |                                        |
//                                                                      `'''''''''''''''''''''''''''''''''''''''
//
//                                            _________
//                                   _,.---'''         `''---.._
//                              _.-''                           ``-._
//                          _,-'                                     `-._
//                       ,-'                                             `-.
//    opposite point  _,'                                                   `._
//                 ,(*).                                                       `.
//                ,'  .`.                                                        `.
//               /    |  \                                                         \
//             ,'     |   `.                                                        `.
//            /       |     `.                                                        \
//          ,'     o  |       `.                                                       `.
//         /       p  |         \  hypotenuse                                            \
//        /        p  |          `.                                                       \
//       ,'        o  |            `.                                                     `.
//       /         s  |              `.                                                    |
//      /          i  |                \                                                    \
//     |           t  |                 `.                                                   |
//     |           e  |                   `.                                                 |
//     |              |                     `.                                               |
//    |               |_                angle \                                               |
//    |               |.|     adjacent       / `.                                             |
//    '---------------(*)------------------------`--------------------------------------------'
//              adjacent point
//

@interface EHIArchGeometry : NSObject

- (EHIArchGeometry *(^)(CGPoint))center;
- (EHIArchGeometry *(^)(CGFloat))radius;
- (EHIArchGeometry *(^)(CGFloat))segments;

- (NSArray *)points;

@end
