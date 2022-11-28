import React, {useEffect, useState} from 'react';
import './App.css';
import {Box, Button, Center, Text, Flex, Input, Spacer} from "@chakra-ui/react";
import {CreateGame, JoinGame} from "./Components/BeforeGameIdAvailable";
import {CardActions, CreateGameResponse, EventOutput, GameView, Player} from "./Types";
import {CardDisplay, Header} from './Components/Common';
import {CreateGameApi, GetGameView, ListAvailableGameIds, PlayActionApi} from "./Apis/GameApis";
import * as CSS from "csstype";


interface CreateOrJoinProps {
    // TODO confused parameter => CreateGameResponse
    setGame(game: CreateGameResponse)

    gameIds: Array<string>
}

function CreateOrJoinComponent(props: CreateOrJoinProps) {
    return (
        <Flex flexDirection="row" mt="128px">
            <CreateGame onRequestCreateGame={(playerId) => {
                CreateGameApi(playerId, {
                    onGameCreated(response: CreateGameResponse) {
                        props.setGame(response);
                    }, onError(reason: string) {
                        console.log(reason);
                    }
                });
            }}/>

            <Box width={10}></Box>
            <JoinGame onGame={props.setGame} gameIds={props.gameIds}/>
        </Flex>
    )
}

interface GameViewProps {
    game: CreateGameResponse
    gameView: GameView
}

function PlayerZoneComponent(props: { player: Player, turnPlayer: Player }) {
    const {player, turnPlayer} = props
    let color = "blue.100"
    if (player.playerId === turnPlayer.playerId) {
        color = "red.100"
    }

    return (
        <Box backgroundColor="gray.100" width="250px" height="150px" key={player.playerId} mb={5} rounded={10}
             shadow="xl">
            <Flex>
                <Box p={2} pl={4} backgroundColor={color}>{player.playerId}</Box>
                <Spacer backgroundColor={color}></Spacer>
                <Box p={2} pr={4} backgroundColor={color} roundedTopRight={20} fontWeight={500}># {player.score}</Box>
            </Flex>
            <Box p={2}>
                <Flex>
                    {player.keptCards.split(",").map(c => {
                        return <Box m={1}><CardDisplay value={parseInt(c[0])} small={true}
                                                       sign={c[1]}></CardDisplay></Box>
                    })}
                </Flex>
                <Text mt={2}>狀態: {player.alive ? '存活' : '淘汰'}</Text>

            </Box>
        </Box>
    )
}

function PlayActionButton(props: { index: number, actions: Array<string>, onActionTake: (i: number, a: string) => void }) {
    const {index, actions, onActionTake} = props;
    return <>
        <Flex fontSize="10px" flexDirection="column">
            {actions.map(action => {
                return <Center>
                    <Box m={1} width="40px" rounded={5} style={{cursor: "pointer"}}
                         backgroundColor="black" color="white"
                         onClick={(e) => {
                             onActionTake(index, action)
                         }
                         }><Center>{action}</Center></Box>
                </Center>
            })}
        </Flex>
    </>
}

function DemoZoneCards(props: { demoZone: Array<CardActions>, isTurnPlayer: boolean, gameView: GameView, onActionTake: (i: number, a: string) => void }) {
    const {demoZone, gameView, isTurnPlayer, onActionTake} = props;
    return <Flex>
        {
            demoZone.map(cardAction => {
                return <Flex m={1} flexDirection="column">
                    <CardDisplay value={cardAction.value}/>
                    {cardAction.actions &&
                        <PlayActionButton index={cardAction.index} actions={cardAction.actions}
                                          onActionTake={onActionTake}/>
                    }
                </Flex>
            })
        }
    </Flex>
}

function Window(props: {
    title: React.ReactNode,
    color: CSS.Property.Color,
    width?: CSS.Property.Width,
    height?: CSS.Property.Height
    children: React.ReactNode
}) {
    const {color, title, children} = props
    const width = props.width == null ? "250px" : props.width
    const height = props.height == null ? "150px" : props.height

    return <Box backgroundColor="gray.100" width={width} height={height} mb={5} rounded={10}
                shadow="xl" mr={5}>
        <Flex>
            <Box p={2} pl={4} backgroundColor={color}>{title}</Box>
            <Spacer backgroundColor={color}></Spacer>
            <Box p={2} pr={4} backgroundColor={color} roundedTopRight={20} fontWeight={500}></Box>
        </Flex>
        <Box>
            {children}
        </Box>
    </Box>
}

function DemoAndDecks(props: { game: CreateGameResponse, gameView: GameView }) {
    const {game, gameView} = props
    let color = "gray.300"
    return (<Box flexDirection="row" mt={5}>
        <Flex>
            <Window color={color} title={`供應牌堆 (${gameView.providingDeck.numberOfCards})`}>
                <Box>
                    <CardDisplay value={gameView.providingDeck.value}/>
                </Box>
            </Window>
            <Window color={color} title={`棄牌堆 (${gameView.droppedDeck.numberOfCards})`}>
                <Box>
                    <CardDisplay value={gameView.droppedDeck.value}/>
                </Box>
            </Window>
            <Window color={color} title="展示區" width="500px">
                <DemoZoneCards demoZone={gameView.demoZone} gameView={gameView}
                               isTurnPlayer={game.playerId === gameView.turn.player.playerId}
                               onActionTake={
                                   (index, action) => {
                                       PlayActionApi(game.gameId, game.playerId, action, index)
                                   }
                               }
                />
            </Window>
        </Flex>
    </Box>)
}


