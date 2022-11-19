import React, {useState} from "react";
import {Box, Button, Input} from "@chakra-ui/react";
import {JoinGameApi} from "../Apis/GameApis";
import {CreateGameResponse} from "../Types";


interface CreateGameProps {
    onRequestCreateGame(playerId: string)
}

export interface JoinGameCallback {
    onGame(game: CreateGameResponse)
}

export function JoinGame(props: JoinGameCallback) {
    const [gameId, setGameId] = useState<string>(null)
    const [playerId, setPlayerId] = useState<string>(null)
    return (<Box background="gray.200" p={10} rounded={10} width={300}>
        <Input name="gameId" backgroundColor="gray.50" borderColor="gray.400"
               placeholder="Game Id"
               onChange={(e) => {
                   setGameId(e.target.value);
               }}
        ></Input>
        <Input mt={1} name="playerId" backgroundColor="gray.50" borderColor="gray.400"
               placeholder="Player Id"
               onChange={(e) => {
                   setPlayerId(e.target.value);
               }}
        ></Input>
        <Button colorScheme="twitter" mt={5} width="100%" onClick={(event) => {
            JoinGameApi(gameId, playerId, {
                onGameCreated(response: CreateGameResponse) {
                    props.onGame(response)
                }, onError(reason: string) {
                    console.log(reason);
                }
            })
        }}>加入遊戲</Button>
    </Box>);
}


export function CreateGame(props: CreateGameProps) {
    const [playerId, setPlayerId] = useState<string>(null)
    return (<Box background="gray.200" p={10} rounded={10} width={300}>
        <Input name="playerId" backgroundColor="gray.50" borderColor="gray.400"
               placeholder="Player Id"
               onChange={(e) => {
                   setPlayerId(e.target.value);
               }}
        ></Input>
        <Button colorScheme="twitter" mt={5} width="100%" onClick={(event) => {
            props.onRequestCreateGame(playerId);
        }}>建立新遊戲</Button>
    </Box>);
}
