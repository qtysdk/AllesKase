import {Box} from "@chakra-ui/react";
import React from "react";
import card_1 from './images/1.png'
import card_2 from './images/2.png'
import card_3 from './images/3.png'
import card_4 from './images/4.png'
import card_5 from './images/5.png'
import card_6 from './images/6.png'
import card_0 from './images/baelz.png'

export function CardDisplay(props: { value: number, small?: boolean, sign?: string }) {
    const {value, small, sign} = props;
    const width = small === true ? "24px" : "48px";

    const mapping = {
        0: card_0,
        1: card_1,
        2: card_2,
        3: card_3,
        4: card_4,
        5: card_5,
        6: card_6,
    }

    if (sign === "T") {
        return (<Box borderWidth="1px"><img src={mapping[value]} style={{width: width, filter: "grayscale(1)"}}/></Box>)
    } else if (sign === "C") {
        return (<Box borderWidth="1px"><img src={mapping[value]} style={{width: width}}/></Box>)
    } else {
        return (<Box borderWidth="3px"><img src={mapping[value]} style={{width: width}}/></Box>)
    }


}

export function Header() {
    return (
        <>
            <Box className="Header">玩命起司</Box>
        </>
    );
}

