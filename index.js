// @flow

import { AppRegistry, StatusBar } from "react-native";
import { App } from "./App";

StatusBar.setBackgroundColor("#f74902", false);

AppRegistry.registerComponent("RNTemplate", () => App);
