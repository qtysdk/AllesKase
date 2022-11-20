import React, {useEffect, useState} from 'react';
import './App.css';
import {Box, Button, Center, Flex, Input} from "@chakra-ui/react";
import {CreateGame, JoinGame} from "./Components/BeforeGameIdAvailable";
import {CreateGameResponse, GameView, Player} from "./Types";
import {Header} from './Components/Common';
import {CreateGameApi, GetGameView, PlayActionApi} from "./Apis/GameApis";

interface CreateOrJoinProps {
    setGame(game: CreateGameResponse)
}

function CreateOrJoinComponent(props: CreateOrJoinProps) {
    return (
        <Flex flexDirection="row">
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
            <JoinGame onGame={props.setGame}/>
        </Flex>
    )
}

interface GameViewProps {
    game: CreateGameResponse
    gameView: GameView
}

interface PlayerZoneProps {
    player: Player
}

function PlayerZoneComponent(props: PlayerZoneProps) {
    const {player} = props
    return (
        <Box backgroundColor="red.100" width="99vw" key={player.playerId} mb={5}>
            <li>玩家ID: {player.playerId} {' '}</li>
            <li>分數: {player.score}</li>
            <li>狀態: {player.alive ? '存活' : '淘汰'}</li>
            <li>持有卡片: {player.keptCards}</li>
        </Box>
    )
}

function GameViewComponent(props: GameViewProps) {
    const {game, gameView} = props;
    if (!gameView) {
        return <></>
    }

    const turn = gameView.turn;
    const availabeActions = []
    if (turn.player.playerId == game.playerId) {
        // turn.actionList.map(action => {action: action, index3: turn.actionIndex})
        turn.actionList.forEach(action => {
            turn.actionIndex.forEach(cardIndex => {
                availabeActions[availabeActions.length] = {action: action, index: cardIndex}
            })
        })
    }

    return <Box>

        <Box>
            {gameView.players.map(player => {
                return <PlayerZoneComponent player={player}/>
            })}
        </Box>

        <Box mb={10}>
            <Box>
                <li>Game Id：{gameView.gameId} </li>
                <li>展示區：{JSON.stringify(gameView.demoZone)} </li>
                <li>供應牌資料：{JSON.stringify(gameView.providingDeck)}</li>
                <li>棄牌堆資料：{JSON.stringify(gameView.droppedDeck)}</li>
                <li>你的 Player Id：{game.playerId}</li>
                <li>目前玩家：{gameView.turn.player.playerId}</li>
                <li>是 Host?：{gameView.players[0].playerId == game.playerId ? 'yes' : 'no'}</li>
                <li>骰子點數：{gameView.turn.diceValue}</li>
                <li>可以選擇的動作：{gameView.turn.actionList}</li>
                <li>可以選擇的位置：{gameView.turn.actionIndex}</li>
            </Box>
            <Box height={150} backgroundColor="blue.50" p={5}>
                {
                    availabeActions.map(x => {
                        return <Button colorScheme="facebook" m={1} onClick={(e) => {
                            PlayActionApi(game.gameId, game.playerId, x.action, x.index)
                        }
                        }>{JSON.stringify(x)}</Button>
                    })
                }

            </Box>
        </Box>

    </Box>;
}

function App() {
    const [game, setGame] = useState<CreateGameResponse>(null);
    const [gameView, setGameView] = useState<GameView>(null);
    const [refresher, setRefresher] = useState<boolean>(false);

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
        console.log(`view: ${gameView}`);
        console.log(gameView);
    }, [gameView])


    return (
        <>
            <Flex background="gray.50"
                  height="100vh"
                  justifyContent="center"
                  alignItems="center"
                  flexDirection="column">
                <Header/>

                <GameViewComponent game={game} gameView={gameView}/>
                {gameView == null &&
                    <CreateOrJoinComponent setGame={setGame}/>
                }
            </Flex>
        </>
    );
}

export default App;
