# Irritator

## How it works

Irritator includes two applications – bot and daemon.

Bot – telegram bot and web-service which should be hosted on web-server. It allows
users to interact with graphical interface and controls daemon.

Daemon – program which should be hosted on local machine, it plays sounds in
defined time periods and commuticate with bot's web-service through http-requests.
It plays random sounds and do not repeat it (of course, if you have multiple files
in resources directory).

## Installation

Irritator's bot and daemon were developed with `java 10.0.2` and `clojure 1.8.0`.
You just need installed `java` and `linengen` for have ability to compile project.

All compilation commands semantically defined in `Makefile`.

- `compile` – compiles `bot` and `daemon`
- `compile-bot` – compiles `bot`
- `compile-daemon` – compiles `daemon`

### Bot

Bot requires `telegram-token` property in your `config.yml` file.

The next requierd parameters is: `secret`, `allowed-usernames`, `db-host` and `db-port`.

You can read about all properties in [configuration section](#configuration).

You also can start bot with `lein` from project sources: `make run-bot`.

### Daemon

Deamon should works on your local machine, which would play sounds and
makes irritation.

It requires mp3-files in `resources` directory and timing settings.

You can redefine resources path in `resources-path`.

Timing properties fully described in [configuration section](#configuration) too.

You also can start daemon with `lein` from project sources: `make run-daemon`.

## Configuration

| Property 	      | Service  | Default value   | Description 								      |
|---------------------|----------|-----------------|----------------------------------------------------------------------------------|
| `telegram-token`    | `bot`	 | `nil`	   | Telegram bot token. Bot can't work without it! 				      |
| `secret`    	      | `bot`	 | `random-string` | Just random string which using for rejecting other requests to bot web-service   |
| `allowed-usernames` | `bot`	 | `[]`		   | Telegram users who can use bot commands 					      |
| `db-host`	      | `bot`	 | `mongo`	   | Mongo DB host 								      |
| `db-port`	      | `bot`	 | `27017`         | Mongo DB port 								      |
| `bot-url`	      | `daemon` | `nil`	   | Bot host url. Requires for access for commands queue 			      |
| `resources-path`    | `daemon` | `./resources`   | Path to local folder with mp3-files for next irritation sessions 😉 	      |
| `palyer-interval`   | `daemon` | `[5 30]`	   | How long daemon will play sound. `[5 30]` means – from 5 minutes to 30 minutes   |
| `borders`	      | `daemon` | 		   | Time when daemon can play sounds 						      |
| `borders.from`      | `daemon` | `[7 00]`	   | Time when daemon starts playing sounds. `[7 00]` means – 7:00 or 7:00 a.m.       |
| `borders.to`	      | `daemon` | `[22 00]`	   | Time when daemon stops playing any sounds. `[22 00]` means - 22:00 or 10:00 p.m. |

## Commands

Telegram bot provides to users some chat commands:

- `/help` – Prints all commands to chat
- `/start` - Starts daemon for playing sounds
- `/stop` - Stops daemon and interrupt current sound
- `/subscribe` - Subscribes for messages from bot. It send messages each time when it play something
- `/unsubscribe` - Unsubscribes from bot messages
- `/info` - Returns bot's uptime
- `/kill` - Kills bot's process. After this you should manually restart bot on your machine

