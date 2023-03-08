# Smash Character Picker (v12)

A program that generates a random matchup for *Super Smash Bros.* using a tier list. For those of us who can never decide what character to play, but are unsatisfied with Smash's built-in random option. It always seems to generate totally unfair matchups. To solve this problem, I created this program. It loads a tier list from a file and generates a matchup that can be considered fair by the rules of that tier list. There are various systems included to generate novel matchups throughout a session. There is also a built-in stat-tracking system, allowing you to track the winrates of players and fighters.

This version of the program (v12) sees a more or less total rewrite of the program. I'm actually taking some advantage of object-oriented programming now! This software has been around since 2019, and I believe it continues to get better over time.

## General Usage

Upon launching the program, load a tier list file using the "Load" button. If there is a file named `tier list.txt` in the same directory as the `.jar` file, the program will prompt you as to whether you want to load it automatically. If the file loaded is valid, then you're ready to go. Hit the "Generate" button to generate a matchup. Two players can swap fighters if they want to; simply check the two boxes to right of the results panel corresponding to the fighters to swap, and hit the "Switch" button.

Under normal circumstances, a player cannot get the same fighter twice in one session. There are two exceptions to this: first, battles can be skipped using the "Skip" button, which is to the right of the "Generate" button. If a battle is skipped, then a player will be able to get that fighter again at a later point. The second exception is if the fighter is part of that player's favorites list. More on what exactly that means below. The inability to get the same fighter twice (except under the previously described circumstances) is what allows the program to generate novel matchups and keep your session of Smash interesting.

### Settings

At the bottom of the window, to the right of the generate, skip, and debug buttons, you'll find the option to set the number of players present. This determines the number of fighters that will be chosen, of course.

#### Tier Chances

On the left side of the window, you'll find various settings that affect the program. First, you can adjust the chance of any individual tier being selected. Note that when you adjust the tier chance settings, the values must add up to 100 and you must hit the "Apply tier chance settings" button for them to take effect. You'll know that your percentages have taken effect because the program will produce a dialog box telling you so.

#### Bump Chances

Below the tier chance settings is the bump settings. When a battle generated, a tier is chosen according to the percentages defined by the tier chance settings. However, each player has a chance to be bumped *up* either one or two sub-tiers. For example, the chosen tier may be Mid A tier, but players could be bumped up to Upper A or Lower S tiers. The chances of being bumped up one or two tiers, or simply staying in the same tier that was initially chosen, is defined by the bump chances. These are also applied by hitting the "Apply tier chance settings" button, and like the tier chances, these values must add up to 100. Tiers are always bumped *up*, never down, except when there are not enough tiers to bump upward, i.e. the top tier or second to top tier was chosen as the initial tier. When the top tier (Upper Double S tier) is selected, tiers can be bumped down one or two tiers. When the second-to-top tier (Mid Double S tier) is chosen, tiers can be bumped up or down one.

#### The "Cannot Get" buffer

Below the tier chance settings, you'll find settings for the "Cannot Get" buffer. After each battle, the fighters that each player got are added to the "Cannot Get" buffer, and they cannot be gotten again *by any player* for a set amount of battles. This is the size of the "Cannot Get" buffer. The default is 5, but it can safely be set higher than this depending on how many fighters are being used, and thus how many possible matchups there are. There are also separate settings to determine whether or not SS and/or S tier characters are allowed in the buffer. If a tier list has a particularly small SS or S tier, it may be desirable to not put those characters in the "Cannot Get" buffer.

**Note**: If the "Cannot Get" buffer's size is set too high, then it could be possible for there to not be any available matchups, because all the fighters are in the "Cannot Get" buffer. In this situation, the program will fail to generate a matchup. "No valid battles found after 100 tries" will be printed to the results field. It is important to set your "Cannot Get" buffer to a size where this will not happen. If this does happen, you are best off simply closing the program and restarting it, being sure to set the "Cannot Get" buffer size lower. Note that with enough use of the program, you will eventually run out of valid matchups, as players will have already gotten every fighter they can. As such, the number of battles you can get before running out of valid options is *approximately* equal to the smallest number of valid fighters for a player on your tier list. For example, if your tier list has 82 fighters on it, but a player has 5 fighters on their exclusion list, you can expect to get a max of ~77 battles. In practice, it will likely be less than that due to various factors such as the number of fighters in each tier and pure luck based on the matchups generated.

