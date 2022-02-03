# Smash Character Picker (v9.0)

A program which generates a random matchup for Super Smash Bros. using a tier list. For those of us who can never decide what character to play, but are unsatisfied with the unfair matchups generated by Smash's built-in random option. Nobody wants to have a matchup like Pyra/Mythra vs. Dr. Mario, but it seems to happen all too often with the built-in random option. Thus, I created this program. It loads a tier list from a `.txt` file and generates a matchup that can be considered fair by the rules of that tier list. There are various systems included to generate novel matchups througout a session.

What's new in this version:

- Tier list files can now have comments! Simply start a line with a `#` to make a comment.
- Under the statistic lookup menu, you can now see each player's overall winrate compared with one another.
- Lots of code optimzation and improvement. Inline functions are now used for most action listeners, and the soundboard has been completely rewritten. The previous soundboard was ~700 lines, while the new one is less than 300!

## General Usage

The program is fairly simple to use. Simply load a tier list file, select the number of players, and hit the generate button to generate a matchup. Two players can swap fighters if they want to; simply check the boxes on the right side of the window corresponding to the fighters to swap, and hit the switch button. Note that it is possible to hit the switch button without having selected any fighters. Doing this has the potential to cause errors, so it is recommended that you only use the switch feature as intended: select two players and then hit the switch button.

Under normal circumstances, a player cannot get the same fighter twice in one session. There are two exceptions to this: first, battles can be skipped. If a battle is skipped, then a player will be able to get that fighter again at a later point. Second, if the fighter is part of that player's favorites list. More on what exactly that means below. The inability to get the same fighter twice (except under the previously described circumstances) is what allows the program to generate novel matchups and keep your session of Smash interesting.

### Settings

At the bottom of the window, to the right of the generate, skip, and debug buttons, you'll find the option to set the number of players present. This determines the number of fighters that will be chosen, of course.

On the left side of the window, you'll find various settings that affect the program. First, you can adjust the chance of any individual tier being selected. Note that when you adjust the tier chance settings, the values must add up to 100 and you must hit the apply button for them to take effect.

#### The "Cannot Get" buffer

Below the tier chance settings, you'll find settings for the "Cannot Get" buffer. After each battle, the fighters gotten by each player are added to the "Cannot Get" buffer, and they cannot be gotten again *by any player* for a set amount of battles. The default is 5, but it can safely be set higher than this depending on how many fighters are being used, and thus how many possible matchups there are. There are also separate settings to determine whether or not SS and S-tier characters are allowed in the buffer. If a particular tier list has a particularly small SS or S tier, it may be desirable to not put those characters in the "Cannot Get" buffer.

**Note**: If the "Cannot Get" buffer's size is set too high, then it could be possible for there to not be any available matchups, because all the fighters are in the "Cannot Get" buffer. It is important to set your "Cannot Get" buffer to a size where this cannot happen.

## Tier List Files

Tier lists are stored in a simple `.txt` file. Check out the provided tier list files for an example of how to format it. Tiers are simply denoted by the name of the tier, followed by an equals sign with a space on both sides, and then a comma-separated list of fighters that belong in that tier. Tiers can be listed in any order, and tiers can be excluded entirely if there are no fighters in that tier. Note that each tier is essentially split into three sub-tiers. There's an Upper A, Mid A, and Lower A. This applies to all tiers.

The program will search its directory for `tier list.txt` upon launch, and prompt the user as to whether or not it should be loaded, if the file is found at all. Once a tier list is loaded, the program can now be used.

New in this version is the ability to put comments in a tier list file. Simply start a line with a `#`, and the program will ignore it when loading the tier list, thus allowing the line to function as a comment.

### Defining Settings in a Tier List File

The various settings described above can also be defined in a tier list file. Once again, see one of the example tier list files to see exactly how it works. For posterity, the settings are described below.

- `players = ` will allow you to set the number of players. A number between 2 and 8 is required.
- `cannot get size = ` will allow you to set the size of the "Cannot Get" buffer. A number between 0 and 15 is required.
- `allow ss in cannot get = ` and `allow s in cannot get = ` allow you to determine those settings. A value of true or false is required.
- `tier chances = ` will allow you to set the custom tier chances automatically. Simply provide a comma-separated list of 8 numbers, and ensure they add up to 100. If so, the program will accept them as valid as soon as you open the tier list.

## Other Features

### Soundboard

The program comes with a built-in soundboard. It contains 21 sound effects that my brothers and I often reference while playing Smash. Rather than simply quoting movies, TV shows, and YouTube videos, now you can hear the exact sound effect!

### Stat Tracking

The program also comes with a built-in stat-tracking system. After a tier list file is loaded, you can open the stats window by simply hitting the button labeled 'stats', which is located at the bottom-right corner of the window. When a battle is generated, the stats window is updated. For each player, it will display their own winrate as that fighter, as well as that fighter's overall winrate.

After a battle, you can select which player won, keeping track of each player's win/loss ratio as each character. If you accidentally select the wrong winner, just select again, this time with the correct player. The program will remove the previously-entered result and only count the correct one.

#### Stat Lookup

There is also functionality to look up stats. You can look up the stats of an individual fighter, displaying each player's winrate when playing as that fighter, as well as the fighter's overall winrate. You can sort the fighters by their overall winrate, as well as their winrate when being played by a particular player. You can also sort the fighters by the total number of battles they've appeared in. Finally, you can see each player's overall winrate compared to one another.

#### Stat Modification

Did you accidentally select the wrong winner? As mentioned above, this is not a problem. Simply selecting the correct player and hitting the select winner button again will solve the problem, so long as you haven't generated a new battle yet. If you did generate a new battle, you can use the modify functionality within the lookup menu. The modify menu lets you adjust each player's individual winrate. You can also rename the character and delete them from the system, though you must also rename/remove the character in the tier list file for this to be permanent. Whenever the stats file is loaded, the tier list file is scanned, and any fighters not currently present in the stats system are added to it.

#### Stat Files

Statistics are stored in a file called `smash stats.sel`, which is stored in the same directory as the program and tier list file. The stat file simply stores the HashMap object which represents the stat system internally. This means that it is not editable with a text editor. Unfortunately, the functionality does not currently exist to load stat files other than `smash stats.sel`. I don't remember what `.sel` is supposed to stand for.

This version of the program comes with a `smash stats.sel` file. It is intended to be used with `tier list.txt`, not `reddit tier list.txt`.