// @flow
import React from "react"
import PropType from "prop-types"
import { StyleSheet, TouchableOpacity, View } from 'react-native'
import { StyledText } from "./StyledText"

const StyledButtonProps = {
    title: PropType.string
}

export const StyledButton = (props: typeof StyledButtonProps) => (
    <TouchableOpacity {...props}>
        <View style={styles.button}>
            <StyledText style={styles.buttonText}>{props.title}</StyledText>
        </View>    
    </TouchableOpacity>
)

StyledButton.defaultProps = {
    title: "Push Me!"
}

StyledButton.propTypes = StyledButtonProps

const styles = StyleSheet.create({
    button: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        padding: 24,
        backgroundColor: "#f74902"
    },
    buttonText: {
        color: "#fff",
        fontFamily: "Mukta-SemiBold"        
    }
})