## Tier List Files

Tier lists are stored in a `.txt` file. Check out the provided tier list files for an example of how to format it. Tiers are denoted by the name of the tier, followed by an equals sign with a space on both sides, and then a comma-separated list of fighters that belong in that tier. Tiers can be listed in any order, and tiers can be excluded entirely if there are no fighters in that tier for your particular tier list. However, the tiers must be placed before the definitions for exclusion and favorites lists. If a fighter is on an exclusion/favorite list that wasn't defined in a tier before it, an error will be thrown.

The program will search its directory for `tier list.txt` upon launch, and prompt the user as to whether or not it should be loaded, if the file is found at all. Once a tier list is loaded, the program can be used.

Any line that is not recognized as a tier, setting, or exclusion/favorite list will be ignored, so you can include whatever text you want on lines that don't contain tier list data. However, lines that do contain data must be formatted correctly.

### Exclusion and Favorite Lists

Each player gets their own exclusion and favorite lists. The exclusion list is simply a list of fighters that a particular player *never* wants to be able to get. For those fighters that you just can't stand. Under no circumstances will a player be able to get a fighter that's on their exclusion list.

The favorite list, on the other hand, is a list of that player's favorite fighters. As described above, normally a player can never get the same fighter twice in the same session. One exception to that is if that fighter is on that player's favorite list. Note that you'll still have to wait however many battles you've set the "Cannot Get" buffer to before you can get the same fighter for a second time.

### Defining Settings in a Tier List File

The various settings described above can also be defined in a tier list file. Once again, see one of the example tier list files to see exactly how it works. The settings are also described below.

- `players = ` will allow you to set the number of players. A number between 2 and 8 is required.
- `cannot get size = ` will allow you to set the size of the "Cannot Get" buffer. A number between 0 and 15 is required.
- `allow ss in cannot get = ` and `allow s in cannot get = ` allow you to determine those settings. A value of true/false or 0/1 is required.
- `tier chances = ` will allow you to set the custom tier chances automatically. Simply provide a comma-separated list of 8 numbers, and ensure they add up to 100. The numbers are the percentages of each tier, starting from SS down to F.
- `bump chances = ` will allow you to set custom bump chances automatically. Simple provide a comma-separated list of 3 numbers; the chance of staying the same tier, bumping up one tier, and bumping up two tiers. These values must add up to 100.

## Stat Tracking

The program comes with a built-in stat-tracking system. The stats panel is located on the right side of the window. When a battle is generated, the stats window is updated. For each player, it will display their own win/loss ratio as that fighter, as well as that fighter's overall win/loss ratio.

After a battle, you can select which player won, keeping track of each player's win/loss ratio as each character. If you accidentally select the wrong winner, simply reselect with the correct winner. The program will remove the previously-entered result and only count the correct one.

### Stat Search

You can look up the stats of an individual fighter, displaying each player's winrate when playing as that fighter, as well as the fighter's overall winrate. Simply hit the "Search" button in the stats panel and type in the name of the fighter you want to look up.

### Stat Sorting

You can sort the fighters by their overall winrate, as well as their winrate when being played by a particular player. You can also sort the fighters by the total number of battles they've appeared in. Finally, you can see each player's overall winrate compared to one another.

### Stat Files

Statistics are stored in a file called `smash stats.sel`, which is stored in the same directory as the program and tier list file. The stats file simply stores the HashMap object which represents the stats system internally. This means that it is not editable with a text editor. The Smash Character Picker program is only designed to read `smash stats.sel`.

If you want to read `.sel` files other than `smash stats.sel`, you can use the [Smash Stats Viewer](https://github.com/jordanknapp00/Smash-Stats-Viewer). This program allows read-only access to `.sel` files, so you can read various stats files without making modifications.

This version of the program comes with a `smash stats.sel` file that contains some example data.
