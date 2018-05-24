// @flow
import React, { Component } from 'react'
import { Animated } from 'react-native'

export class SpringAppearAnimation extends Component<*,*> {
    
    animatedValue = new Animated.Value(0)

    componentDidMount = () => {
        Animated.spring(this.animatedValue, {
            toValue: 1,
            friction: 3,
            useNativeDriver: true
        }).start()
    }

    render = () => {
        const animatedScale = this.animatedValue.interpolate({
            inputRange: [0,1],
            outputRange: [0.25, 1]
        })
        return (
            <Animated.View style={{ opacity: this.animatedValue, transform: [ { scale: animatedScale } ] }}>
                {this.props.children}
            </Animated.View>
        )
    }
}
