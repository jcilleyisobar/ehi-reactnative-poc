// @flow 
import React from "react"
import { StyleSheet, Text } from "react-native"

type MonoTextProps = {
    style: mixed,
    children: mixed
}

export const MonoText = (props: MonoText) => (
    <Text style={[styles.customFont, props.style]}>{props.children}</Text>
);

const styles = StyleSheet.create({
    customFont: {
        fontFamily: "Courier"
    }
})