function GameViewComponent(props: GameViewProps) {
    const {game, gameView} = props;
    if (!gameView) {
        return <></>
    }

    const eventFilter = (events: Array<EventOutput>) => {
        return events.filter((input) => {
            switch (input.type) {
                case "DEMO_ZONE_CHANGED":
                    return false
                case "GAME_CHANGE_TURN_PLAYER":
                    return false
                case "PLAYER_DID_KEEP":
                    return false
                case "SCORE_LIST_ANNOUNCED":
                    // 分數列表
                    return false
                default:
                    return true
            }
        });
    }

    const cardInfo = (c: string) => {
        const type = c[1] === "T" ? "陷阱" : "起司"
        return `${c[0]}點的${type}`
    }

    const EventLog = (props: { evt: EventOutput, verb: string, children: React.ReactNode }) => {
        const {evt, verb, children} = props
        return <>{evt.playerId} {verb}
            <Text as="span" backgroundColor="cyan.200" ml="2px">{children}</Text></>
    }

    const eventTranslate = (events: Array<EventOutput>) => {
        return events.map(evt => {
            switch (evt.type) {
                case "GAME_STARTED":
                    return "遊戲已經開始囉！"
                case "GAME_ENDED":
                    return "遊戲已經結束惹！"
                case "PLAYER_DID_DROP":
                    return <EventLog evt={evt} verb="丟棄">位置 {parseInt(evt.data) + 1} 的牌</EventLog>
                case "PLAYER_PRIVATE_PEEP_DATA":
                    return <EventLog evt={evt} verb="偷看到">{cardInfo(evt.data)}</EventLog>
                case "PLAYER_DID_PEEP":
                    return <EventLog evt={evt} verb="偷看的是">位置 {parseInt(evt.data) + 1} 的牌</EventLog>
                case "GAME_HAS_WINNER":
                    return <Text as="span" backgroundColor="yellow">{evt.playerId} 贏辣！！！</Text>
                case "GAME_CHANGE_TURN_PLAYER":
                    return <Text as="span" fontSize="8pt">等待 {evt.playerId} 出牌</Text>

            }
            return JSON.stringify(evt)
        })
    }


    return <Box width="100vw" style={{paddingTop: "48px"}}>
        <Flex>
            <Box minWidth="300px" padding={5}>
                <Box>遊戲歷程</Box>
                <Box fontSize="10pt" maxHeight="400px" style={{scrollBehavior: "auto"}}>
                    {gameView.events.length == 0 && <li>遊戲還沒有開始喔！</li>}
                    {eventTranslate(eventFilter(gameView.events)).map(e => {
                        return <><Box p="2px">* {e}</Box></>
                    })}
                </Box>
            </Box>
            <Box width="100%">
                <DemoAndDecks game={game} gameView={gameView}/>
                <Box>
                    {gameView.players.map(player => {
                        return <PlayerZoneComponent player={player} turnPlayer={gameView.turn.player}/>
                    })}
                </Box>

                <Box mb={10}>

                    <Box>
                        <li>Game Id：{gameView.gameId} </li>
                        <li>骰子點數：{gameView.turn.diceValue}</li>
                    </Box>

                </Box>

            </Box>

        </Flex>

    </Box>;
}

function App() {
    const [game, setGame] = useState<CreateGameResponse>(null);
    const [gameView, setGameView] = useState<GameView>(null);
    const [refresher, setRefresher] = useState<boolean>(false);
    const [gameIds, setGameIds] = useState<Array<string>>(null);

    const refreshGameView = () => {
        GetGameView(game?.gameId, game?.playerId, {
            onGameView(view: GameView) {
                setGameView(view)
            },
            onError(reason: string) {
                console.log(reason);
            }
        })
    }

    useEffect(() => {
        if (game != null && refresher === false) {
            setRefresher(true);
            console.log("setUp game-view refresher");
            setInterval(refreshGameView, 1000)
        }
    }, [game]);

    useEffect(() => {
        if (game == null) {
            return;
        }
    }, [gameView])

    useEffect(() => {
        if (game != null) {
            return;
        }
        ListAvailableGameIds({
            onGameIdsAvailable(response: { gameIds: Array<string> }) {
                setGameIds(response.gameIds)
                return
            }
        });
    }, [])


    return (
        <>
            <Flex background="gray.50"
                  height="100vh"
                  alignItems="center"
                  flexDirection="column">
                <Header/>

                <GameViewComponent game={game} gameView={gameView}/>
                {gameView == null &&
                    <CreateOrJoinComponent setGame={setGame} gameIds={gameIds}/>
                }
            </Flex>
        </>
    );
}

export default App;
