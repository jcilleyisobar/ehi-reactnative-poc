// @flow 
import React from "react"
import { StyleSheet, Text } from "react-native"

type StyledTextProps = {
    style: mixed,
    children: mixed
}

export const StyledText = (props: StyledTextProps) => (
    <Text style={[styles.customFont, props.style]}>{props.children}</Text>
)

const styles = StyleSheet.create({
    customFont: {
        fontFamily: "IsobarLight"
    }
})
