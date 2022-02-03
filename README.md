# Smash Character Picker (v2.0)

A program designed to pick random matchups for the game Super Smash Bros. For those of us who don't like using the random character option, because it usually results in an unfair matchup. Instead, this program uses a tier list to create a matchup that is fair. Tier lists are defined in a `.txt` file. There are some additional settings to customize the experience, as well as some systems to ensure novel matchups are generated.

## Usage

The program runs in a small window. The first order of business will be to load a tier list. Under "File", select the "Load tier list" option. Once you find your tier list file, if it is valid, the program will open it without issue. You are now ready to generate matchups! Simply hit the "Generate" button, and a matchup will be generated.

## Changing Options

There is a plethora of options to customize exactly what kind of experience you want. You can select from 2 to 8 players using the spinner next to the "Generate" button. Under "Picker" on the menu bar, you will find the option to change more settings. If desired, you have the option to force a particular tier. You can also decide whether or not all players get the same tier. If this option is unchecked, some players may end up with characters from a tier above other players. Depending on whether or not you consider this a fair matchup, you may want to leave this option checked.

As before, there are a handful of rules about the lower tiers. They are as follows:

- No tiers below C: Will select any tier from C tier up to double S tier.
- Allow D tier and above: Will use tiers D through double S tier.
- Allow D & E tiers and bove: Will use tiers E through double S tier.
- Allow all low tiers: Will use all tiers.

New in v4.0 is the option to alter the exact percentages of a particular tier being chosen. Simple set your desired percentage chances and hit the "Apply tier chance settings" button. If your chances add up to 100% (this is a requirement), then the "Use custom chances" box will be checked, and the program will now use the chances that you've defined.

The final set of settings deals with the "Cannot Get" buffer. This is detailed in the next section.

## The "Cannot Get" Buffer

In order to ensure novel matchups, the program employs what I call the "Cannot Get" buffer. Essentially, this is a buffer for each player containing the last few characters they got. Each player will not be able to get the same character for a certain number of battles. This option is changeable by the user. The size of the "Cannot Get" buffer determines the number of battles you can go before getting a repeat character.

There is also the option as to whether or not SS or S tier characters are allowed in the "Cannot Get" buffer. If your SS or S tiers are particularly small, you may not want to include them in the "Cannot Get" buffer. Or perhaps you find the best characters in the game to be the most fun, so you don't want to limit the number of times you can play them.

**Note**: You do not want to make the "Cannot Get" buffer size too big, otherwise the program may freeze. The program may encounter a situation in which there are no characters to choose from, because they're all in the "Cannot Get" buffer. In that case, the program will freeze. Be sure to set your buffer size carefully. Essentially, you want to ensure there are always enough valid characters to choose from for each player at all times.

## Tier List Format

Under the "Help" section of the menu bar, you see how the tier list must be formatted. An example tier list is also included in this repository. For posterity, the format is repeated below:

>All lines should contain comma-separated entries.
>Do not put spaces after the commas.
>Leave area after equals sign blank if N/A.
>Do not omit lines, or it will not load properly.
>Ensure that the lines are in this order, too.

>doubS=
>tierS=
>tierA=
>tierB=
>tierC=
>tierD=
>tierE=
>tierF=
>p1Exc=
>p2Exc=
>p3Exc=
>p4Exc=
>p5Exc=
>p6Exc=
>p7Exc=
>p8Exc=

Under the "doubS" and "tierX" lines, simply put a comma-separated list containing each character in that tier. The "pXExc" lines contain each player's exclusion lists: a list of fighters that that particular player never wants to get. A player will never get a fighter that's on their exclusion list.