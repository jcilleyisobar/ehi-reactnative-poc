// @flow

import React, { Component } from "react"
import { StyleSheet, Platform, View, Image, ActivityIndicator } from "react-native"
import { connect } from "react-redux"
import { fetchUserLocation } from "../actions"
import { StyledButton, StyledText, MonoText } from "../components/widgets"
import { SpringAppearAnimation } from "../components/animations"

class Intro extends Component<*,*> {
    _buttonPressed = () => {
        this.props.fetchLocation()
    }

    render() {
        return <View style={styles.container}>
                <View style={styles.logoBackground}>
                    <Image source={require("../../assets/images/iso-logo.png")} />
                </View>
                <StyledText style={styles.welcome}>
                    Welcome to React Native!
                </StyledText>
                <StyledText style={styles.instructions}>
                    To get started, edit <MonoText>index.js</MonoText>, <MonoText>App.js</MonoText>, and the files in <MonoText>js/</MonoText>.
                </StyledText>
                <StyledText style={styles.instructions}>
                    iOS: Press Cmd+R to reload,{"\n"}
                    iOS: Cmd+D or shake for dev menu{"\n\n"}
                    Android: Press R+R to reload,{"\n"}
                    Android: Cmd+M or shake for dev menu
                </StyledText>
                <SpringAppearAnimation>
                    <StyledButton onPress={this._buttonPressed} />
                </SpringAppearAnimation>
                <View style={styles.footer}>
                    {this.props.fetching && <ActivityIndicator size={"large"} />}
                    {this.props.encouragementVisible && !this.props.error && (
                        <SpringAppearAnimation>
                            <StyledText style={styles.welcome}>
                                {this.props.location.city}, {this.props.location.region}
                            </StyledText>
                        </SpringAppearAnimation>
                    )}
                    {this.props.error && (
                        <SpringAppearAnimation>
                            <StyledText style={styles.error}>
                                {this.props.error}
                            </StyledText>
                        </SpringAppearAnimation>
                    )}

                </View>
            </View>;
    }
}

const mapStateToProps = (state) => {
    return {
        fetching: state.location.isFetching,
        encouragementVisible: state.location.result !== null && state.location.result !== undefined,
        location: state.location.result,
        error: state.location.message
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        fetchLocation: () => dispatch(fetchUserLocation())
    }
}

export const IntroScreen = connect(mapStateToProps, mapDispatchToProps)(Intro)

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#F5FCFF"
    },
    welcome: {
        fontSize: 20,
        fontFamily: "Mukta-SemiBold",
        textAlign: "center",
        margin: 10
    },
    error: {
        fontSize: 20,
        fontFamily: "Mukta-SemiBold",
        textAlign: "center",
        color: "red",
        margin: 10
    },
    instructions: {
        textAlign: "center",
        color: "#333333",
        marginBottom: 16,
        paddingHorizontal: 32
    },
    logoBackground: {
        backgroundColor: "#f74902",
        width: "100%",
        padding: 16,
        ...Platform.select({
            ios: { paddingTop: 36 }
        }),
        top: 0,
        position: "absolute",
        alignItems: "center"
    },
    footer: {
        position: "absolute",
        bottom: 0,
        height: 100
    }
})
