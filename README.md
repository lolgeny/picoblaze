# Replace

A small fabric mod (for the 1.17 snapshots) which adds two new commands,
used for dynamically building and running commands.

It may be buggy
(if you find a bug, I'd greatly appreciate a bug report)
and is a lot of an experiment to see if these features
would be useful in the vanilla game.
It's part of a series, the first being [game events](https://github.com/lolgeny/game_event).

Of course, I'd also appreciate ideas and feedback too.

## Replacements

We can use the new `execute replace` subcommand to replace a variable in the following command.
In this command, we reference it using a `$` before it.
For example,
```
execute replace my_score score @s foo run say $my_score
```
acts very similar to a
```
tellraw @a {"score":{"name":"@s","objective":"foo"}}
```

Replacements are stored in `execute` context,
just like `as` and `at`. This means, if we
```
execute replace ... run function ...
```
we can use replacements inside the function!

Inside the function, to mark a function as not being checked when the pack is loaded
(a `$foo` would cause a syntax error),
we can use the command `eval`.

For example, a simple add function could be

```
scoreboard objectives add global dummy
eval say $a, $b
eval scoreboard players set #a global $a
eval scoreboard players add #a global $b
execute replace result score #a global run say $result
```

and would be called

```
execute replace a ... replace b ... run function foo:add
```


## Commands

- `eval <command>`, a command which simply runs another command
  dynamically, not performing datapack-time load checks. Useful for
  using replacements inside a function.
- `execute replace <variable> <text|score|data> ...`, an execute subcommand that sets replacements in the
  context. The rest of the command will be dynamically evaluated, as if
  being proceeded by `eval`.
  
## Types of replacement

### `text`
This accepts a JSON text component,
evaluates it,
flattens it,
then sets the variable to the result.
```
execute replace command text {"text":"say Flattened!"} run $command
```

### `data`
This accepts an nbt location and path,
using the same format as `data get`.

After the path, there's an `<interpret>` field which acts like the one in text components.
```
execute replace pos data entity @s Pos false run say I'm at $pos.
```

### `score`
This takes in a score
(in the same format as `scoreboard players get`)
and sets the variable to it.
```
execute replace my_score score @s foo run say My score is $my_score.
```

## A note on syntax highlighting
Unfortunately, after an `eval` or `execute replace`,
syntax highlighting (in chat or in whatever editor you use)
will stop working - there's no way the game can infer what sort of things the variables will be expanded to.